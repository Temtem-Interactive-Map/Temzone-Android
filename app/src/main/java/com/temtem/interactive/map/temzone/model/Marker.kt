package com.temtem.interactive.map.temzone.model

data class Marker(
    val id: String,
    val type: MarkerType,
    val title: String,
    val subtitle: String,
    val x: Double,
    val y: Double,
)
