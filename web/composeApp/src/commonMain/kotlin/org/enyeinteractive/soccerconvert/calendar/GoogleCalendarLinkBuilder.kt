package org.enyeinteractive.soccerconvert.calendar

import org.enyeinteractive.soccerconvert.ui.data.ScheduledEvent

class GoogleCalendarLinkBuilder(
    private val csvConverter: CalendarCsvConverter = CalendarCsvConverter(),
    private val icsConverter: IcsCalendarConverter = IcsCalendarConverter(csvConverter)
) {
    fun createTemplateUrl(event: ScheduledEvent): String {
        val summary: String = csvConverter.formatEventTitle(event.group, event.locationKey)
        val location: String = csvConverter.formatLocation(event.locationKey, event.location)
        val dates: String = "${icsConverter.formatStartDateTime(event)}/${icsConverter.formatEndDateTime(event)}"
        return "$TEMPLATE_BASE&text=${encodeQuery(summary)}" +
            "&dates=$dates" +
            "&details=${encodeQuery(event.opponentKey)}" +
            "&location=${encodeQuery(location)}"
    }

    private fun encodeQuery(value: String): String {
        return buildString {
            value.forEach { character: Char ->
                appendEncodedCharacter(character)
            }
        }
    }

    private fun StringBuilder.appendEncodedCharacter(character: Char) {
        if (isUnreserved(character)) {
            append(character)
            return
        }
        character.toString().encodeToByteArray().forEach { byte: Byte ->
            val unsigned: Int = byte.toInt() and BYTE_MASK
            append('%')
            append(unsigned.toString(HEX_RADIX).uppercase().padStart(HEX_PAD, '0'))
        }
    }

    private fun isUnreserved(character: Char): Boolean {
        return character.isLetterOrDigit() ||
            character == '-' ||
            character == '_' ||
            character == '.' ||
            character == '~'
    }

    private companion object {
        const val TEMPLATE_BASE: String =
            "https://calendar.google.com/calendar/render?action=TEMPLATE"
        const val BYTE_MASK: Int = 0xFF
        const val HEX_RADIX: Int = 16
        const val HEX_PAD: Int = 2
    }
}
