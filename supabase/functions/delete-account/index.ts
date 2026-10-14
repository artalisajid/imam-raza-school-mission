import {createClient} from "npm:@supabase/supabase-js@2.57.0";
// Supports in-app supporter account deletion. Financial transaction records are
// de-identified and retained only for applicable accounting obligations.
const reply=(status:number,body:unknown)=>new Response(JSON.stringify(body),{status,headers:{"content-type":"application/json","cache-control":"no-store"}});
Deno.serve(async(req:Request)=>{
 if(req.method!=="POST")return reply(405,{error:"method_not_allowed"});
 const url=Deno.env.get("SUPABASE_URL")??"",key=Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")??"";
 const token=req.headers.get("authorization")?.replace(/^Bearer /,"");
 if(!url||!key)return reply(503,{error:"backend_unavailable"});
 if(!token)return reply(401,{error:"login_required"});
 const db=createClient(url,key,{auth:{autoRefreshToken:false,persistSession:false}});
 const {data:{user},error}=await db.auth.getUser(token);
 if(error||!user)return reply(401,{error:"invalid_session"});
 const {data:admin}=await db.from("admin_memberships").select("role").eq("user_id",user.id).maybeSingle();
 if(admin)return reply(403,{error:"administrator_deletion_requires_separate_review"});
 try {
  const ent=await db.from("entitlements").delete().eq("user_id",user.id);
  if(ent.error)throw ent.error;
  const orders=await db.from("purchases").update({user_id:null}).eq("user_id",user.id);
  if(orders.error)throw orders.error;
  const deleted=await db.auth.admin.deleteUser(user.id);
  if(deleted.error)throw deleted.error;
  return reply(200,{deleted:true,retained:"Anonymized purchase and settlement records may be retained for financial compliance."});
 }catch(error){
  console.error("Account deletion failed",error instanceof Error?error.message:"unknown");
  return reply(500,{error:"account_deletion_incomplete_contact_support"});
 }
});
