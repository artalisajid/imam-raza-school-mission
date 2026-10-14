package org.imamraza.schoolmission
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object PackDelivery {
 suspend fun downloadOwnedPack(context:Context,sku:String):File?=withContext(Dispatchers.IO) {
  if(MissionContent.packs.none { it.id==sku })return@withContext null
  val jwt=SupporterAuth.accessToken()?:return@withContext null
  val path=BuildConfig.SUPABASE_URL.trimEnd('/')+"/storage/v1/object/authenticated/paid-sticker-packs/"+sku+"/pack.zip"
  try{
   val conn=(URL(path).openConnection() as HttpURLConnection).apply{
    connectTimeout=12000;readTimeout=20000
    setRequestProperty("apikey",BuildConfig.SUPABASE_PUBLISHABLE_KEY)
    setRequestProperty("Authorization","Bearer "+jwt)
   }
   try {
    if(conn.responseCode!=200 || conn.contentLengthLong>25_000_000L)return@withContext null
    val directory=File(context.cacheDir,"paid-stickers")
    directory.mkdirs()
    val temp=File(directory,sku+".tmp")
    val dest=File(directory,sku+".zip")
    var copied=0L
    conn.inputStream.use { input ->
     temp.outputStream().use { output ->
      val block=ByteArray(8192)
      while(true){
       val n=input.read(block);if(n<0)break
       copied+=n;if(copied>25_000_000L){temp.delete();return@withContext null}
       output.write(block,0,n)
      }
     }
    }
    if(copied<50L || !temp.renameTo(dest)){temp.delete();return@withContext null}
    dest
   }finally{conn.disconnect()}
  }catch(_:Exception){null}
 }
}
