package org.imamraza.schoolmission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.content.SharedPreferences
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class SupporterSession(val userId:String,val email:String?,val accessToken:String,val refreshToken:String,val expiresAt:Long)
data class AppFeatureFlags(val googleSignIn:Boolean=false,val commerce:Boolean=false)

// Keystore-backed encryption for the OAuth verifier and Supabase tokens.
private class LocalSecretStore(context:Context) {
 private val preferences=context.getSharedPreferences("mission_auth_v1",Context.MODE_PRIVATE)
 private val alias="imam_raza_school_oauth"
 private fun key():SecretKey {
  val store=KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
  (store.getKey(alias,null) as? SecretKey)?.let { return it }
  return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore").apply {
   init(KeyGenParameterSpec.Builder(alias,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setRandomizedEncryptionRequired(true).build())
  }.generateKey()
 }
 private fun encode(bytes:ByteArray)=Base64.encodeToString(bytes,Base64.NO_WRAP or Base64.URL_SAFE)
 private fun decode(s:String)=Base64.decode(s,Base64.NO_WRAP or Base64.URL_SAFE)
 fun put(name:String,value:String) {
  val cipher=Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.ENCRYPT_MODE,key())
  val text=encode(cipher.iv)+"."+encode(cipher.doFinal(value.toByteArray(Charsets.UTF_8)))
  preferences.edit().putString(name,text).apply()
 }
 fun get(name:String):String? = try {
  val text=preferences.getString(name,null)?:return null
  val parts=text.split(".")
  val cipher=Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.DECRYPT_MODE,key(),GCMParameterSpec(128,decode(parts[0])))
  String(cipher.doFinal(decode(parts[1])),Charsets.UTF_8)
 }catch(_:Exception){ null }
 fun remove(name:String)=preferences.edit().remove(name).apply()
 fun clear()=preferences.edit().clear().apply()
}

object SupporterAuth {
 private lateinit var app:Context
 private lateinit var store:LocalSecretStore
 var session by mutableStateOf<SupporterSession?>(null); private set
 var pending by mutableStateOf(false); private set
 var errorMessage by mutableStateOf<String?>(null); private set
 private const val CALLBACK="org.imamraza.schoolmission://login-callback"
 private val endpoint get()=BuildConfig.SUPABASE_URL.trimEnd('/')
 private fun random():String=ByteArray(40).also{SecureRandom().nextBytes(it)}.let{Base64.encodeToString(it,Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)}
 fun initialize(context:Context) {
  app=context.applicationContext
  store=LocalSecretStore(app)
  val raw=store.get("session")?:return
  try{
   val j=JSONObject(raw)
   session=SupporterSession(j.getString("user_id"),j.optString("email").takeIf {it.isNotBlank()},
    j.getString("access_token"),j.getString("refresh_token"),j.getLong("expires_at"))
  }catch(_:Exception){store.remove("session")}
 }
 fun beginGoogleLogin(context:Context) {
  if(endpoint.isEmpty() || BuildConfig.SUPABASE_PUBLISHABLE_KEY.isEmpty()) {
   errorMessage="Supabase is not connected";return
  }
  val verifier=random()
  val challenge=MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
   .let{Base64.encodeToString(it,Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)}
  store.put("pending_verifier",verifier)
  store.put("pending_time",System.currentTimeMillis().toString())
  val redirect=Uri.encode(CALLBACK)
  val target=endpoint+"/auth/v1/authorize?provider=google&redirect_to="+redirect+"&code_challenge="+Uri.encode(challenge)+"&code_challenge_method=s256"
  try{context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(target)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))}
  catch(_:Exception){errorMessage="No browser is available to complete login"}
 }
 suspend fun finishGoogleLogin(callback:Uri?) {
  if(callback?.scheme!="org.imamraza.schoolmission" || callback.host!="login-callback") return
  val verifier=store.get("pending_verifier")?:run{errorMessage="Login request expired";return}
  val started=store.get("pending_time")?.toLongOrNull()?:0L
  store.remove("pending_verifier");store.remove("pending_time")
  if(System.currentTimeMillis()-started>600_000L || started>System.currentTimeMillis()) {
   errorMessage="Login request expired. Please try again.";return
  }
  val code=callback.getQueryParameter("code")
  if(code.isNullOrBlank()) {errorMessage=callback.getQueryParameter("error_description")?:"Google sign-in was canceled";return}
  pending=true
  try {
   val tokens=postJson("/auth/v1/token?grant_type=pkce",JSONObject().put("auth_code",code).put("code_verifier",verifier))
   storeSession(tokens)
  }catch(e:Exception){errorMessage="Unable to complete sign-in: "+(e.message?:"unknown error")}
  finally{pending=false}
 }
 private fun storeSession(json:JSONObject) {
  val user=json.getJSONObject("user")
  val next=SupporterSession(user.getString("id"),user.optString("email").takeIf {it.isNotBlank()},
   json.getString("access_token"),json.getString("refresh_token"),
   System.currentTimeMillis()+json.optLong("expires_in",3600)*1000L)
  session=next
  store.put("session",JSONObject().put("user_id",next.userId).put("email",next.email?:"")
   .put("access_token",next.accessToken).put("refresh_token",next.refreshToken)
   .put("expires_at",next.expiresAt).toString())
  errorMessage=null
 }
 suspend fun accessToken():String? {
  val current=session?:return null
  if(current.expiresAt>System.currentTimeMillis()+60_000L)return current.accessToken
  return try{
   val next=postJson("/auth/v1/token?grant_type=refresh_token",JSONObject().put("refresh_token",current.refreshToken))
   storeSession(next);session?.accessToken
  }catch(_:Exception){ logout();null }
 }
 fun logout(){session=null;store.clear();errorMessage=null}
 private suspend fun postJson(path:String, body:JSONObject):JSONObject=withContext(Dispatchers.IO) {
  val connection=(URL(endpoint+path).openConnection() as HttpURLConnection).apply{
   requestMethod="POST";connectTimeout=12000;readTimeout=12000;doOutput=true
   setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
   setRequestProperty("content-type","application/json")
  }
  try{
   connection.outputStream.use{it.write(body.toString().toByteArray())}
   val payload=(if(connection.responseCode in 200..299)connection.inputStream else connection.errorStream)
    ?.bufferedReader()?.use{it.readText()}?:""
   if(connection.responseCode !in 200..299)throw IllegalStateException("Authentication service returned "+connection.responseCode)
   JSONObject(payload)
  } finally { connection.disconnect() }
 }
}
