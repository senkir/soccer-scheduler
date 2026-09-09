package org.enyeinteractive.soccerconvert.calendar

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

class IcsCalendarConverterTest {
    @Test
    fun convertEventWritesSampleKickoff() {
        val converter: IcsCalendarConverter = IcsCalendarConverter()
        val actualIcs: String = converter.convertEvent(createSampleFirstGame())
        assertTrue(actualIcs.contains("DTSTART;TZID=America/Los_Angeles:20260912T090000"))
        assertTrue(actualIcs.contains("DTEND;TZID=America/Los_Angeles:20260912T100000"))
        assertTrue(actualIcs.contains("SUMMARY:Game G7B - Paradise Meadows 3 (North)"))
        assertTrue(actualIcs.contains("DESCRIPTION:SWSC-G7B-Coupland"))
        assertTrue(actualIcs.contains("LOCATION:39000 State Route 706 East\\, Ashford\\, WA 98304"))
        assertTrue(actualIcs.startsWith("BEGIN:VCALENDAR"))
        assertTrue(actualIcs.contains("END:VCALENDAR"))
    }

    @Test
    fun convertSkipsByeGames() {
        val converter: IcsCalendarConverter = IcsCalendarConverter()
        val inputEvents: List<ScheduledEvent> = listOf(
            createSampleFirstGame(),
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
        val actualIcs: String = converter.convert(inputEvents)
        assertTrue(actualIcs.contains("DTSTART;TZID=America/Los_Angeles:20260912T090000"))
        assertFalse(actualIcs.contains("Powerlines Park"))
        assertEquals(1, actualIcs.split("BEGIN:VEVENT").size - 1)
    }

    @Test
    fun formatStartDateTimePadsSingleDigitHour() {
        val converter: IcsCalendarConverter = IcsCalendarConverter()
        val actualStart: String = converter.formatStartDateTime(createSampleFirstGame())
        val actualEnd: String = converter.formatEndDateTime(createSampleFirstGame())
        assertEquals("20260912T090000", actualStart)
        assertEquals("20260912T100000", actualEnd)
    }

    private fun createSampleFirstGame(): ScheduledEvent {
        return ScheduledEvent(
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
        )
    }
}
