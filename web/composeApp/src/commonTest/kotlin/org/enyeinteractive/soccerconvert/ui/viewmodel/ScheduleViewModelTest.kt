package org.enyeinteractive.soccerconvert.ui.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.enyeinteractive.soccerconvert.fixtures.SampleScheduleHtml
import org.enyeinteractive.soccerconvert.fixtures.SampleScheduleRawText
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduleMetadata

class ScheduleViewModelTest {
    @Test
    fun onSubmitParsesPastedHtml() {
        val inputHtml: String = SampleScheduleHtml.value
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        viewModel.onSubmit(inputHtml)
        val actualState: ScheduleUiState = viewModel.uiState.value
        assertNull(actualState.errorMessage)
        assertEquals(8, actualState.events.size)
        assertEquals(6, actualState.locations.size)
        assertEquals("SWSC-G7B-Coupland", actualState.events.first().opponentKey)
        assertEquals("Somerset West", actualState.events.first().opponent?.name)
        assertEquals("Hurricane Ridge Field", actualState.locations.first { location: Location ->
            location.name == "Hurricane Ridge Field"
        }.name)
        assertEquals(expectedSampleMetadata(), actualState.scheduleMetadata)
    }

    @Test
    fun onSubmitParsesPastedRawText() {
        val inputRawText: String = SampleScheduleRawText.value
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        viewModel.onSubmit(inputRawText)
        val actualState: ScheduleUiState = viewModel.uiState.value
        assertNull(actualState.errorMessage)
        assertEquals(8, actualState.events.size)
        assertEquals(expectedSampleMetadata(), actualState.scheduleMetadata)
    }

    @Test
    fun onSubmitRejectsRemoteUrl() {
        val inputUrl: String = "https://schedules.compumetics.com/Fall2026/MUSC/example.html"
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        viewModel.onSubmit(inputUrl)
        val actualState: ScheduleUiState = viewModel.uiState.value
        val errorMessage: String? = actualState.errorMessage
        assertTrue(errorMessage != null && errorMessage.contains("page source"))
        assertEquals(0, actualState.events.size)
    }

    @Test
    fun generateCalendarWritesSampleCsv() {
        val inputHtml: String = SampleScheduleHtml.value
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        viewModel.onSubmit(inputHtml)
        viewModel.generateCalendar()
        val actualState: ScheduleUiState = viewModel.uiState.value
        val actualLines: List<String> = actualState.calendarCsv.lines()
        assertEquals("Start Date, Start Time,  Description, Location, Subject,", actualLines.first())
        assertTrue(actualLines[1].startsWith("09/12/2026, 9:00, SWSC-G7B-Coupland"))
        assertNull(actualState.errorMessage)
    }

    @Test
    fun generateCalendarRequiresEvents() {
        val viewModel: ScheduleViewModel = ScheduleViewModel()
        viewModel.generateCalendar()
        val actualState: ScheduleUiState = viewModel.uiState.value
        val errorMessage: String? = actualState.errorMessage
        assertTrue(errorMessage != null && errorMessage.contains("Submit"))
        assertEquals("", actualState.calendarCsv)
    }

    private fun expectedSampleMetadata(): ScheduleMetadata {
        return ScheduleMetadata(
            team = "MUSC-G7B-Sacagawea",
            coach = "Sacagawea",
            club = "MUSC",
            group = "Grade 7 Boys",
            season = "F2025"
        )
    }
}
