package com.temtem.interactive.map.temzone.data.remote.dto.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.ImageDto

data class TemtemDto(
    val id: Int,
    val name: String,
    val description: String,
    val types: List<TypeDto>,
    val traits: List<TraitDto>,
    val gender: GenderDto?,
    val stats: StatsDto,
    val tvs: TvsDto,
    val catchRate: Int,
    val height: HeightDto,
    val weight: WeightDto,
    val evolutions: List<EvolutionDto>,
    val image: ImageDto,
)
