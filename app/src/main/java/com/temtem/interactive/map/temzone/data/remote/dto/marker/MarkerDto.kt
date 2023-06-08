package com.temtem.interactive.map.temzone.data.remote.dto.marker

import com.google.gson.annotations.SerializedName

data class MarkerDto(
    val id: String,
    val type: Type,
    val title: String,
    val subtitle: String,
    val coordinates: CoordinatesDto,
    val obtained: Boolean,
) {
    enum class Type(val value: String) {
        @SerializedName("spawn")
        SPAWN("spawn"),

        @SerializedName("saipark")
        SAIPARK("saipark"),
    }
}
