package com.temtem.interactive.map.temzone.repositories.temzone.data

import com.google.gson.annotations.SerializedName

data class Page<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("next") val next: Int?,
    @SerializedName("prev") val prev: Int?,
)
