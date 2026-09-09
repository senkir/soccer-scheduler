package org.enyeinteractive.soccerconvert.network.model

import kotlinx.serialization.Serializable

@Serializable
data class WebSchedule(
    val isHome: Boolean,
    val time: String,
    val date: String,
    val opponentKey: String,
    val locationKey: String
)
