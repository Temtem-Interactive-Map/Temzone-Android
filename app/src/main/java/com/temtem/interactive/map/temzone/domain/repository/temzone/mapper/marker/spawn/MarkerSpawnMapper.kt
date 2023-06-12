package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.MarkerSpawnDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem.toTemtem
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.MarkerSpawn

fun MarkerSpawnDto.toMarkerSpawn(): MarkerSpawn {
    return MarkerSpawn(
        id = id,
        rate = rate,
        level = level.toLevel(),
        condition = condition,
        temtem = temtem.toTemtem(),
        imageUrl = image.url,
    )
}
