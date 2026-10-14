package org.imamraza.schoolmission

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

@Composable fun ScrollPage(content:@Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=18.dp),verticalArrangement=Arrangement.spacedBy(17.dp),content=content)
}
@Composable fun AppHeader() {
    Row(Modifier.fillMaxWidth().background(Forest).padding(horizontal=18.dp,vertical=13.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(43.dp).clip(RoundedCornerShape(12.dp)).background(Gold),contentAlignment=Alignment.Center) { Text("🌱",fontSize=24.sp) }
        Column(Modifier.weight(1f)) {
            Text("Imam Raza (a.s.)",color=Color.White,fontFamily=FontFamily.Serif,fontWeight=FontWeight.Bold,fontSize=18.sp)
            Text("SCHOOL MISSION • KARACHI",color=Gold,fontSize=10.sp,letterSpacing=1.sp)
        }
        Icon(Icons.Default.Info,null,tint=Gold)
    }
}
@Composable fun BottomNavigation(selected:Int,onTab:(Int)->Unit) {
    val nav=listOf(Triple("Home",Icons.Default.Home,0),Triple("School",Icons.Default.School,1),Triple("Support",Icons.Default.Favorite,2),Triple("Journey",Icons.Default.Timeline,3),Triple("Account",Icons.Default.Person,4))
    NavigationBar(containerColor=Color.White,tonalElevation=8.dp) {
        nav.forEach { (title,icon,index) ->
            NavigationBarItem(selected=selected==index,onClick={onTab(index)},icon={ Icon(icon,title,tint=if(index==2) Evergreen else if(selected==index) Forest else Muted,modifier=Modifier.size(if(index==2)27.dp else 23.dp))},label={Text(title,fontWeight=if(index==2)FontWeight.Bold else FontWeight.Medium,fontSize=11.sp)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Forest,indicatorColor=AccentMint,selectedTextColor=Forest))
        }
    }
}
@Composable fun OnboardingScreen(onStart:()->Unit) {
    Column(Modifier.fillMaxSize().background(Forest).padding(22.dp),verticalArrangement=Arrangement.SpaceBetween) {
        Spacer(Modifier.height(35.dp))
        Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(18.dp)) {
            Box(Modifier.size(110.dp).clip(RoundedCornerShape(30.dp)).background(Evergreen).border(1.dp,Gold,RoundedCornerShape(30.dp)),contentAlignment=Alignment.Center) { Text("🌱📖",fontSize=38.sp) }
            Text("Imam Raza (a.s.)\nSchool Mission",color=Color.White,fontFamily=FontFamily.Serif,fontWeight=FontWeight.Bold,fontSize=32.sp,textAlign=TextAlign.Center,lineHeight=38.sp)
            Text("KNOWLEDGE • CHARACTER • COMPASSION",color=Gold,fontSize=11.sp,letterSpacing=1.1.sp,textAlign=TextAlign.Center)
            SchoolIllustration()
            Text("Help build a school from zero.",color=Color.White,fontWeight=FontWeight.Bold,fontSize=22.sp,textAlign=TextAlign.Center)
            Text("Our dream: an affordable school in Karachi, from Nursery to Class 7, inspired by Ahlulbayt (a.s.) and grounded in Quranic and world knowledge.",color=Color(0xFFE5EEE7),fontSize=15.sp,textAlign=TextAlign.Center,lineHeight=23.sp)
        }
        Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
            GoldButton("Explore the mission",onStart)
            Text("A proposed school, not an operating institution. No funds are reported yet.",color=Color(0xFFCEE0D4),fontSize=11.sp,textAlign=TextAlign.Center)
        }
    }
}
@Composable fun HomeScreen(updates:List<Pair<String,String>>,onSupport:()->Unit,onSchool:()->Unit,onJourney:()->Unit) {
    ScrollPage {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(23.dp)).background(Forest).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
            Text("INSPIRED BY AHLULBAYT (A.S.)",color=Gold,fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.1.sp)
            Text("Help us build\na school from zero.",fontFamily=FontFamily.Serif,fontSize=31.sp,lineHeight=37.sp,color=Color.White,fontWeight=FontWeight.Bold)
            Text("A future Shia school in Karachi where Quran knowledge, religious understanding and world education grow together.",color=Color(0xFFE4F0E8),fontSize=14.sp,lineHeight=21.sp)
            SchoolIllustration()
            GoldButton("Explore Stickers & Support  →",onSupport)
            Text("Original digital products. Net proceeds may support the school subject to lawful arrangements and verified accounting.",color=Color(0xFFD5E4D9),fontSize=11.sp,lineHeight=17.sp)
        }
        SurfaceCard {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween,modifier=Modifier.fillMaxWidth()) {
                Text("Our first milestone",fontWeight=FontWeight.Bold,color=Forest,fontSize=18.sp)
                Tag("PLANNED",Color(0xFFFFF2D6))
            }
            Text("Legal registration",fontWeight=FontWeight.Bold,fontSize=20.sp,color=Forest)
            Text("We are starting without land or an established school. The first task is to establish the lawful organization and required permissions.",color=Muted,fontSize=13.sp,lineHeight=20.sp)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text("Funding target",color=Muted,fontSize=12.sp);Text("Being assessed",fontSize=12.sp,fontWeight=FontWeight.SemiBold)
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
                Text("Verified funds",color=Muted,fontSize=12.sp);Text("Not yet reported",fontSize=12.sp,fontWeight=FontWeight.SemiBold)
            }
            OutlineButton("View our journey",onJourney)
        }
        PageTitle("Three pillars of learning","Faith and academic education designed to complement one another.")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()) {
            listOf("Shia\nknowledge" to "🌿","Quran\nknowledge" to "📖","World\nknowledge" to "🌍").forEach { (title,emoji) ->
                Column(Modifier.weight(1f).clip(RoundedCornerShape(15.dp)).background(AccentMint).clickable(onClick=onSchool).padding(vertical=15.dp,horizontal=5.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                    Text(emoji,fontSize=24.sp); Spacer(Modifier.height(7.dp));Text(title,textAlign=TextAlign.Center,fontWeight=FontWeight.Bold,fontSize=12.sp,lineHeight=16.sp)
                }
            }
        }
        PageTitle("Latest verified updates")
        if(updates.isEmpty()) Text("No verified public updates have been published yet.",color=Muted,fontSize=13.sp)
        else updates.forEach { (title,summary) -> SurfaceCard { Text(title,fontWeight=FontWeight.Bold,color=Forest);Text(summary,color=Muted,fontSize=13.sp) } }
    }
}

