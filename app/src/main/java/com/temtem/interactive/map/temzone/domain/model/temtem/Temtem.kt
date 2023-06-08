package com.temtem.interactive.map.temzone.domain.model.temtem

data class Temtem(
    val id: Int,
    val name: String,
    val description: String,
    val types: List<Type>,
    val traits: List<Trait>,
    val gender: Gender?,
    val stats: Stats,
    val tvs: Tvs,
    val catchRate: Int,
    val height: Height,
    val weight: Weight,
    val evolutions: List<Evolution>,
    val imageUrl: String,
)
