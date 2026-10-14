package org.imamraza.schoolmission

import org.junit.Assert.*
import org.junit.Test

class MissionDataTest {
  @Test fun catalogHasThreeGenuinePacks() {
    assertEquals(3, MissionContent.packs.size)
    assertEquals(listOf(12,20,30), MissionContent.packs.map { it.items.size })
    assertEquals(3, MissionContent.packs.map { it.id }.distinct().size)
  }
  @Test fun registrationComesFirst() {
    assertEquals("Legal registration", MissionContent.stages.first().title)
    assertTrue(MissionContent.stages.all { it.status == "Not started" })
  }
  @Test fun noReligiousDepictionsInStickerDescriptions() {
    val prohibited = listOf("shrine","holy portrait","sacred figure")
    MissionContent.packs.forEach { pack ->
      prohibited.forEach { word -> assertFalse(pack.description.lowercase().contains(word)) }
    }
  }
  @Test fun privacyHasPublicHttpsUrl() {
    assertTrue(MissionContent.privacyUrl.startsWith("https://"))
  }
}
