package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.WeightDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Weight

fun WeightDto.toWeight(): Weight {
    return Weight(
        kg = kg,
        lbs = lbs,
    )
}
