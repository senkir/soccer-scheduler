package org.enyeinteractive.soccerconvert.network

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import org.enyeinteractive.soccerconvert.network.model.WebContent
import org.enyeinteractive.soccerconvert.network.model.WebLocation
import org.enyeinteractive.soccerconvert.network.model.WebOpponent
import org.enyeinteractive.soccerconvert.network.model.WebSchedule

class HtmlSourceScanner {
    fun parse(source: String): WebContent {
        if (containsHtmlTable(source)) {
            return parseHtml(source)
        }
        return parseRawText(source)
    }

    private fun containsHtmlTable(source: String): Boolean {
        return source.contains(TABLE_MARKER, ignoreCase = true)
    }

    private fun parseHtml(html: String): WebContent {
        val document: Document = Ksoup.parse(html)
        return createContent(
            header = parseHtmlHeader(document),
            schedule = parseSchedule(findTable(document, HOME_AWAY_HEADER)),
            locations = parseLocations(findTable(document, LOCATIONS_HEADER)),
            opponents = parseOpponents(findTable(document, TEAM_INFO_HEADER))
        )
    }

    private fun parseHtmlHeader(document: Document): WebHeader {
        val heading: Element = document.selectFirst(HEADING_TAG)
            ?: throw IllegalArgumentException(MISSING_HEADER_ERROR)
        val lines: List<String> = extractLines(heading)
        if (lines.size < HEADER_LINE_COUNT) {
            throw IllegalArgumentException(MISSING_HEADER_ERROR)
        }
        return WebHeader(
            teamName = lines[0],
            clubSeason = unwrapSeason(lines[1]),
            date = collapseSpaces(lines[2]),
            coach = parseHtmlCoach(document)
        )
    }

    private fun parseHtmlCoach(document: Document): String {
        val firstTable: Element = document.selectFirst(TABLE_TAG)
            ?: throw IllegalArgumentException(MISSING_HEADER_ERROR)
        val cells: List<Element> = firstTable.select(TABLE_CELL_TAG)
        if (cells.size < COACH_CELL_COUNT) {
            return ""
        }
        return cells[1].text().trim()
    }

    private fun findTable(document: Document, headerText: String): Element {
        val tables: List<Element> = document.select(TABLE_TAG)
        val matchedTable: Element? = tables.firstOrNull { table: Element ->
            table.selectFirst(TABLE_ROW_TAG)?.text()?.contains(headerText) == true
        }
        return matchedTable ?: throw IllegalArgumentException("Missing table: $headerText")
    }

    private fun parseSchedule(table: Element): List<WebSchedule> {
        val rows: List<Element> = table.select(TABLE_ROW_TAG).drop(1)
        return rows.map { row: Element -> createSchedule(row) }
    }

    private fun createSchedule(row: Element): WebSchedule {
        val cells: List<Element> = row.select(TABLE_CELL_TAG)
        val homeOrAway: String = cells[HOME_AWAY_INDEX].text().trim()
        return WebSchedule(
            isHome = homeOrAway == HOME_MARKER,
            time = cells[TIME_INDEX].text().trim(),
            date = cells[DATE_INDEX].text().trim(),
            opponentKey = cells[OPPONENT_INDEX].text().trim(),
            locationKey = cells[LOCATION_INDEX].text().trim()
        )
    }

    private fun parseLocations(table: Element): List<WebLocation> {
        val cells: List<Element> = table.select(TABLE_ROW_TAG).drop(1).flatMap { row: Element ->
            row.select(TABLE_CELL_TAG)
        }
        return cells.mapNotNull { cell: Element -> createLocationFromLines(extractLines(cell)) }
    }

    private fun parseOpponents(table: Element): List<WebOpponent> {
        val cells: List<Element> = table.select(TABLE_ROW_TAG).drop(1).flatMap { row: Element ->
            row.select(TABLE_CELL_TAG)
        }
        return cells.mapNotNull { cell: Element -> createOpponentFromLines(extractLines(cell)) }
    }

