package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper.marker.spawn.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.marker.spawn.temtem.GenderDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.temtem.Gender

fun GenderDto.toGender(): Gender {
    return Gender(
        male = male,
        female = female,
    )
}
