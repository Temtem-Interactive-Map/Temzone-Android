package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.StatsDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Stats

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
