package org.enyeinteractive.soccerconvert.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import org.enyeinteractive.soccerconvert.fixtures.SampleScheduleHtml
import org.enyeinteractive.soccerconvert.fixtures.SampleScheduleRawText
import org.enyeinteractive.soccerconvert.network.model.WebContent
import org.enyeinteractive.soccerconvert.network.model.WebLocation
import org.enyeinteractive.soccerconvert.network.model.WebOpponent
import org.enyeinteractive.soccerconvert.network.model.WebSchedule

class HtmlSourceScannerTest {
    @Test
    fun parseSampleSchedule() {
        val inputHtml: String = SampleScheduleHtml.value
        val scanner: HtmlSourceScanner = HtmlSourceScanner()
        val actualContent: WebContent = scanner.parse(inputHtml)
        assertSampleContent(actualContent)
    }

    @Test
    fun parseSampleRawSchedule() {
        val inputRawText: String = SampleScheduleRawText.value
        val scanner: HtmlSourceScanner = HtmlSourceScanner()
        val actualContent: WebContent = scanner.parse(inputRawText)
        assertSampleContent(actualContent)
    }

    @Test
    fun parseSampleScheduleJsonRoundTrip() {
        val inputHtml: String = SampleScheduleHtml.value
        val scanner: HtmlSourceScanner = HtmlSourceScanner()
        val actualContent: WebContent = scanner.parse(inputHtml)
        val encodedJson: String = Json.encodeToString(actualContent)
        val decodedContent: WebContent = Json.decodeFromString(encodedJson)
        assertEquals(actualContent, decodedContent)
    }

    private fun assertSampleContent(actualContent: WebContent) {
        val expectedFirstGame: WebSchedule = WebSchedule(
            isHome = false,
            time = "9:00 a.m.",
            date = "Sep 12, 2026, Sat",
            opponentKey = "SWSC-G7B-Coupland",
            locationKey = "Paradise Meadows 3 (North)"
        )
        val expectedLocation: WebLocation = WebLocation(
            name = "Hurricane Ridge Field",
            address = "3002 Mount Angeles Road, Port Angeles, WA 98362"
        )
        val expectedOpponent: WebOpponent = WebOpponent(
            name = "TSC-G7B-James",
            coach = "TylerJames",
            team = "F2025/G7 Te/G7B/TSC G"
        )
        assertEquals("MUSC-G7B-Sacagawea", actualContent.teamName)
        assertEquals("F2025/G7 Te/G7B/MUSC", actualContent.clubSeason)
        assertEquals("Sep 2, 2026", actualContent.date)
        assertEquals("Sacagawea", actualContent.coach)
        assertEquals(8, actualContent.schedule.size)
        assertEquals(6, actualContent.locations.size)
        assertEquals(8, actualContent.opponents.size)
        assertEquals(expectedFirstGame, actualContent.schedule.first())
        assertEquals(expectedLocation, actualContent.locations.first { location: WebLocation ->
            location.name == "Hurricane Ridge Field"
        })
        assertEquals(expectedOpponent, actualContent.opponents.first { opponent: WebOpponent ->
            opponent.name == "TSC-G7B-James"
        })
    }
}
