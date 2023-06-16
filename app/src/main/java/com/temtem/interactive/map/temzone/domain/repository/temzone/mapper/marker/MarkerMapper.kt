package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker

import com.temtem.interactive.map.temzone.data.remote.dto.marker.MarkerDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker

fun MarkerDto.toMarker(): Marker {
    return Marker(
        id = id,
        type = when (type) {
            MarkerDto.Type.SPAWN -> Marker.Type.Spawn
            MarkerDto.Type.SAIPARK -> Marker.Type.Saipark
        },
        title = title,
        subtitle = subtitle,
        x = coordinates.x,
        y = coordinates.y,
        obtained = obtained,
    )
}