    private fun parseRawText(source: String): WebContent {
        if (!source.contains(HOME_AWAY_HEADER)) {
            throw IllegalArgumentException("Missing table: $HOME_AWAY_HEADER")
        }
        return createContent(
            header = parseRawHeader(source),
            schedule = parseRawSchedule(source),
            locations = parseRawLocations(source),
            opponents = parseRawOpponents(source)
        )
    }

    private fun parseRawHeader(source: String): WebHeader {
        val headerLines: List<String> = source.lineSequence()
            .map { line: String -> line.trim() }
            .takeWhile { line: String -> !line.contains(HOME_AWAY_HEADER) }
            .filter { line: String -> line.isNotEmpty() }
            .toList()
        if (headerLines.size < HEADER_LINE_COUNT) {
            throw IllegalArgumentException(MISSING_HEADER_ERROR)
        }
        return WebHeader(
            teamName = headerLines[0],
            clubSeason = unwrapSeason(headerLines[1]),
            date = collapseSpaces(headerLines[2]),
            coach = headerLines.getOrElse(COACH_LINE_INDEX) { "" }
        )
    }

    private fun parseRawSchedule(source: String): List<WebSchedule> {
        val lines: List<String> = source.lineSequence()
            .dropWhile { line: String -> !line.contains(HOME_AWAY_HEADER) }
            .drop(1)
            .takeWhile { line: String -> !line.contains(LOCATIONS_HEADER) }
            .filter { line: String -> line.trim().isNotEmpty() }
            .toList()
        return lines.map { line: String -> createRawSchedule(line) }
    }

    private fun createRawSchedule(line: String): WebSchedule {
        val cells: List<String> = line.split(TAB)
        val homeOrAway: String = cells[HOME_AWAY_INDEX].trim()
        return WebSchedule(
            isHome = homeOrAway == HOME_MARKER,
            time = cells[TIME_INDEX].trim(),
            date = cells[DATE_INDEX].trim(),
            opponentKey = cells[OPPONENT_INDEX].trim(),
            locationKey = cells[LOCATION_INDEX].trim()
        )
    }

    private fun parseRawLocations(source: String): List<WebLocation> {
        val lines: List<String> = flattenRawBlock(
            source = source,
            startMarker = LOCATIONS_HEADER,
            endMarker = TEAM_INFO_HEADER
        )
        return lines.chunked(LOCATION_LINE_COUNT).mapNotNull { group: List<String> ->
            createLocationFromLines(group)
        }
    }

    private fun parseRawOpponents(source: String): List<WebOpponent> {
        val lines: List<String> = flattenRawBlock(
            source = source,
            startMarker = TEAM_INFO_HEADER,
            endMarker = null
        )
        return lines.chunked(OPPONENT_LINE_COUNT).mapNotNull { group: List<String> ->
            createOpponentFromLines(group)
        }
    }

    private fun flattenRawBlock(
        source: String,
        startMarker: String,
        endMarker: String?
    ): List<String> {
        val block: String = sliceRawBlock(source, startMarker, endMarker)
        return block.split(TAB).flatMap { cell: String ->
            cell.split(NEWLINE).map { line: String -> line.trim() }.filter { line: String ->
                line.isNotEmpty()
            }
        }
    }

    private fun sliceRawBlock(source: String, startMarker: String, endMarker: String?): String {
        val startIndex: Int = source.indexOf(startMarker)
        if (startIndex < 0) {
            throw IllegalArgumentException("Missing table: $startMarker")
        }
        val afterStart: String = source.substring(startIndex + startMarker.length)
        if (endMarker == null) {
            return afterStart
        }
        val endIndex: Int = afterStart.indexOf(endMarker)
        if (endIndex < 0) {
            return afterStart
        }
        return afterStart.substring(0, endIndex)
    }

