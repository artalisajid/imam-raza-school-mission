package org.imamraza.schoolmission

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class StickerPack(val id: String, val name: String, val tagline: String, val description: String, val items: List<String>, val accent: Long)
data class SchoolStage(val title: String, val note: String, val status: String = "Not started")

object MissionContent {
    const val privacyUrl = "https://artalisajid.github.io/imam-raza-school-mission/privacy.html"
    const val contactEmail = "CONTACT_NOT_YET_CONFIGURED"
    val packs = listOf(
        StickerPack("little_learners", "Little Learners", "A joyful start to knowledge", "12 original educational illustrations designed for personal use, sharing and everyday encouragement.", listOf("📚","✏️","🎒","📖","🌱","🎓","🧠","⭐","🖌️","🧩","💡","🌍"), 0xFFE5B65E),
        StickerPack("seeds_of_knowledge", "Seeds of Knowledge", "Curiosity makes us grow", "20 original nature and learning illustrations for thoughtful messages and daily inspiration.", listOf("🌱","🌿","🌳","🍀","🌼","🌻","📗","💧","🍃","🌤️","🌍","✏️","💡","📚","🌾","🌺","🪴","🔍","🎒","⭐"), 0xFF6AA887),
        StickerPack("bright_minds", "Bright Minds", "Think, learn, build", "30 original science, creativity and education-themed illustrations for personal digital use.", listOf("💡","📐","🔬","🧠","🪐","🌍","🧮","🔭","🎨","📘","✏️","⚙️","🔎","💻","🧪","📊","🪄","🎒","📏","⭐","🌱","🏆","🔢","🌈","📖","🧩","🛠️","📚","📗","🎓"), 0xFF71A7B6)
    )
    val stages = listOf(
        SchoolStage("Legal registration", "Establish the lawful entity, its governance and required permissions"),
        SchoolStage("Land feasibility and acquisition", "Check suitable Karachi sites, title, zoning, access and costs"),
        SchoolStage("Design and approvals", "Prepare compliant architectural plans and secure permissions"),
        SchoolStage("Construction", "Develop safe classrooms and essential school facilities"),
        SchoolStage("School readiness", "Curriculum, qualified teachers, equipment, safeguarding and approvals"),
        SchoolStage("School opening", "Begin Nursery through Class 7 only once opening requirements are met")
    )
    val grades = listOf("Nursery", "Kindergarten", "Preparatory", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7")
}

object PublicContentRepository {
    suspend fun getPublishedUpdates(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        if (BuildConfig.SUPABASE_URL.isBlank() || BuildConfig.SUPABASE_PUBLISHABLE_KEY.isBlank()) return@withContext emptyList()
        try {
            val endpoint = BuildConfig.SUPABASE_URL.trimEnd('/') + "/rest/v1/project_updates?select=title,summary&published=eq.true&order=created_at.desc&limit=5"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                connectTimeout = 7000
                readTimeout = 7000
                setRequestProperty("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
                setRequestProperty("Accept", "application/json")
            }
            if (conn.responseCode !in 200..299) return@withContext emptyList()
            val arr = JSONArray(conn.inputStream.bufferedReader().use { it.readText() })
            (0 until arr.length()).map { val obj = arr.getJSONObject(it); obj.optString("title") to obj.optString("summary") }
        } catch (_: Exception) { emptyList() }
    }
}
