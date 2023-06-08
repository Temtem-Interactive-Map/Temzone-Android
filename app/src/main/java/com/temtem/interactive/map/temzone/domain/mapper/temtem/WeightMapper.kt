package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.WeightDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Weight

fun WeightDto.toWeight(): Weight {
    return Weight(
        kg = kg,
        lbs = lbs,
    )
}
