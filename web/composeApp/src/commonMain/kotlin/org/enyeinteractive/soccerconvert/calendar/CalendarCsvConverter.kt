package org.enyeinteractive.soccerconvert.calendar

import org.enyeinteractive.soccerconvert.ui.data.Location
import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

data class CalendarCsvResult(
    val csv: String,
    val errors: List<String>
)

class CalendarCsvConverter {
    fun convert(events: List<ScheduledEvent>): CalendarCsvResult {
        val errors: MutableList<String> = mutableListOf()
        val rows: List<String> = events.mapNotNull { event: ScheduledEvent ->
            createRow(event = event, errors = errors)
        }
        val csv: String = (listOf(HEADER) + rows).joinToString("\n")
        return CalendarCsvResult(csv = csv, errors = errors)
    }

    fun formatDate(date: String): String {
        val match: MatchResult = DATE_REGEX.find(date.trim())
            ?: throw IllegalArgumentException("Invalid date: $date")
        val monthNumber: Int = MONTH_NUMBERS[match.groupValues[1].lowercase()]
            ?: throw IllegalArgumentException("Invalid month: $date")
        val day: Int = match.groupValues[2].toInt()
        val year: Int = match.groupValues[3].toInt()
        return "${padTwoDigits(monthNumber)}/${padTwoDigits(day)}/$year"
    }

    fun formatTime(time: String): String {
        val trimmedTime: String = time.trim()
        val digitsMatch: MatchResult = TIME_DIGITS_REGEX.find(trimmedTime)
            ?: throw IllegalArgumentException("Invalid time: $time")
        val parts: List<String> = digitsMatch.value.split(":")
        val hours: Int = parts[0].toInt()
        val minutes: String = if (parts.size > 1) parts[1] else "00"
        val lowerTime: String = trimmedTime.lowercase()
        val convertedHours: Int = convertHours(
            hours = hours,
            isAfternoon = lowerTime.contains(PM_MARKER),
            isMorning = lowerTime.contains(AM_MARKER)
        )
        return formatHourMinute(hours = convertedHours, minutes = minutes)
    }

    fun formatLocation(locationKey: String, location: Location?): String {
        val address: String? = location?.address
        if (!address.isNullOrBlank()) {
            return address
        }
        return locationKey
    }

    fun formatEventTitle(group: String, locationKey: String): String {
        return "$TITLE_PREFIX$group$TITLE_SEPARATOR$locationKey"
    }

    private fun createRow(event: ScheduledEvent, errors: MutableList<String>): String? {
        if (event.opponentKey == BYE_MARKER) {
            errors.add("ERROR: BYE on date ${event.date}")
            return null
        }
        return try {
            createCsvLine(
                listOf(
                    formatDate(event.date),
                    formatTime(event.time),
                    event.opponentKey,
                    formatLocation(event.locationKey, event.location),
                    formatEventTitle(event.group, event.locationKey)
                )
            )
        } catch (error: Exception) {
            errors.add("ERROR: date parse error $error")
            null
        }
    }

    private fun convertHours(hours: Int, isAfternoon: Boolean, isMorning: Boolean): Int {
        if (isAfternoon && hours != NOON_HOUR) {
            return hours + NOON_HOUR
        }
        if (isMorning && hours == NOON_HOUR) {
            return 0
        }
        return hours
    }

    private fun formatHourMinute(hours: Int, minutes: String): String {
        if (hours == 0) {
            return "00:$minutes"
        }
        return "$hours:$minutes"
    }

    private fun createCsvLine(columns: List<String>): String {
        return columns.joinToString(", ") { column: String -> quoteIfNeeded(column) }
    }

    private fun quoteIfNeeded(value: String): String {
        if (value.contains(",") || value.contains("\"")) {
            return "\"${value.replace("\"", "\"\"")}\""
        }
        return value
    }

    private fun padTwoDigits(value: Int): String {
        return value.toString().padStart(2, '0')
    }

    private companion object {
        const val HEADER: String = "Start Date, Start Time,  Description, Location, Subject,"
        const val BYE_MARKER: String = "(BYE)"
        const val TITLE_PREFIX: String = "Game "
        const val TITLE_SEPARATOR: String = " - "
        const val AM_MARKER: String = "a.m."
        const val PM_MARKER: String = "p.m."
        const val NOON_HOUR: Int = 12
        val DATE_REGEX: Regex = Regex("([A-Za-z]+)\\s+(\\d{1,2}),\\s*(\\d{4})")
        val TIME_DIGITS_REGEX: Regex = Regex("^\\d{1,2}(?::\\d{2})?")
        val LETTERS_SPACES_REGEX: Regex = Regex("^[A-Za-z ]+")
        val MONTH_NUMBERS: Map<String, Int> = mapOf(
            "jan" to 1, "january" to 1,
            "feb" to 2, "february" to 2,
            "mar" to 3, "march" to 3,
            "apr" to 4, "april" to 4,
            "may" to 5,
            "jun" to 6, "june" to 6,
            "jul" to 7, "july" to 7,
            "aug" to 8, "august" to 8,
            "sep" to 9, "sept" to 9, "september" to 9,
            "oct" to 10, "october" to 10,
            "nov" to 11, "november" to 11,
            "dec" to 12, "december" to 12
        )

    }
}
