package com.temtem.interactive.map.temzone.domain.mapper.marker.saipark

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.MarkerSaiparkDto
import com.temtem.interactive.map.temzone.domain.mapper.marker.saipark.area.toArea
import com.temtem.interactive.map.temzone.domain.model.marker.saipark.MarkerSaipark

fun MarkerSaiparkDto.toMarkerSaipark(): MarkerSaipark {
    return MarkerSaipark(
        id = id,
        areas = areas.map { it.toArea() },
    )
}
