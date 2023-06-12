package com.temtem.interactive.map.temzone.domain.repository.temzone.mapper

import com.temtem.interactive.map.temzone.data.remote.dto.PageDto
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.Page

fun <T, R> PageDto<T>.toPage(items: List<R>): Page<R> {
    return Page(
        items = items,
        next = next,
        prev = prev,
    )
}
