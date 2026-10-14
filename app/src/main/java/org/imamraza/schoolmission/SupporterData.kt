package org.imamraza.schoolmission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.net.HttpURLConnection

object SupporterData {
 suspend fun ownedStickers():List<String> = withContext(Dispatchers.IO) {
  val access=SupporterAuth.accessToken()?:return@withContext emptyList()
  val user=SupporterAuth.session?.userId?:return@withContext emptyList()
  try{
   val url=BuildConfig.SUPABASE_URL.trimEnd('/')+"/rest/v1/entitlements?select=product_id&user_id=eq."+java.net.URLEncoder.encode(user,"UTF-8")+"&revoked_at=is.null"
   val conn=(URL(url).openConnection() as HttpURLConnection).apply {
    connectTimeout=9000;readTimeout=9000
    setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
    setRequestProperty("Authorization","Bearer "+access)
   }
   try {
    if(conn.responseCode!=200)return@withContext emptyList()
    val array=org.json.JSONArray(conn.inputStream.bufferedReader().use {it.readText()})
    (0 until array.length()).mapNotNull { array.optJSONObject(it)?.optString("product_id")?.takeIf(String::isNotBlank) }
   }finally{conn.disconnect()}
  }catch(_:Exception){emptyList()}
 }
 suspend fun deleteAccount():Boolean = withContext(Dispatchers.IO) {
  val token=SupporterAuth.accessToken()?:return@withContext false
  try {
   val conn=(URL(BuildConfig.SUPABASE_URL.trimEnd('/')+"/functions/v1/delete-account").openConnection() as HttpURLConnection).apply {
    requestMethod="POST";connectTimeout=12000;readTimeout=12000;doOutput=true
    setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
    setRequestProperty("Authorization","Bearer "+token)
    setRequestProperty("Content-Type","application/json")
   }
   try {
    conn.outputStream.use{it.write("{}".toByteArray())}
    if(conn.responseCode==200) { SupporterAuth.logout();true } else false
   }finally{conn.disconnect()}
  }catch(_:Exception){false}
 }
}
