package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.LevelDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Level

fun LevelDto.toLevel(): Level {
    return Level(
        min = min,
        max = max,
    )
}
