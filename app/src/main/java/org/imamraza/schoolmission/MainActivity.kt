package org.imamraza.schoolmission

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(7,62,51)
        setContent { MissionTheme { MissionApp() } }
    }
}

@Composable fun MissionApp() {
    val context=LocalContext.current
    val uri=LocalUriHandler.current
    val preferences=remember { context.getSharedPreferences("mission_ui",0) }
    var onboarded by rememberSaveable { mutableStateOf(preferences.getBoolean("onboarded",false)) }
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var page by rememberSaveable { mutableStateOf("") }
    var packId by rememberSaveable { mutableStateOf("") }
    var dialog by remember { mutableStateOf("") }
    var updates by remember { mutableStateOf<List<Pair<String,String>>>(emptyList()) }
    LaunchedEffect(Unit) { updates=PublicContentRepository.getPublishedUpdates() }
    BackHandler(enabled=page.isNotEmpty() || packId.isNotEmpty()) { page="";packId="" }
    val openSupport={ tab=2;page="";packId="" }
    if(!onboarded) {
        OnboardingScreen { onboarded=true;preferences.edit().putBoolean("onboarded",true).apply() }
        return
    }
    Scaffold(containerColor=Ivory,bottomBar={ if(page.isEmpty() && packId.isEmpty()) BottomNavigation(tab) { tab=it } }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if(packId.isNotEmpty()) {
                val pack=MissionContent.packs.find { it.id==packId }
                if(pack!=null) PackDetailScreen(pack,onBack={packId=""},onBuy={dialog="Purchases will be available after Google Play and Supabase payment verification are configured."})
            } else if(page.isNotEmpty()) {
                SchoolDetailScreen(page,onBack={page=""},onSupport=openSupport)
            } else {
                AppHeader()
                when(tab) {
                    0 -> HomeScreen(updates,onSupport=openSupport,onSchool={tab=1},onJourney={tab=3})
                    1 -> SchoolScreen(onOpen={page=it})
                    2 -> SupportScreen(onSelect={packId=it})
                    3 -> JourneyScreen(updates)
                    4 -> AccountScreen(onPrivacy={uri.openUri(MissionContent.privacyUrl)},onAction={dialog=it})
                }
            }
        }
    }
    if(dialog.isNotEmpty()) AlertDialog(onDismissRequest={dialog=""},title={Text("Information")},text={Text(dialog)},confirmButton={TextButton(onClick={dialog=""}){Text("Understood")}})
}
