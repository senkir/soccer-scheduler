package org.enyeinteractive.soccerconvert.calendar

import kotlin.test.Test
import kotlin.test.assertTrue
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

class GoogleCalendarLinkBuilderTest {
    @Test
    fun createTemplateUrlWritesSampleKickoff() {
        val builder: GoogleCalendarLinkBuilder = GoogleCalendarLinkBuilder()
        val actualUrl: String = builder.createTemplateUrl(createSampleFirstGame())
        assertTrue(actualUrl.startsWith("https://calendar.google.com/calendar/render?action=TEMPLATE"))
        assertTrue(actualUrl.contains("dates=20260912T090000/20260912T100000"))
        assertTrue(actualUrl.contains("details=SWSC-G7B-Coupland"))
        assertTrue(actualUrl.contains("text=Game%20G7B%20-%20Paradise%20Meadows%203%20%28North%29"))
        assertTrue(actualUrl.contains("location=39000%20State%20Route%20706%20East%2C%20Ashford%2C%20WA%2098304"))
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
