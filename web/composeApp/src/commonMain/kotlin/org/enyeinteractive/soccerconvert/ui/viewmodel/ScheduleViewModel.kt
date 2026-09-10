package org.enyeinteractive.soccerconvert.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.enyeinteractive.soccerconvert.calendar.CalendarCsvConverter
import org.enyeinteractive.soccerconvert.calendar.CalendarCsvResult
import org.enyeinteractive.soccerconvert.calendar.GoogleCalendarLinkBuilder
import org.enyeinteractive.soccerconvert.calendar.IcsCalendarConverter
import org.enyeinteractive.soccerconvert.network.HtmlSourceScanner
import org.enyeinteractive.soccerconvert.network.model.WebContent
import org.enyeinteractive.soccerconvert.network.model.WebLocation
import org.enyeinteractive.soccerconvert.network.model.WebOpponent
import org.enyeinteractive.soccerconvert.network.model.WebSchedule
import org.enyeinteractive.soccerconvert.platform.downloadTextFile
import org.enyeinteractive.soccerconvert.platform.openUrl
import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.Opponent
import org.enyeinteractive.soccerconvert.ui.data.ScheduleMetadata
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

sealed interface ScheduleError {
    data object UrlPaste : ScheduleError
    data object ParseFailed : ScheduleError
    data object EmptyEvents : ScheduleError
    data object EmptyCsv : ScheduleError
    data object CalendarExportFailed : ScheduleError
    data class Conversion(val details: List<String>) : ScheduleError
}

data class ScheduleUiState(
    val scheduleMetadata: ScheduleMetadata? = null,
    val events: List<ScheduledEvent> = emptyList(),
    val locations: List<Location> = emptyList(),
    val calendarCsv: String = "",
    val error: ScheduleError? = null
)

