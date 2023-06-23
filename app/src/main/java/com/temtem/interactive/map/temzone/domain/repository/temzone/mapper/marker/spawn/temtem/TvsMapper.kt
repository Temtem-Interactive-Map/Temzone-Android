package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.TvsDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Tvs

fun TvsDto.toTvs(): Tvs {
    return Tvs(
        hp = hp,
        sta = sta,
        spd = spd,
        atk = atk,
        def = def,
        spatk = spatk,
        spdef = spdef,
    )
}
