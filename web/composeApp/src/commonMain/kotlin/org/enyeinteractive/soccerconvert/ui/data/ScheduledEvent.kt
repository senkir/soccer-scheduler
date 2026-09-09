package org.enyeinteractive.soccerconvert.ui.data

data class ScheduledEvent(
    val index: Int,
    val isHome: Boolean,
    val time: String,
    val date: String,
    val opponentKey: String,
    val locationKey: String,
    val group: String,
    val location: Location?=null,
    val opponent: Opponent?=null
)

interface Team {
    val key: String
    val name: String
    val teamIdentifier: String
}

data class ScheduleMetadata(
    val team: String,
    val coach: String, // eg RAMIREZ
    val club: String, // eg MUSC
    val group: String, // eg: G7B
    val season: String, // eg: F2025
)

data class Opponent(
    override val key: String,
    override val name: String,
    override val teamIdentifier: String
): Team

data class MyTeam(
    override val key: String,
    override val name: String,
    override val teamIdentifier: String
): Team

data class Location(
    val key: String,
    val name: String,
    val address: String,
)