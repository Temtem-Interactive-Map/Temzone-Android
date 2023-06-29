package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.StatsDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Stats

fun StatsDto.toStats(): Stats {
    return Stats(
        hp = hp,
        sta = sta,
        spd = spd,
        atk = atk,
        def = def,
        spatk = spatk,
        spdef = spdef,
        total = total,
    )
}
