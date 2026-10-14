package org.imamraza.schoolmission
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight

@Composable fun ConnectedAccountScreen(flags:AppFeatureFlags,commerce:PlayCommerce,onPrivacy:()->Unit,onDeletionPage:()->Unit,onMessage:(String)->Unit) {
 val context=LocalContext.current
 val session=SupporterAuth.session
 val downloadScope=rememberCoroutineScope()
 var owned by remember { mutableStateOf<List<String>>(emptyList()) }
 var showDelete by remember { mutableStateOf(false) }
 var processing by remember { mutableStateOf(false) }
 LaunchedEffect(session?.userId) { owned=SupporterData.ownedStickers() }
 ScrollPage {
  PageTitle("My Account","Secure access to digital sticker ownership and account choices.")
  SurfaceCard {
   Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){
    Icon(Icons.Default.AccountCircle,null,modifier=Modifier.size(50.dp),tint=Evergreen)
    Column {
     Text(if(session==null)"Guest supporter" else "Signed in",fontWeight=FontWeight.Bold,color=Forest,fontSize=18.sp)
     Text(session?.email?:"Google account required for purchases",color=Muted,fontSize=13.sp)
    }
   }
   if(session==null) {
    ForestButton(if(flags.googleSignIn)"Continue with Google" else "Google sign-in — awaiting setup",{
     if(flags.googleSignIn) SupporterAuth.beginGoogleLogin(context)
     else onMessage("Google sign-in needs an OAuth client ID, secret and redirect configuration. No login data is collected yet.")
    })
   }else{
    OutlineButton("Sign out",onClick={SupporterAuth.logout();owned=emptyList()})
   }
   SupporterAuth.errorMessage?.let { Text(it,color=MaterialTheme.colorScheme.error,fontSize=12.sp) }
   if(SupporterAuth.pending) LinearProgressIndicator(Modifier.fillMaxWidth())
  }
  SurfaceCard {
   Text("My sticker collection",fontWeight=FontWeight.Bold,fontSize=18.sp,color=Forest)
   if(session==null) Text("Sign in to see the collections attached to your Google account.",color=Muted,fontSize=13.sp)
   else if(owned.isEmpty()) Text("No verified purchases yet.",color=Muted,fontSize=13.sp)
   else owned.forEach { sku ->
    InfoRow(Icons.Default.CollectionsBookmark,MissionContent.packs.find{it.id==sku}?.name?:sku,"Download verified sticker ZIP") {
     downloadScope.launch {
      val file=PackDelivery.downloadOwnedPack(context,sku)
      if(file==null) onMessage("Unable to download this pack. Check your connection and purchase ownership.")
      else {
       try {
        val contentUri=FileProvider.getUriForFile(context,context.packageName+".provider",file)
        val send=Intent(Intent.ACTION_SEND).apply{
         type="application/zip";putExtra(Intent.EXTRA_STREAM,contentUri)
         addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(send,"Save or share your sticker pack"))
       }catch(_:Exception){onMessage("No compatible app found to open the sticker pack.")}
      }
     }
    }
   }
   OutlineButton("Refresh purchases & ownership",onClick={
    if(session!=null) commerce.restore()
    onMessage(if(session==null)"Please sign in first." else "Google Play ownership reconciliation started. Refresh this page to view any newly verified purchases.")
   })
  }
  SurfaceCard {
   InfoRow(Icons.Default.ReceiptLong,"Google Play purchase records","Purchases are managed in your Google Play account"){onMessage("Open Play Store → Payments & subscriptions → Budget & history to view official receipts.")}
   HorizontalDivider()
   InfoRow(Icons.Default.PrivacyTip,"Privacy policy","How your account and transaction data is handled",onPrivacy)
   HorizontalDivider()
   InfoRow(Icons.Default.DeleteOutline,"Account deletion","Permanently remove supporter account data",{
    if(session==null)onDeletionPage() else showDelete=true
   })
  }
  SurfaceCard {
   Text("Supporter privacy",fontWeight=FontWeight.Bold,color=Forest)
   Text("The app is for adult supporters. Children's school and scholarship records are not collected here. School fundraising and payments are not active yet.",fontSize=13.sp,color=Muted)
  }
 }
 if(showDelete) AlertDialog(onDismissRequest={showDelete=false},title={Text("Delete your account?")},text={Text("This removes your supporter account and active sticker entitlements. Anonymized payment records may be kept for legal accounting obligations. This cannot be undone.")},confirmButton={
  val scope=rememberCoroutineScope()
  TextButton(enabled=!processing,onClick={
   processing=true
   scope.launch {
    val ok=SupporterData.deleteAccount()
    processing=false;showDelete=false
    onMessage(if(ok)"Your account was deleted." else "Deletion could not be completed. Please contact support.")
   }
  }) {Text("Delete account")}
 },dismissButton={TextButton(onClick={showDelete=false}) {Text("Cancel")}})
}
