package com.temtem.interactive.map.temzone.data

data class Marker(
    val id: String,
    val type: MarkerType,
    val title: String,
    val subtitle: String,
    val coordinates: Coordinates,
    val obtained: Boolean
)
