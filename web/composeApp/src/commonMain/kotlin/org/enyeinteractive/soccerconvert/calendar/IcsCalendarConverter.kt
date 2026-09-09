package org.enyeinteractive.soccerconvert.calendar

import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

class IcsCalendarConverter(
    private val csvConverter: CalendarCsvConverter = CalendarCsvConverter()
) {
    fun convertEvent(event: ScheduledEvent): String {
        return convert(listOf(event))
    }

    fun convert(events: List<ScheduledEvent>): String {
        val eventBlocks: List<String> = events.mapNotNull { event: ScheduledEvent ->
            createEventBlock(event)
        }
        val lines: List<String> = listOf(
            "BEGIN:VCALENDAR",
            "VERSION:2.0",
            "PRODID:$PRODUCT_ID",
            "CALSCALE:GREGORIAN"
        ) + eventBlocks + listOf("END:VCALENDAR")
        return lines.joinToString(CRLF)
    }

    fun formatStartDateTime(event: ScheduledEvent): String {
        return formatDateTime(date = event.date, time = event.time)
    }

    fun formatEndDateTime(event: ScheduledEvent): String {
        return addOneHour(formatStartDateTime(event))
    }

    private fun createEventBlock(event: ScheduledEvent): String? {
        if (event.opponentKey == BYE_MARKER) {
            return null
        }
        val startDateTime: String = formatStartDateTime(event)
        val endDateTime: String = formatEndDateTime(event)
        val summary: String = csvConverter.formatEventTitle(event.group, event.locationKey)
        val location: String = csvConverter.formatLocation(event.locationKey, event.location)
        return listOf(
            "BEGIN:VEVENT",
            "UID:${createUid(event, startDateTime)}",
            "DTSTAMP:${startDateTime}Z",
            "DTSTART;TZID=$TIME_ZONE:$startDateTime",
            "DTEND;TZID=$TIME_ZONE:$endDateTime",
            "SUMMARY:${escapeIcs(summary)}",
            "DESCRIPTION:${escapeIcs(event.opponentKey)}",
            "LOCATION:${escapeIcs(location)}",
            "END:VEVENT"
        ).joinToString(CRLF)
    }

    private fun formatDateTime(date: String, time: String): String {
        val csvDate: String = csvConverter.formatDate(date)
        val csvTime: String = csvConverter.formatTime(time)
        val dateParts: List<String> = csvDate.split(DATE_SEPARATOR)
        val timeParts: List<String> = csvTime.split(TIME_SEPARATOR)
        val month: String = dateParts[0]
        val day: String = dateParts[1]
        val year: String = dateParts[2]
        val hours: String = padTwoDigits(timeParts[0].toInt())
        val minutes: String = padTwoDigits(timeParts[1].toInt())
        return "$year$month${day}T$hours${minutes}00"
    }

    private fun addOneHour(icsDateTime: String): String {
        val year: Int = icsDateTime.substring(YEAR_START, YEAR_END).toInt()
        val month: Int = icsDateTime.substring(MONTH_START, MONTH_END).toInt()
        val day: Int = icsDateTime.substring(DAY_START, DAY_END).toInt()
        val hour: Int = icsDateTime.substring(HOUR_START, HOUR_END).toInt()
        val minute: String = icsDateTime.substring(MINUTE_START, MINUTE_END)
        val nextHour: Int = hour + GAME_DURATION_HOURS
        if (nextHour < HOURS_IN_DAY) {
            return formatStamp(year = year, month = month, day = day, hour = nextHour, minute = minute)
        }
        return formatStamp(
            year = year,
            month = month,
            day = day + 1,
            hour = nextHour - HOURS_IN_DAY,
            minute = minute
        )
    }

    private fun formatStamp(year: Int, month: Int, day: Int, hour: Int, minute: String): String {
        return "${padFourDigits(year)}${padTwoDigits(month)}${padTwoDigits(day)}T${padTwoDigits(hour)}${minute}00"
    }

    private fun createUid(event: ScheduledEvent, startDateTime: String): String {
        return "game-${event.index}-$startDateTime@$UID_DOMAIN"
    }

    private fun escapeIcs(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace(";", "\\;")
            .replace(",", "\\,")
            .replace("\n", "\\n")
    }

    private fun padTwoDigits(value: Int): String {
        return value.toString().padStart(2, '0')
    }

    private fun padFourDigits(value: Int): String {
        return value.toString().padStart(4, '0')
    }

    private companion object {
        const val PRODUCT_ID: String = "-//Enye Interactive//Soccer Convert//EN"
        const val TIME_ZONE: String = "America/Los_Angeles"
        const val BYE_MARKER: String = "(BYE)"
        const val UID_DOMAIN: String = "enyeinteractive.org"
        const val CRLF: String = "\r\n"
        const val DATE_SEPARATOR: String = "/"
        const val TIME_SEPARATOR: String = ":"
        const val GAME_DURATION_HOURS: Int = 1
        const val HOURS_IN_DAY: Int = 24
        const val YEAR_START: Int = 0
        const val YEAR_END: Int = 4
        const val MONTH_START: Int = 4
        const val MONTH_END: Int = 6
        const val DAY_START: Int = 6
        const val DAY_END: Int = 8
        const val HOUR_START: Int = 9
        const val HOUR_END: Int = 11
        const val MINUTE_START: Int = 11
        const val MINUTE_END: Int = 13
    }
}
