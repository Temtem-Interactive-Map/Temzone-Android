package com.temtem.interactive.map.temzone.domain.mapper.temtem

import com.temtem.interactive.map.temzone.data.remote.dto.temtem.HeightDto
import com.temtem.interactive.map.temzone.domain.model.temtem.Height

fun HeightDto.toHeight(): Height {
    return Height(
        cm = cm,
        inches = inches,
    )
}