@Composable fun SchoolScreen(onOpen:(String)->Unit) {
    ScrollPage {
        PageTitle("Our Future School","Karachi • Nursery through Class 7 • Proposed institution")
        SchoolIllustration()
        Text("An affordable Shia school inspired by Ahlulbayt (a.s.), bringing faith, knowledge, curiosity and good character together.",fontSize=14.sp,lineHeight=22.sp,color=Ink)
        SurfaceCard {
            InfoRow(Icons.Default.AutoStories,"Vision, Values & Philosophy","Justice, kindness, compassion and lifelong learning"){onOpen("Vision, Values & Philosophy")}
            HorizontalDivider()
            InfoRow(Icons.Default.LocalLibrary,"Shia, Quran & World Knowledge","Our three connected educational pillars"){onOpen("Knowledge Pillars")}
            HorizontalDivider()
            InfoRow(Icons.Default.School,"Curriculum: Nursery–Class 7","Developmentally appropriate progress across ten stages"){onOpen("Curriculum")}
            HorizontalDivider()
            InfoRow(Icons.Default.Apartment,"Campus & Facilities","Planned classroom, safety and accessibility provisions"){onOpen("Campus & Facilities")}
            HorizontalDivider()
            InfoRow(Icons.Default.VolunteerActivism,"Tuition & Scholarships","Rs. 3,000 / Rs. 1,500 / Rs. 0"){onOpen("Tuition & Scholarships")}
            HorizontalDivider()
            InfoRow(Icons.Default.Assignment,"Admissions (Future)","Admissions are not open"){onOpen("Admissions")}
            HorizontalDivider()
            InfoRow(Icons.Default.Shield,"Child Safeguarding & Governance","Safe, dignified and inclusive learning"){onOpen("Safeguarding & Governance")}
        }
    }
}
@Composable fun SchoolDetailScreen(name:String,onBack:()->Unit,onSupport:()->Unit) {
    ScrollPage {
        TextButton(onClick=onBack) { Icon(Icons.Default.ArrowBack,null);Spacer(Modifier.width(8.dp));Text("Back to school",color=Forest) }
        PageTitle(name,"Imam Raza (a.s.) School Mission • Karachi")
        when(name) {
            "Vision, Values & Philosophy" -> {
                SchoolIllustration()
                SurfaceCard { Text("Our vision",fontWeight=FontWeight.Bold,color=Forest);Text("To establish an affordable institution where Shia and Quranic knowledge support academic excellence, curiosity, empathy and service to society.",lineHeight=22.sp) }
                listOf("Knowledge and critical thinking","Justice, honesty and integrity","Compassion and service","Respect and dignity","Child-centered, evidence-led teaching").forEach { Text("✦  $it",fontSize=15.sp,color=Ink) }
            }
            "Knowledge Pillars" -> {
                listOf(Triple("Shia Knowledge","Ahlulbayt (a.s.) history, Ja'fari studies, ethics and verified teachings","🌿"),Triple("Quran Knowledge","Age-appropriate listening, reading, tajweed, meaning and reflection","📖"),Triple("World Knowledge","Sindh-aligned languages, mathematics, science, computing, creativity and wellbeing","🌍")).forEach { (title,desc,emoji)->SurfaceCard {Text("$emoji  $title",fontWeight=FontWeight.Bold,color=Forest,fontSize=18.sp);Text(desc,color=Muted,lineHeight=20.sp)}}
            }
            "Curriculum" -> {
                Text("Learning develops from exploration in the early years to deeper inquiry in primary and middle grades. This is the proposed progression pending academic and religious review.",color=Muted)
                MissionContent.grades.forEachIndexed { index,grade -> SurfaceCard {Text(grade,fontWeight=FontWeight.Bold,color=Forest); Text(when {index<3->"Play-based language, early numeracy, social skills, Quran listening and kindness."; index<6->"Foundational reading, mathematics, Quranic literacy, Shia ethics and general knowledge.";else->"General science, languages, mathematics, computing, Shia studies, Quran understanding and reasoning."},fontSize=13.sp,color=Muted,lineHeight=19.sp)} }
            }
            "Campus & Facilities" -> {
                SchoolIllustration()
                Text("Illustrative campus design. No property has been purchased or construction begun.",fontSize=12.sp,color=Muted)
                listOf("Age-appropriate classrooms","Library, science and computer learning resources","Accessible toilets, drinking water and sanitation","Play spaces, first-aid and emergency provisions","Staff and family meeting areas","Appropriate facilities for modest school uniforms").forEach { Text("• $it",color=Ink) }
            }
            "Tuition & Scholarships" -> {
                listOf("Standard tuition" to "Rs. 3,000 / month","Subsidized tuition" to "Rs. 1,500 / month","Full scholarship" to "Rs. 0 / month").forEach { (label,value) -> SurfaceCard {Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {Text(label,fontWeight=FontWeight.SemiBold);Text(value,fontWeight=FontWeight.Bold,color=Forest)}} }
                Text("Fees are proposed, subject to approval. Scholarship eligibility will be assessed confidentially. Full scholarships should account for applicable education costs beyond tuition.",fontSize=13.sp,color=Muted,lineHeight=20.sp)
            }
            "Admissions" -> {
                SurfaceCard {Tag("NOT OPEN");Text("The school has not yet been established.",fontWeight=FontWeight.Bold,fontSize=18.sp,color=Forest);Text("Admissions criteria, dates, policies and application requirements will be published after registration and operational readiness.",fontSize=14.sp,lineHeight=21.sp,color=Muted)}
            }
            "Safeguarding & Governance" -> {
                listOf("No physical punishment or humiliating discipline","Qualified, screened and trained staff","Private child-protection reporting","Accessible and inclusive instruction","Financial and academic oversight","Religious content reviewed by qualified Ja'fari scholars","Black school uniforms, full hair coverage for girls, and age-appropriate, non-punitive implementation").forEach {SurfaceCard {Text("✓  $it",fontSize=14.sp,color=Ink)}}
            }
        }
        GoldButton("Explore Stickers & Support",onSupport)
    }
}

