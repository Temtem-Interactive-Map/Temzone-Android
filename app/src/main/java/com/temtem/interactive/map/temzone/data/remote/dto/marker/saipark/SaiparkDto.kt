package com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area.AreaDto

data class SaiparkDto(
    val id: String,
    val areas: List<AreaDto>,
)