class ScheduleViewModel(
    private val scanner: HtmlSourceScanner = HtmlSourceScanner(),
    private val calendarConverter: CalendarCsvConverter = CalendarCsvConverter(),
    private val icsConverter: IcsCalendarConverter = IcsCalendarConverter(calendarConverter),
    private val googleLinkBuilder: GoogleCalendarLinkBuilder = GoogleCalendarLinkBuilder(
        csvConverter = calendarConverter,
        icsConverter = icsConverter
    )
) : ViewModel() {

    private var locationMap = mutableMapOf<String, Location>()
    private var opponentMap = mutableMapOf<String, Opponent>()
    private val _uiState: MutableStateFlow<ScheduleUiState> = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun onSubmit(htmlInput: String) {
        val trimmedInput: String = htmlInput.trim()
        if (isRemoteUrl(trimmedInput)) {
            _uiState.value = ScheduleUiState(error = ScheduleError.UrlPaste)
            return
        }
        try {
            val content: WebContent = scanner.parse(trimmedInput)
            _uiState.value = createSuccessState(content)
        } catch (_: Exception) {
            _uiState.value = ScheduleUiState(error = ScheduleError.ParseFailed)
        }
        generateCalendar()
    }

    private fun isRemoteUrl(value: String): Boolean {
        val lowerValue: String = value.lowercase()
        return lowerValue.startsWith(HTTP_PREFIX) || lowerValue.startsWith(HTTPS_PREFIX)
    }

    private fun createSuccessState(content: WebContent): ScheduleUiState {
        generateLocationMap(content.locations)
        generateOpponentMap(content.opponents)
        val groupCode: String = findGroupCode(content)
        return ScheduleUiState(
            scheduleMetadata = createScheduleMetadata(content, groupCode),
            events = createScheduledEvents(content.schedule, groupCode),
            locations = createLocations(content.locations)
        )
    }

    private fun createScheduleMetadata(content: WebContent, groupCode: String): ScheduleMetadata {
        val seasonParts: List<String> = content.clubSeason.split(SEASON_PATH_SEPARATOR)
        return ScheduleMetadata(
            team = content.teamName,
            coach = content.coach,
            club = seasonParts.lastOrNull() ?: "",
            group = groupCode,
            season = seasonParts.firstOrNull() ?: ""
        )
    }

    private fun findGroupCode(content: WebContent): String {
        val seasonParts: List<String> = content.clubSeason.split(SEASON_PATH_SEPARATOR)
        val teamTokens: Set<String> = content.teamName.split(TEAM_NAME_SEPARATOR).toSet()
        val matchedGroup: String? = seasonParts.firstOrNull { part: String ->
            teamTokens.contains(part)
        }
        return matchedGroup ?: ""
    }

    private fun generateLocationMap(locations: List<WebLocation>) {
        locations.forEach { l ->
            locationMap[l.name] = Location(
                key = l.name,
                name = l.name,
                address = l.address
            )
        }
    }


    private fun generateOpponentMap(opponents: List<WebOpponent>) {
        opponents.forEach { opponent: WebOpponent ->
            opponentMap[opponent.name] = Opponent(
                key = opponent.name,
                name = findClubName(opponent.name),
                teamIdentifier = opponent.team
            )
        }
    }

    private fun findClubName(opponentKey: String): String {
        val clubCode: String = opponentKey.split(TEAM_NAME_SEPARATOR).first()
        return CLUB_NAMES[clubCode] ?: clubCode
    }

    private fun createScheduledEvents(
        games: List<WebSchedule>,
        groupCode: String
    ): List<ScheduledEvent> {
        return games.mapIndexed { index: Int, game: WebSchedule ->
            ScheduledEvent(
                index = index,
                isHome = game.isHome,
                time = game.time,
                date = game.date,
                opponentKey = game.opponentKey,
                locationKey = game.locationKey,
                group = groupCode,
                location = locationMap[game.locationKey],
                opponent = opponentMap[game.opponentKey]
            )
        }
    }

    private fun createLocations(locations: List<WebLocation>): List<Location> {
        return locations.map { location: WebLocation ->
            Location(
                key = location.name,
                name = location.name,
                address = location.address
            )
        }
    }


    fun generateCalendar() {
        val currentState: ScheduleUiState = _uiState.value
        if (currentState.events.isEmpty()) {
            _uiState.value = currentState.copy(
                calendarCsv = "",
                error = ScheduleError.EmptyEvents
            )
            return
        }
        val result: CalendarCsvResult = calendarConverter.convert(currentState.events)
        val conversionError: ScheduleError? = result.errors.takeIf { errors: List<String> ->
            errors.isNotEmpty()
        }?.let { details: List<String> ->
            ScheduleError.Conversion(details)
        }
        _uiState.value = currentState.copy(
            calendarCsv = result.csv,
            error = conversionError
        )
    }

    fun addEventToCalendar(event: ScheduledEvent) {
        if (event.opponentKey == BYE_MARKER) {
            return
        }
        try {
            val icsContent: String = icsConverter.convertEvent(event)
            downloadTextFile(createEventFileName(event), ICS_MIME_TYPE, icsContent)
            openUrl(googleLinkBuilder.createTemplateUrl(event))
        } catch (_: Exception) {
            _uiState.value = _uiState.value.copy(error = ScheduleError.CalendarExportFailed)
        }
    }

    fun downloadCalendarFiles() {
        val currentState: ScheduleUiState = _uiState.value
        if (currentState.calendarCsv.isEmpty()) {
            _uiState.value = currentState.copy(error = ScheduleError.EmptyCsv)
            return
        }
        try {
            val baseName: String = createSeasonFileName(currentState.scheduleMetadata)
            downloadTextFile("$baseName.csv", CSV_MIME_TYPE, currentState.calendarCsv)
            downloadTextFile(
                "$baseName.ics",
                ICS_MIME_TYPE,
                icsConverter.convert(currentState.events)
            )
            openUrl(GOOGLE_IMPORT_URL)
        } catch (_: Exception) {
            _uiState.value = currentState.copy(error = ScheduleError.CalendarExportFailed)
        }
    }

    private fun createEventFileName(event: ScheduledEvent): String {
        val startDateTime: String = icsConverter.formatStartDateTime(event)
        return "soccer-game-$startDateTime.ics"
    }

    private fun createSeasonFileName(metadata: ScheduleMetadata?): String {
        val teamName: String? = metadata?.team?.trim()
        if (teamName.isNullOrBlank()) {
            return DEFAULT_FILE_NAME
        }
        return teamName
    }

    private companion object {
        const val CONTACT_MAILTO_URL: String =
            "mailto:contact@mirthandmalice.co?subject=Soccer%20Scheduler"
        const val HTTP_PREFIX: String = "http://"
        const val HTTPS_PREFIX: String = "https://"
        const val SEASON_PATH_SEPARATOR: String = "/"
        const val TEAM_NAME_SEPARATOR: String = "-"
        const val BYE_MARKER: String = "(BYE)"
        const val DEFAULT_FILE_NAME: String = "soccer-schedule"
        const val CSV_MIME_TYPE: String = "text/csv"
        const val ICS_MIME_TYPE: String = "text/calendar"
        const val GOOGLE_IMPORT_URL: String =
            "https://calendar.google.com/calendar/u/0/r/settings/export"
        val CLUB_NAMES: Map<String, String> = mapOf(
            "AUSCR" to "Aloha United",
            "AJRSC" to "Albion Juniors",
            "MUSC" to "Milltown",
            "OHSC" to "Oak Hills",
            "SWSC" to "Somerset West",
            "SYSC" to "Sherwood",
            "TSC" to "Tualitin",
            "WSC" to "Westside"
        )
    }

    fun contactEmail() {
        openUrl(CONTACT_MAILTO_URL)
    }
}