@Composable fun SupportScreen(onSelect:(String)->Unit) {
    ScrollPage {
        SurfaceCard(background=Forest) {
            Text("SUPPORT THE BEGINNING",fontSize=11.sp,fontWeight=FontWeight.Bold,letterSpacing=1.sp,color=Gold)
            Text("Small stickers.\nA brighter tomorrow.",fontFamily=FontFamily.Serif,fontSize=29.sp,lineHeight=35.sp,fontWeight=FontWeight.Bold,color=Color.White)
            Text("Discover original digital collections. Our goal is to use proceeds available under a lawful, published allocation arrangement to help establish the school.",fontSize=13.sp,lineHeight=20.sp,color=Color(0xFFE2F1E6))
            Tag("First milestone: legal registration",Color(0xFFFFE4A8))
        }
        PageTitle("Original sticker collections","Choose meaningful artwork you can keep and use. Store prices appear once Google Play products are configured.")
        MissionContent.packs.forEach { pack ->
            SurfaceCard(Modifier.clickable { onSelect(pack.id) }) {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)) {
                    StickerArt(pack,Modifier.size(112.dp))
                    Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(6.dp)) {
                        Text(pack.name,fontWeight=FontWeight.Bold,fontSize=18.sp,color=Forest)
                        Text(pack.tagline,color=Muted,fontSize=12.sp)
                        Tag(pack.items.size.toString()+" original stickers")
                        Text("View collection →",fontWeight=FontWeight.Bold,fontSize=13.sp,color=Evergreen)
                    }
                }
            }
        }
        SurfaceCard {
            Text("Where do the proceeds go?",fontWeight=FontWeight.Bold,color=Forest)
            Text("The initial objective is to fund the lawful organization and registration work. Transaction values, fees, actual settlements, expenses and allocations will be distinguished in our reports once verified.",fontSize=13.sp,lineHeight=20.sp,color=Muted)
            Text("Product purchases are not charitable donation receipts or tax-deductible gifts.",fontSize=12.sp,color=Ink,fontWeight=FontWeight.SemiBold)
        }
    }
}
@Composable fun PackDetailScreen(pack:StickerPack,onBack:()->Unit,onBuy:()->Unit,price:String,enabled:Boolean) {
    ScrollPage {
        TextButton(onClick=onBack){ Icon(Icons.Default.ArrowBack,null);Spacer(Modifier.width(6.dp));Text("All collections",color=Forest)}
        StickerArt(pack,Modifier.fillMaxWidth().height(226.dp),large=true)
        PageTitle(pack.name,pack.tagline)
        Tag(pack.items.size.toString()+" digital stickers")
        Text(pack.description,color=Muted,fontSize=14.sp,lineHeight=21.sp)
        Text("Included artwork",fontWeight=FontWeight.Bold,color=Forest,fontSize=17.sp)
        pack.items.chunked(6).forEach { row ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceAround) {row.forEach { Text(it,fontSize=26.sp,modifier=Modifier.padding(4.dp)) }}
        }
        SurfaceCard {
            Text("Permanent collection",fontWeight=FontWeight.Bold,color=Forest)
            Text("Each pack is a one-time digital purchase. The same permanent pack cannot be purchased again; new collections may be released later.",color=Muted,fontSize=13.sp,lineHeight=20.sp)
            Text("Actual local pricing will be supplied by Google Play when checkout is configured.",color=Muted,fontSize=13.sp,lineHeight=20.sp)
        }
        Text("Google Play price: "+price,color=Forest,fontWeight=FontWeight.Bold)
        GoldButton(if(enabled) "Buy with Google Play" else "Checkout not yet available",onBuy,enabled=enabled)
        Text(if(enabled) "Your permanent digital collection will be unlocked only after Google verifies payment." else "Purchase is disabled until Google sign-in, Play products and lawful payment settings are activated.",color=Muted,fontSize=11.sp,lineHeight=16.sp)
    }
}

