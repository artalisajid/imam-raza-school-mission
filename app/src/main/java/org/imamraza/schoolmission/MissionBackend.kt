package org.imamraza.schoolmission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object MissionBackend {
 suspend fun featureFlags():AppFeatureFlags=withContext(Dispatchers.IO){
  if(BuildConfig.SUPABASE_URL.isBlank())return@withContext AppFeatureFlags()
  try{
   val c=(URL(BuildConfig.SUPABASE_URL.trimEnd('/')+"/rest/v1/app_flags?select=commerce_enabled,google_signin_enabled&singleton=eq.true").openConnection() as HttpURLConnection).apply{
    connectTimeout=6000;readTimeout=6000
    setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
   }
   if(c.responseCode!=200)return@withContext AppFeatureFlags()
   val first=org.json.JSONArray(c.inputStream.bufferedReader().use{it.readText()}).optJSONObject(0)
   AppFeatureFlags(first?.optBoolean("google_signin_enabled",false)?:false,first?.optBoolean("commerce_enabled",false)?:false)
  }catch(_:Exception){AppFeatureFlags()}
 }
 suspend fun verify(productId:String,token:String):String=withContext(Dispatchers.IO){
  val access=SupporterAuth.accessToken()?:return@withContext "Sign in to verify your purchase."
  val conn=(URL(BuildConfig.SUPABASE_URL.trimEnd('/')+"/functions/v1/verify-play-purchase").openConnection() as HttpURLConnection).apply{
   requestMethod="POST";connectTimeout=14000;readTimeout=14000;doOutput=true
   setRequestProperty("authorization","Bearer "+access)
   setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
   setRequestProperty("content-type","application/json")
  }
  try {
   conn.outputStream.use{it.write(JSONObject().put("productId",productId).put("purchaseToken",token).toString().toByteArray())}
   val stream=if(conn.responseCode in 200..299)conn.inputStream else conn.errorStream
   val answer=JSONObject(stream?.bufferedReader()?.use{it.readText()}?:"{}")
   when(conn.responseCode) {
    200-> if(answer.optBoolean("acknowledged",false)) "Purchase verified and stickers unlocked." else "Purchase verified. Google acknowledgement is pending."
    409->"This purchase is pending or cannot be fulfilled yet."
    401->"Your login has expired. Please sign in again."
    503->"Purchases are not available yet."
    else->"We could not verify this purchase. Please contact support with your Google Play receipt."
   }
  }catch(_:Exception){"Network error while verifying your purchase. Your Google Play purchase can be restored later."}
  finally {conn.disconnect()}
 }
}
