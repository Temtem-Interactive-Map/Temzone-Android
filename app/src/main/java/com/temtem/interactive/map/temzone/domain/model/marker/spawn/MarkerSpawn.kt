package com.temtem.interactive.map.temzone.domain.model.marker.spawn

import com.temtem.interactive.map.temzone.domain.model.temtem.Temtem

data class MarkerSpawn(
    val id: String,
    val rate: List<Int>,
    val level: Level,
    val condition: String?,
    val temtem: Temtem,
    val imageUrl: String,
)
