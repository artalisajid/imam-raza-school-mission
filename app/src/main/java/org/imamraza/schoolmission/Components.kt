package org.imamraza.schoolmission

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*

val Forest = Color(0xFF073E33)
val Evergreen = Color(0xFF0A5140)
val Gold = Color(0xFFE6B65D)
val Ivory = Color(0xFFFFFBF3)
val Paper = Color(0xFFF6F0E5)
val Ink = Color(0xFF162F29)
val Muted = Color(0xFF65786F)
val AccentMint = Color(0xFFDDEEE2)

@Composable fun MissionTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme=lightColorScheme(primary=Evergreen,onPrimary=Color.White,secondary=Gold,background=Ivory,surface=Color.White,onSurface=Ink,outline=Color(0xFFE4DDD0)), content=content)
}

@Composable fun PageTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement=Arrangement.spacedBy(5.dp)) {
        Text(title,fontFamily=FontFamily.Serif,fontWeight=FontWeight.Bold,fontSize=26.sp,lineHeight=31.sp,color=Forest)
        if(subtitle!=null) Text(subtitle,style=MaterialTheme.typography.bodyMedium,color=Muted,lineHeight=21.sp)
    }
}

@Composable fun SurfaceCard(modifier: Modifier=Modifier,background:Color=Color.White,content:@Composable ColumnScope.()->Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(background).border(1.dp,Color(0xFFE8E1D4),RoundedCornerShape(22.dp)).padding(17.dp),verticalArrangement=Arrangement.spacedBy(12.dp),content=content)
}
@Composable fun GoldButton(text:String,onClick:()->Unit,modifier:Modifier=Modifier,enabled:Boolean=true) {
    Box(modifier.fillMaxWidth().heightIn(min=54.dp).clip(RoundedCornerShape(16.dp)).background(if(enabled) Brush.horizontalGradient(listOf(Gold,Color(0xFFF6D88F))) else Brush.horizontalGradient(listOf(Color.LightGray,Color.LightGray))).clickable(enabled=enabled,onClick=onClick).padding(14.dp),contentAlignment=Alignment.Center) {
        Text(text,color=Forest,fontWeight=FontWeight.ExtraBold,fontSize=15.sp,textAlign=TextAlign.Center)
    }
}
@Composable fun ForestButton(text:String,onClick:()->Unit,modifier:Modifier=Modifier) {
    Box(modifier.fillMaxWidth().heightIn(min=52.dp).clip(RoundedCornerShape(15.dp)).background(Forest).clickable(onClick=onClick).padding(14.dp),contentAlignment=Alignment.Center) {
        Text(text,color=Color.White,fontWeight=FontWeight.Bold,fontSize=14.sp)
    }
}
@Composable fun OutlineButton(text:String,onClick:()->Unit,modifier:Modifier=Modifier) {
    Box(modifier.fillMaxWidth().heightIn(min=48.dp).clip(RoundedCornerShape(14.dp)).border(1.dp,Color(0xFFB3C6B9),RoundedCornerShape(14.dp)).clickable(onClick=onClick).padding(12.dp),contentAlignment=Alignment.Center) {
        Text(text,color=Forest,fontWeight=FontWeight.SemiBold,fontSize=14.sp)
    }
}
@Composable fun Tag(text:String,background:Color=AccentMint,color:Color=Forest) {
    Text(text,modifier=Modifier.clip(RoundedCornerShape(50)).background(background).padding(horizontal=11.dp,vertical=6.dp),color=color,fontSize=11.sp,fontWeight=FontWeight.SemiBold)
}
@Composable fun InfoRow(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,note:String?=null,onClick:(()->Unit)?=null) {
    Row(Modifier.fillMaxWidth().then(if(onClick!=null)Modifier.clickable(onClick=onClick) else Modifier).padding(vertical=12.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(AccentMint),contentAlignment=Alignment.Center) {
            Icon(icon,null,tint=Evergreen,modifier=Modifier.size(22.dp))
        }
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)) {
            Text(title,color=Ink,fontWeight=FontWeight.SemiBold,fontSize=14.sp)
            if(note!=null) Text(note,color=Muted,fontSize=12.sp,lineHeight=17.sp)
        }
        if(onClick!=null) Icon(Icons.Default.ChevronRight,null,tint=Muted)
    }
}

@Composable fun SchoolIllustration(modifier:Modifier=Modifier) {
    Canvas(modifier.fillMaxWidth().height(175.dp).clip(RoundedCornerShape(18.dp)).background(Brush.verticalGradient(listOf(Color(0xFFD9EBE1),Color(0xFFF1DCAF))))) {
        val w=size.width; val h=size.height
        drawCircle(Color(0xFFFFE5A2),w*.12f,androidx.compose.ui.geometry.Offset(w*.83f,h*.18f))
        drawRect(Color(0xFFDBC098),androidx.compose.ui.geometry.Offset(w*.15f,h*.38f),androidx.compose.ui.geometry.Size(w*.72f,h*.51f))
        drawRect(Color(0xFFFAEACA),androidx.compose.ui.geometry.Offset(w*.12f,h*.47f),androidx.compose.ui.geometry.Size(w*.78f,h*.41f))
        drawRect(Color(0xFFAA8055),androidx.compose.ui.geometry.Offset(w*.13f,h*.37f),androidx.compose.ui.geometry.Size(w*.76f,h*.065f))
        for(row in 0..1) for(col in 0..4) {
            val x=w*(.18f+col*.143f); val y=h*(.49f+row*.20f)
            drawRoundRect(Color(0xFF387968),androidx.compose.ui.geometry.Offset(x,y),androidx.compose.ui.geometry.Size(w*.075f,h*.13f),androidx.compose.ui.geometry.CornerRadius(5.dp.toPx()))
        }
        drawRect(Color(0xFF8FAC70),androidx.compose.ui.geometry.Offset(0f,h*.89f),androidx.compose.ui.geometry.Size(w,h*.11f))
        drawRect(Forest,androidx.compose.ui.geometry.Offset(w*.47f,h*.70f),androidx.compose.ui.geometry.Size(w*.12f,h*.19f))
        listOf(.07f,.93f).forEach { x ->
            drawRect(Color(0xFF705C3C),androidx.compose.ui.geometry.Offset(w*x,h*.54f),androidx.compose.ui.geometry.Size(w*.018f,h*.36f))
            drawCircle(Color(0xFF478B62),w*.070f,androidx.compose.ui.geometry.Offset(w*x,h*.45f))
            drawCircle(Color(0xFF569E6C),w*.045f,androidx.compose.ui.geometry.Offset(w*(x+.02f),h*.38f))
        }
    }
}
@Composable fun StickerPreview(pack:StickerPack,modifier:Modifier=Modifier,large:Boolean=false) {
    Box(modifier.clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(Color(pack.accent).copy(alpha=.22f),Color(0xFFFFF7E7)))),contentAlignment=Alignment.Center) {
        Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center) {
            Text(pack.items.take(if(large) 6 else 4).joinToString(" "),fontSize=if(large) 26.sp else 21.sp,lineHeight=if(large) 42.sp else 32.sp,textAlign=TextAlign.Center,maxLines=3)
            if(large) Text("ORIGINAL DIGITAL COLLECTION",fontSize=10.sp,letterSpacing=1.sp,fontWeight=FontWeight.Bold,color=Forest)
        }
    }
}
