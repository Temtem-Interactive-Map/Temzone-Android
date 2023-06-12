package com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.area

import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Temtem

data class Area(
    val name: String,
    val rate: Int,
    val lumaRate: Int,
    val minSvs: Int,
    val eggMoves: Int,
    val temtem: Temtem,
)
