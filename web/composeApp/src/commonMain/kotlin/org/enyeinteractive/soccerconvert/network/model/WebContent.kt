package org.enyeinteractive.soccerconvert.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WebContent(
    val teamName: String,
    val clubSeason: String,
    val date: String,
    val coach: String,
    val schedule: List<WebSchedule>,
    val locations: List<WebLocation>,
    val opponents: List<WebOpponent>
)

@Serializable
data class WebLocation(
    val name: String,
    val address: String
)

@Serializable
data class WebOpponent(
    val name: String,
    val coach: String,
    val team: String
)
