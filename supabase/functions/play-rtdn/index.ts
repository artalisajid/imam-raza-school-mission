import {createClient} from "npm:@supabase/supabase-js@2.57.0";
import {createRemoteJWKSet,jwtVerify,SignJWT,importPKCS8} from "npm:jose@5.10.0";
// Google Cloud Pub/Sub authenticated push endpoint; rejects unconfigured/unsigned requests.
const respond=(status:number,v:object)=>new Response(JSON.stringify(v),{status,headers:{"content-type":"application/json","cache-control":"no-store"}});
const jwks=createRemoteJWKSet(new URL("https://www.googleapis.com/oauth2/v3/certs"));
async function sha(s:string){return Array.from(new Uint8Array(await crypto.subtle.digest("SHA-256",new TextEncoder().encode(s)))).map(x=>x.toString(16).padStart(2,"0")).join("");}
async function playAccess(){
 const raw=Deno.env.get("GOOGLE_PLAY_SERVICE_ACCOUNT_JSON");if(!raw)throw Error("play_unconfigured");
 const sa=JSON.parse(raw),key=await importPKCS8(sa.private_key,"RS256"),now=Math.floor(Date.now()/1000);
 const jwt=await new SignJWT({scope:"https://www.googleapis.com/auth/androidpublisher"})
  .setProtectedHeader({alg:"RS256",typ:"JWT"}).setIssuer(sa.client_email).setSubject(sa.client_email)
  .setAudience("https://oauth2.googleapis.com/token").setIssuedAt(now).setExpirationTime(now+3400).sign(key);
 const r=await fetch("https://oauth2.googleapis.com/token",{method:"POST",headers:{"content-type":"application/x-www-form-urlencoded"},
  body:new URLSearchParams({grant_type:"urn:ietf:params:oauth:grant-type:jwt-bearer",assertion:jwt})});
 if(!r.ok)throw Error("play_token_unavailable");return (await r.json()).access_token;
}
Deno.serve(async(req:Request)=>{
 if(req.method!=="POST")return respond(405,{error:"method_not_allowed"});
 const audience=Deno.env.get("PLAY_PUBSUB_PUSH_AUDIENCE");
 const email=Deno.env.get("PLAY_PUBSUB_PUSH_EMAIL");
 const url=Deno.env.get("SUPABASE_URL"),role=Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
 if(!audience||!email||!url||!role)return respond(503,{error:"push_not_configured"});
 const jwt=req.headers.get("authorization")?.match(/^Bearer (.+)$/i)?.[1];
 if(!jwt)return respond(401,{error:"authentication_required"});
 try{
  const {payload}=await jwtVerify(jwt,jwks,{issuer:["accounts.google.com","https://accounts.google.com"],audience,algorithms:["RS256"]});
  if(payload.email!==email||payload.email_verified!==true)return respond(403,{error:"unknown_push_identity"});
 }catch{return respond(401,{error:"invalid_push_identity"});}
 let notice:Record<string,unknown>;
 try{
  const body=await req.json();
  const b64=body?.message?.data;
  if(typeof b64!=="string"||b64.length>20_000)return respond(400,{error:"invalid_payload"});
  notice=JSON.parse(atob(b64));
 }catch{return respond(400,{error:"invalid_notification"});}
 if(notice.packageName!=="org.imamraza.schoolmission")return respond(400,{error:"wrong_package"});
 const item=notice.oneTimeProductNotification as {purchaseToken?:string,notificationType?:number,sku?:string}|undefined;
 if(!item||![1,2].includes(item.notificationType??0))return respond(200,{ignored:true});
 if(typeof item.purchaseToken!=="string"||item.purchaseToken.length<16||item.purchaseToken.length>4000||
    typeof item.sku!=="string"||!/^[a-z0-9_]{3,100}$/.test(item.sku))
    return respond(400,{error:"invalid_purchase"});
 try{
  const access=await playAccess();
  const endpoint="https://androidpublisher.googleapis.com/androidpublisher/v3/applications/org.imamraza.schoolmission/purchases/productsv2/tokens/"+encodeURIComponent(item.purchaseToken);
  const r=await fetch(endpoint,{headers:{authorization:"Bearer "+access}});
  if(!r.ok)throw Error("purchase_state_unavailable");
  const google=await r.json();
  if(!Array.isArray(google.productLineItem)||google.productLineItem.length!==1||
     google.productLineItem[0]?.productId!==item.sku)
     return respond(422,{error:"product_mismatch"});
  // New purchases are always bound to the signed-in buyer in verify-play-purchase.
  // Cancellation may revoke an existing entitlement if Google no longer reports PURCHASED.
  if(item.notificationType===2 && google.purchaseStateContext?.purchaseState!=="PURCHASED"){
   const db=createClient(url,role,{auth:{persistSession:false,autoRefreshToken:false}});
   const {error}=await db.rpc("revoke_verified_google_purchase",{p_token_hash:await sha(item.purchaseToken)});
   if(error)throw Error("revocation_unavailable");
  }
  return respond(200,{received:true});
 }catch(e){console.error("RTDN retryable failure",e instanceof Error?e.message:"unknown");
  return respond(502,{error:"processing_unavailable"});}
});
