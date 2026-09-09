package org.enyeinteractive.soccerconvert.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

class CalendarCsvConverterTest {
    @Test
    fun formatDateConvertsRubySample() {
        val converter: CalendarCsvConverter = CalendarCsvConverter()
        val actualDate: String = converter.formatDate("Sep 6, 2025, Sat")
        assertEquals("09/06/2025", actualDate)
    }

    @Test
    fun formatTimeConvertsMorningSample() {
        val converter: CalendarCsvConverter = CalendarCsvConverter()
        val actualTime: String = converter.formatTime("10:30 a.m.")
        assertEquals("10:30", actualTime)
    }

    @Test
    fun formatTimeConvertsAfternoonAndNoon() {
        val converter: CalendarCsvConverter = CalendarCsvConverter()
        assertEquals("13:30", converter.formatTime("1:30 p.m."))
        assertEquals("12:00", converter.formatTime("12:00 p.m."))
        assertEquals("00:00", converter.formatTime("12:00 a.m."))
    }

    @Test
    fun formatLocationFallsBackToLocationKey() {
        val converter: CalendarCsvConverter = CalendarCsvConverter()
        val actualLocation: String = converter.formatLocation("Powerlines Park 4", null)
        assertEquals("Powerlines Park 4", actualLocation)
    }

    @Test
    fun convertSkipsByeAndUsesHtmlAddress() {
        val converter: CalendarCsvConverter = CalendarCsvConverter()
        val inputEvents: List<ScheduledEvent> = listOf(
            ScheduledEvent(
                index = 0,
                isHome = false,
                time = "9:00 a.m.",
                date = "Sep 12, 2026, Sat",
                opponentKey = "SWSC-G7B-Coupland",
                locationKey = "Paradise Meadows 3 (North)",
                group = "G7B",
                location = Location(
                    key = "Paradise Meadows 3 (North)",
                    name = "Paradise Meadows 3 (North)",
                    address = "39000 State Route 706 East, Ashford, WA 98304"
                )
            ),
            ScheduledEvent(
                index = 1,
                isHome = false,
                time = "10:30 a.m.",
                date = "Sep 6, 2025, Sat",
                opponentKey = "(BYE)",
                locationKey = "Powerlines Park 4",
                group = "G7B"
            )
        )
        val actualResult: CalendarCsvResult = converter.convert(inputEvents)
        val actualLines: List<String> = actualResult.csv.lines()
        assertEquals("Start Date, Start Time,  Description, Location, Subject,", actualLines[0])
        assertEquals(
            "09/12/2026, 9:00, SWSC-G7B-Coupland, \"39000 State Route 706 East, Ashford, WA 98304\", Game G7B - Paradise Meadows 3 (North)",
            actualLines[1]
        )
        assertEquals(2, actualLines.size)
        assertTrue(actualResult.errors.any { error: String -> error.contains("BYE") })
    }
}
