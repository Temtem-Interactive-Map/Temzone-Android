package com.temtem.interactive.map.temzone.domain.mapper.marker.saipark.area

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area.AreaDto
import com.temtem.interactive.map.temzone.domain.mapper.temtem.toTemtem
import com.temtem.interactive.map.temzone.domain.model.marker.saipark.area.Area

fun AreaDto.toArea(): Area {
    return Area(
        name = name,
        rate = rate,
        lumaRate = lumaRate,
        minSvs = minSvs,
        eggMoves = eggMoves,
        temtem = temtem.toTemtem(),
    )
}
