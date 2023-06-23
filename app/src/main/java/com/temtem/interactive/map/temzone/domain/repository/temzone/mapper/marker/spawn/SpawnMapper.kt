package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.SpawnDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem.toTemtem
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn

fun SpawnDto.toMarkerSpawn(): Spawn {
    return Spawn(
        id = id,
        rate = rate,
        level = level.toLevel(),
        condition = condition,
        temtem = temtem.toTemtem(),
        imageUrl = image.url,
    )
}
