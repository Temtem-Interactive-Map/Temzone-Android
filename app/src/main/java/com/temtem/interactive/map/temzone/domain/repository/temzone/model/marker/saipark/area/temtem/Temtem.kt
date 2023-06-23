package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.area.temtem

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Type

data class Temtem(
    val id: Int,
    val name: String,
    val types: List<Type>,
    val imageUrl: String,
)