    private fun createContent(
        header: WebHeader,
        schedule: List<WebSchedule>,
        locations: List<WebLocation>,
        opponents: List<WebOpponent>
    ): WebContent {
        return WebContent(
            teamName = header.teamName,
            clubSeason = header.clubSeason,
            date = header.date,
            coach = header.coach,
            schedule = schedule,
            locations = locations,
            opponents = opponents
        )
    }

    private fun createLocationFromLines(lines: List<String>): WebLocation? {
        if (lines.isEmpty()) {
            return null
        }
        return WebLocation(
            name = lines.first(),
            address = lines.drop(1).joinToString(ADDRESS_SEPARATOR)
        )
    }

    private fun createOpponentFromLines(lines: List<String>): WebOpponent? {
        if (lines.size < OPPONENT_LINE_COUNT) {
            return null
        }
        return WebOpponent(
            name = lines[0],
            coach = lines[2],
            team = unwrapSeason(lines[1])
        )
    }

    private fun extractLines(element: Element): List<String> {
        val lines: MutableList<String> = mutableListOf()
        val buffer: StringBuilder = StringBuilder()
        element.childNodes().forEach { node: Node ->
            appendNode(node = node, buffer = buffer, lines = lines)
        }
        addLine(rawLine = buffer.toString(), lines = lines)
        return lines
    }

    private fun appendNode(node: Node, buffer: StringBuilder, lines: MutableList<String>) {
        val textNode: TextNode? = node as? TextNode
        if (textNode != null) {
            buffer.append(textNode.text())
            return
        }
        val child: Element? = node as? Element
        if (child != null && child.normalName() == BREAK_TAG) {
            addLine(rawLine = buffer.toString(), lines = lines)
            buffer.clear()
        }
    }

    private fun addLine(rawLine: String, lines: MutableList<String>) {
        val line: String = collapseSpaces(rawLine)
        if (line.isNotEmpty()) {
            lines.add(line)
        }
    }

    private fun unwrapSeason(value: String): String {
        return value.removePrefix(SEASON_PREFIX).removeSuffix(SEASON_SUFFIX)
    }

    private fun collapseSpaces(value: String): String {
        return value.trim().replace(WHITESPACE_REGEX, SINGLE_SPACE)
    }

    private companion object {
        const val TABLE_MARKER: String = "<table"
        const val TABLE_TAG: String = "table"
        const val TABLE_ROW_TAG: String = "tr"
        const val TABLE_CELL_TAG: String = "td"
        const val HEADING_TAG: String = "h2"
        const val HOME_MARKER: String = "H"
        const val HOME_AWAY_HEADER: String = "H/A"
        const val LOCATIONS_HEADER: String = "Locations:"
        const val TEAM_INFO_HEADER: String = "Team Info:"
        const val HOME_AWAY_INDEX: Int = 1
        const val DATE_INDEX: Int = 2
        const val TIME_INDEX: Int = 3
        const val OPPONENT_INDEX: Int = 4
        const val LOCATION_INDEX: Int = 5
        const val HEADER_LINE_COUNT: Int = 3
        const val COACH_LINE_INDEX: Int = 3
        const val COACH_CELL_COUNT: Int = 2
        const val LOCATION_LINE_COUNT: Int = 3
        const val OPPONENT_LINE_COUNT: Int = 3
        const val BREAK_TAG: String = "br"
        const val TAB: String = "\t"
        const val NEWLINE: String = "\n"
        const val ADDRESS_SEPARATOR: String = ", "
        const val SEASON_PREFIX: String = "("
        const val SEASON_SUFFIX: String = ")"
        const val SINGLE_SPACE: String = " "
        const val MISSING_HEADER_ERROR: String = "Missing schedule header"
        val WHITESPACE_REGEX: Regex = Regex("\\s+")
    }
}

private data class WebHeader(
    val teamName: String,
    val clubSeason: String,
    val date: String,
    val coach: String
)
