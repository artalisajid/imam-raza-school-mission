// Finance-admin initiated refund/chargeback reconciliation against Google's
// authoritative Voided Purchases API. Requires Play Console service account
// credentials in Supabase Edge secrets. No public access.
import { createClient } from "npm:@supabase/supabase-js@2.57.0";
import { importPKCS8, SignJWT } from "npm:jose@5.10.0";
const out=(status:number,payload:object)=>new Response(JSON.stringify(payload),{
 status,headers:{"content-type":"application/json","cache-control":"no-store"}
});
const digest=async (s:string)=>Array.from(new Uint8Array(
 await crypto.subtle.digest("SHA-256",new TextEncoder().encode(s))
)).map(x=>x.toString(16).padStart(2,"0")).join("");
async function getGoogleAccess():Promise<string>{
 const secret=Deno.env.get("GOOGLE_PLAY_SERVICE_ACCOUNT_JSON");
 if(!secret)throw Error("not_configured");
 const svc=JSON.parse(secret);
 if(typeof svc?.client_email!=="string"||typeof svc?.private_key!=="string")
  throw Error("not_configured");
 const key=await importPKCS8(svc.private_key,"RS256");
 const now=Math.floor(Date.now()/1000);
 const jwt=await new SignJWT({scope:"https://www.googleapis.com/auth/androidpublisher"})
 .setProtectedHeader({alg:"RS256",typ:"JWT"})
 .setIssuer(svc.client_email).setSubject(svc.client_email)
 .setAudience("https://oauth2.googleapis.com/token")
 .setIssuedAt(now).setExpirationTime(now+3400).sign(key);
 const resp=await fetch("https://oauth2.googleapis.com/token",{method:"POST",headers:{
 "content-type":"application/x-www-form-urlencoded"
 },body:new URLSearchParams({
 grant_type:"urn:ietf:params:oauth:grant-type:jwt-bearer",assertion:jwt
 })});
 if(!resp.ok)throw Error("play_authorization_failed");
 const json=await resp.json();
 return json.access_token as string;
}
Deno.serve(async req=>{
 if(req.method!=="POST")return out(405,{error:"method_not_allowed"});
 const url=Deno.env.get("SUPABASE_URL"),serviceKey=Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
 if(!url||!serviceKey)return out(503,{error:"backend_unavailable"});
 const token=req.headers.get("authorization")?.match(/^Bearer (.+)$/i)?.[1];
 if(!token)return out(401,{error:"authentication_required"});
 const db=createClient(url,serviceKey,{auth:{autoRefreshToken:false,persistSession:false}});
 const {data:{user},error:userError}=await db.auth.getUser(token);
 if(userError||!user)return out(401,{error:"invalid_session"});
 const {data:role,error:roleError}=await db.from("admin_memberships")
 .select("role").eq("user_id",user.id).maybeSingle();
 if(roleError||!role||!["owner","finance_approver","finance_officer"].includes(role.role))
  return out(403,{error:"finance_admin_required"});
 if(!Deno.env.get("GOOGLE_PLAY_SERVICE_ACCOUNT_JSON"))
  return out(503,{error:"publisher_not_configured"});
 // Always re-scan Google Play's maximum 30-day voided order visibility,
 // rather than trusting event arrival or a client-supplied token.
 const oldest=Date.now()-29*24*60*60*1000,latest=Date.now();
 try{
  const access=await getGoogleAccess();
  let next:string|undefined;let seen=0,revoked=0,unmatched=0;
  for(let page=0;page<10;page++){
   const params=new URLSearchParams({startTime:String(oldest),
    endTime:String(latest),type:"0","pageSelection.maxResults":"1000"});
   if(next)params.set("pageSelection.token",next);
   const reqUrl="https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"
    +"org.imamraza.schoolmission/purchases/voidedpurchases?"+params;
   const google=await fetch(reqUrl,{headers:{authorization:"Bearer "+access}});
   if(!google.ok)throw Error("play_voided_lookup_failed");
   const body=await google.json();
   const orders=body.voidedPurchases??[];
   if(!Array.isArray(orders))throw Error("invalid_voided_response");
   for(const order of orders){
    if(typeof order.purchaseToken!=="string")continue;
    seen++;
    const tokenHash=await digest(order.purchaseToken);
    const {data,error}=await db.rpc("revoke_verified_google_purchase",{
     p_token_hash:tokenHash
    });
    if(error)throw Error("revocation_failed");
    if(data)revoked++;else unmatched++;
   }
   next=body.tokenPagination?.nextPageToken;
   if(!next)break;
   if(page===9)throw Error("voided_reconciliation_incomplete");
  }
  await db.from("audit_events").insert({
   actor_id:user.id,event_name:"google_play_voided_purchase_reconciliation",
   entity_type:"google_play",entity_id:"org.imamraza.schoolmission",
   details:{window_days:29,checked:seen,matched:revoked,unmatched}
  });
  return out(200,{status:"completed",checked:seen,matched:revoked,unmatched});
 }catch(e){
  console.error("Void reconciliation unsuccessful",e instanceof Error?e.message:"unknown");
  return out(502,{error:"reconciliation_unavailable"});
 }
});
