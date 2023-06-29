package com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area

import com.temtem.interactive.map.temzone.data.remote.dto.marker.saipark.area.temtem.TemtemDto

data class AreaDto(
    val name: String,
    val rate: Int,
    val lumaRate: Int,
    val minSvs: Int,
    val eggMoves: Int,
    val temtem: TemtemDto,
)
