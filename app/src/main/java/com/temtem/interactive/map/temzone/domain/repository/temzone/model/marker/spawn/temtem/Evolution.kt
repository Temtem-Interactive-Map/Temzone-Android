package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem

data class Evolution(
    val name: String,
    val traits: List<String>,
    val condition: String,
    val imageUrl: String,
)
