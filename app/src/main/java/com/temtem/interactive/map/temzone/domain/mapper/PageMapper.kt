package com.temtem.interactive.map.temzone.domain.mapper

import com.temtem.interactive.map.temzone.data.remote.dto.PageDto
import com.temtem.interactive.map.temzone.domain.model.Page

fun <T, R> PageDto<T>.toPage(items: List<R>): Page<R> {
    return Page(
        items = items,
        next = next,
        prev = prev,
    )
}
