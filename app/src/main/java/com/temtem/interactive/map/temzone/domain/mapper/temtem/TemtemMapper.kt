package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.TemtemDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Temtem

fun TemtemDto.toTemtem(): Temtem {
    return Temtem(
        id = id,
        name = name,
        description = description,
        types = types.map { it.toType() },
        traits = traits.map { it.toTrait() },
        gender = gender?.toGender(),
        stats = stats.toStats(),
        tvs = tvs.toTvs(),
        catchRate = catchRate,
        height = height.toHeight(),
        weight = weight.toWeight(),
        evolutions = evolutions.map { it.toEvolution() },
        imageUrl = image.url,
    )
}
