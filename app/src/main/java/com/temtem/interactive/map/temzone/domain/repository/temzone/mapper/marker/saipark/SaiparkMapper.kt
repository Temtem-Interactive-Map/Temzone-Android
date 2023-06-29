package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.saipark

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.SaiparkDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.saipark.area.toArea
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.Saipark

fun SaiparkDto.toMarkerSaipark(): Saipark {
    return Saipark(
        id = id,
        areas = areas.map { it.toArea() },
    )
}