@Composable fun JourneyScreen(updates:List<Pair<String,String>>) {
    ScrollPage {
        PageTitle("From zero to school opening","Every step requires verified funding, permissions and evidence.")
        SurfaceCard(background=Color(0xFFFFF2D5)) {
            Tag("FIRST MILESTONE",Gold)
            Text("Legal registration",fontWeight=FontWeight.Bold,color=Forest,fontSize=21.sp)
            Text("We need to establish the lawful organization before moving toward land purchase and construction.",fontSize=13.sp,color=Ink,lineHeight=20.sp)
            Text("Target: Being assessed   •   Verified funds: Not yet reported",fontSize=12.sp,color=Muted)
        }
        PageTitle("Our project stages")
        MissionContent.stages.forEachIndexed { index,stage ->
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Box(Modifier.size(34.dp).clip(CircleShape).background(if(index==0) Forest else AccentMint),contentAlignment=Alignment.Center) {Text((index+1).toString(),color=if(index==0)Color.White else Forest,fontWeight=FontWeight.Bold)}
                Column(Modifier.weight(1f).padding(bottom=14.dp),verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    Text(stage.title,fontSize=16.sp,fontWeight=FontWeight.Bold,color=Forest)
                    Text(stage.note,fontSize=12.sp,color=Muted,lineHeight=18.sp)
                    Tag(stage.status,Color(0xFFF2EEE4),Muted)
                }
            }
        }
        PageTitle("Financial transparency")
        SurfaceCard {
            InfoRow(Icons.Default.AccountBalance,"Capital fund","No verified funds reported yet")
            HorizontalDivider()
            InfoRow(Icons.Default.ReceiptLong,"Financial reports","No published reconciled report yet")
            HorizontalDivider()
            InfoRow(Icons.Default.Folder,"Documents & evidence","No approved public documents yet")
        }
        PageTitle("Recent project updates")
        if(updates.isEmpty()) Text("There are no approved updates to display.",color=Muted,fontSize=13.sp)
        else updates.forEach { (title,summary)->SurfaceCard{Text(title,fontWeight=FontWeight.Bold);Text(summary,color=Muted,fontSize=13.sp)} }
    }
}
@Composable fun AccountScreen(onPrivacy:()->Unit,onAction:(String)->Unit) {
    ScrollPage {
        PageTitle("My Account","Private sticker ownership and supporter settings.")
        SurfaceCard {
            Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(13.dp)) {
                Icon(Icons.Default.AccountCircle,null,modifier=Modifier.size(54.dp),tint=Evergreen)
                Column { Text("Guest supporter",fontWeight=FontWeight.Bold,fontSize=18.sp,color=Forest);Text("Sign-in is not configured yet",fontSize=12.sp,color=Muted) }
            }
            OutlineButton("Google sign-in · setup pending", onClick={onAction("Google sign-in will be enabled only after a Supabase project, Google OAuth provider and Android redirect configuration are connected.")})
        }
        SurfaceCard {
            InfoRow(Icons.Default.CollectionsBookmark,"My sticker collection","Purchased content appears after verification"){onAction("Your sticker collection will appear after verified purchases and login are enabled.")}
            HorizontalDivider()
            InfoRow(Icons.Default.ReceiptLong,"Purchase history","Receipts and status"){onAction("No purchase records are available before checkout launches.")}
            HorizontalDivider()
            InfoRow(Icons.Default.Notifications,"Mission announcements","Public updates are available on Home and Journey"){onAction("Push notifications are not enabled in this build.")}
        }
        SurfaceCard {
            InfoRow(Icons.Default.PrivacyTip,"Privacy policy","View our published policy",onPrivacy)
            HorizontalDivider()
            InfoRow(Icons.Default.DeleteOutline,"Delete my account","Request deletion and learn retention rules"){onAction("No account is stored in this build. After login is enabled, a self-service deletion flow and public deletion page will be available.")}
            HorizontalDivider()
            InfoRow(Icons.Default.HelpOutline,"Help & contact","Project contacts will be published after organizational verification"){onAction("The official project contact has not been configured yet. Please do not submit sensitive information.")}
        }
        Text("Imam Raza (a.s.) School is a proposed project. This application does not yet collect payments or hold children's records.",fontSize=11.sp,color=Muted,lineHeight=17.sp)
    }
}
