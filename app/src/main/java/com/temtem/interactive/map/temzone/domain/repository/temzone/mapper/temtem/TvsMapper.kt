package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TvsDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Tvs

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
