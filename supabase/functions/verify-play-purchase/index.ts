import {createClient} from "npm:@supabase/supabase-js@2.57.0";
import {importPKCS8,SignJWT} from "npm:jose@5.10.0";
// Mandatory: GOOGLE_PLAY_SERVICE_ACCOUNT_JSON must be set in Supabase secrets;
// the service account needs Play Console permission to verify and acknowledge products.
const packageName="org.imamraza.schoolmission";
const json=(status:number,value:unknown)=>new Response(JSON.stringify(value),{status,headers:{"content-type":"application/json","cache-control":"no-store"}});
const hash=async(text:string)=>Array.from(new Uint8Array(await crypto.subtle.digest("SHA-256",new TextEncoder().encode(text)))).map(x=>x.toString(16).padStart(2,"0")).join("");
async function playToken(){
 const raw=Deno.env.get("GOOGLE_PLAY_SERVICE_ACCOUNT_JSON");
 if(!raw) throw new Error("publisher_not_configured");
 const sa=JSON.parse(raw);
 const privateKey=await importPKCS8(sa.private_key,"RS256");
 const now=Math.floor(Date.now()/1000);
 const jwt=await new SignJWT({scope:"https://www.googleapis.com/auth/androidpublisher"})
  .setProtectedHeader({alg:"RS256",typ:"JWT"}).setIssuer(sa.client_email)
  .setSubject(sa.client_email).setAudience("https://oauth2.googleapis.com/token")
  .setIssuedAt(now).setExpirationTime(now+3500).sign(privateKey);
 const r=await fetch("https://oauth2.googleapis.com/token",{
  method:"POST",headers:{"content-type":"application/x-www-form-urlencoded"},
  body:new URLSearchParams({grant_type:"urn:ietf:params:oauth:grant-type:jwt-bearer",assertion:jwt})
 });
 if(!r.ok) throw new Error("publisher_auth_failure");
 return (await r.json()).access_token as string;
}
async function playGet(token:string,access:string){
 const r=await fetch("https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"+packageName+"/purchases/productsv2/tokens/"+encodeURIComponent(token),{headers:{Authorization:"Bearer "+access}});
 if(!r.ok) throw new Error("publisher_lookup_failure");
 return r.json();
}
async function playAcknowledge(sku:string,token:string,access:string){
 const r=await fetch("https://androidpublisher.googleapis.com/androidpublisher/v3/applications/"+packageName+"/purchases/products/"+encodeURIComponent(sku)+"/tokens/"+encodeURIComponent(token)+":acknowledge",{method:"POST",headers:{Authorization:"Bearer "+access,"content-type":"application/json"},body:"{}"});
 return r.ok;
}
Deno.serve(async(req:Request)=>{
 if(req.method!=="POST") return json(405,{error:"method_not_allowed"});
 const url=Deno.env.get("SUPABASE_URL"),key=Deno.env.get("SUPABASE_SERVICE_ROLE_KEY");
 if(!url||!key) return json(503,{error:"backend_not_configured"});
 const jwt=req.headers.get("Authorization")?.replace(/^Bearer /,"");
 if(!jwt) return json(401,{error:"login_required"});
 const db=createClient(url,key,{auth:{persistSession:false,autoRefreshToken:false}});
 const {data:{user},error:authError}=await db.auth.getUser(jwt);
 if(authError||!user||user.is_anonymous) return json(401,{error:"invalid_session"});
 const {data:flag}=await db.from("app_flags").select("commerce_enabled").eq("singleton",true).single();
 if(!flag?.commerce_enabled) return json(503,{error:"commerce_disabled"});
 let body:Record<string,unknown>;
 try{body=await req.json();}catch{return json(400,{error:"invalid_json"});}
 const sku=body?.productId,token=body?.purchaseToken;
 if(typeof sku!=="string"||typeof token!=="string"||!/^[a-z0-9_]{3,100}$/.test(sku)||token.length<16||token.length>4000) return json(400,{error:"invalid_purchase"});
 const {data:product}=await db.from("sticker_products").select("published,play_product_id").eq("id",sku).single();
 if(!product?.published||product.play_product_id!==sku) return json(400,{error:"product_unavailable"});
 try{
  const access=await playToken();
  const p=await playGet(token,access);
  const state=p?.purchaseStateContext?.purchaseState;
  if(state==="PENDING")return json(409,{error:"payment_pending"});
  if(state!=="PURCHASED")return json(422,{error:"not_purchased"});
  const items=p?.productLineItem;
  if(!p?.orderId||!Array.isArray(items)||items.length!==1||items[0].productId!==sku||(items[0].productOfferDetails?.quantity??1)!==1) return json(422,{error:"product_mismatch"});
  // Binding added by Android BillingFlowParams.setObfuscatedAccountId.
  if(p?.obfuscatedExternalAccountId!==await hash(user.id)) return json(403,{error:"purchaser_mismatch"});
  const {data,error}=await db.rpc("finalize_verified_google_purchase",{p_user_id:user.id,p_product_id:sku,p_token_hash:await hash(token),p_order_reference:p.orderId});
  if(error){console.error("Finalization error",error.code);return json(409,{error:"fulfillment_failed"});}
  const acknowledged=p.acknowledgementState==="ACKNOWLEDGEMENT_STATE_ACKNOWLEDGED"||await playAcknowledge(sku,token,access);
  if(!acknowledged) console.error("Play acknowledgement needs retry");
  return json(200,{status:data?.status??"verified",acknowledged});
 }catch(error){
  const msg=error instanceof Error?error.message:"unknown";
  if(msg==="publisher_not_configured")return json(503,{error:"publisher_not_configured"});
  console.error("Verification unavailable",msg);
  return json(502,{error:"verification_unavailable"});
 }
});