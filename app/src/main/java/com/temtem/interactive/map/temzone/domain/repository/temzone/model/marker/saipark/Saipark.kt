package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.area.Area

data class Saipark(
    val id: String,
    val areas: List<Area>,
)
