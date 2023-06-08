package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.GenderDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Gender

fun GenderDto.toGender(): Gender {
    return Gender(
        male = male,
        female = female,
    )
}
