package com.temtem.interactive.map.temzone.domain.model

data class Page<T>(
    val items: List<T>,
    val next: Int?,
    val prev: Int?,
)
