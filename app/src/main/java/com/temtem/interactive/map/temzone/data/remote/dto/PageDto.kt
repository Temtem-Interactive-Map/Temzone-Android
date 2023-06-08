package com.temtem.interactive.map.temzone.data.remote.dto

data class PageDto<T>(
    val items: List<T>,
    val next: Int?,
    val prev: Int?,
)
