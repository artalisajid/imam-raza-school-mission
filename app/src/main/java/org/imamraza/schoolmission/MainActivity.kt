package org.imamraza.schoolmission

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity:ComponentActivity() {
 override fun onCreate(savedInstanceState:Bundle?) {
  super.onCreate(savedInstanceState)
  SupporterAuth.initialize(this)
  handleAuthIntent(intent)
  setContent { MissionTheme { MissionApp(this) } }
 }
 override fun onNewIntent(intent:Intent) {
  super.onNewIntent(intent)
  setIntent(intent)
  handleAuthIntent(intent)
 }
 private fun handleAuthIntent(intent:Intent?) {
  if(intent?.data!=null)lifecycleScope.launch {SupporterAuth.finishGoogleLogin(intent.data)}
 }
}
@Composable fun MissionApp(activity:MainActivity) {
 val context=LocalContext.current
 val uri=LocalUriHandler.current
 val preferences=remember {context.getSharedPreferences("mission_ui",0)}
 var onboarded by rememberSaveable {mutableStateOf(preferences.getBoolean("onboarded",false))}
 var tab by rememberSaveable {mutableIntStateOf(0)}
 var page by rememberSaveable {mutableStateOf("")}
 var packId by rememberSaveable {mutableStateOf("")}
 var dialog by remember {mutableStateOf("")}
 var updates by remember {mutableStateOf<List<Pair<String,String>>>(emptyList())}
 var flags by remember {mutableStateOf(AppFeatureFlags())}
 val scope=rememberCoroutineScope()
 val commerce=remember(activity) {PlayCommerce(activity,scope)}
 DisposableEffect(commerce) {commerce.initialize();onDispose {commerce.dispose()}}
 LaunchedEffect(Unit) {
  updates=PublicContentRepository.getPublishedUpdates()
  flags=MissionBackend.featureFlags()
 }
 val signedUser=SupporterAuth.session?.userId
 LaunchedEffect(signedUser) {if(signedUser!=null)commerce.restore()}
 BackHandler(enabled=page.isNotEmpty() || packId.isNotEmpty()){page="";packId=""}
 val openSupport={tab=2;page="";packId=""}
 if(!onboarded){
  OnboardingScreen{onboarded=true;preferences.edit().putBoolean("onboarded",true).apply()}
  return
 }
 Scaffold(containerColor=Ivory,bottomBar={
  if(page.isEmpty()&&packId.isEmpty())BottomNavigation(tab){tab=it}
 }) {padding->
  Column(Modifier.fillMaxSize().padding(padding)){
   if(packId.isNotEmpty()){
    val pack=MissionContent.packs.find{it.id==packId}
    if(pack!=null)PackDetailScreen(
      pack,onBack={packId=""},onBuy={commerce.buy(pack.id,flags.commerce)},
      price=commerce.price(pack.id),enabled=commerce.canBuy(pack.id,flags.commerce)&&signedUser!=null
    )
   }else if(page.isNotEmpty()){
    SchoolDetailScreen(page,onBack={page=""},onSupport=openSupport)
   }else{
    AppHeader()
    when(tab){
     0->HomeScreen(updates,onSupport=openSupport,onSchool={tab=1},onJourney={tab=3})
     1->SchoolScreen(onOpen={page=it})
     2->SupportScreen(onSelect={packId=it})
     3->JourneyScreen(updates)
     4->ConnectedAccountScreen(flags,commerce,
       onPrivacy={uri.openUri(MissionContent.privacyUrl)},
       onDeletionPage={uri.openUri("https://artalisajid.github.io/imam-raza-school-mission/deletion.html")},
       onMessage={dialog=it})
    }
   }
  }
 }
 if(commerce.info.isNotBlank()&&flags.commerce&&dialog.isEmpty()){
  // The status remains available on product detail; never show payment dialogs automatically.
 }
 if(dialog.isNotEmpty())AlertDialog(
  onDismissRequest={dialog=""},title={Text("Information")},text={Text(dialog)},
  confirmButton={TextButton(onClick={dialog=""}){Text("Understood")}}
 )
}
