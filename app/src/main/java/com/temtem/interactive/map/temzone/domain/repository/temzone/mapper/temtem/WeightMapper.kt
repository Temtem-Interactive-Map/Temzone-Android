package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.WeightDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.temtem.Weight

fun WeightDto.toWeight(): Weight {
    return Weight(
        kg = kg,
        lbs = lbs,
    )
}
