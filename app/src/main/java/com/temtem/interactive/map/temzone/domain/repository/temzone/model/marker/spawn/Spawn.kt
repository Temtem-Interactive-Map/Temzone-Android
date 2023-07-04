package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Temtem

data class Spawn(
    val id: String,
    val rate: List<Int>,
    val level: Level,
    val condition: String?,
    val temtem: Temtem,
    val imageUrl: String,
)
