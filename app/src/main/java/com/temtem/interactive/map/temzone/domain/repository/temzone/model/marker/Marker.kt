package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker

data class Marker(
    val id: String,
    val type: Type,
    val title: String,
    val subtitle: String,
    val x: Int,
    val y: Int,
    val obtained: Boolean,
) {
    sealed interface Type {
        object Spawn : Type
        object Saipark : Type
    }
}
