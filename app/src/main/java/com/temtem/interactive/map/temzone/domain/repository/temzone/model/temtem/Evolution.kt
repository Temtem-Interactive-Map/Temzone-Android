package com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem

data class Evolution(
    val name: String,
    val traits: List<Trait>,
    val condition: String,
    val imageUrl: String,
)
