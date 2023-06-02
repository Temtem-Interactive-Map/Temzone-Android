package com.temtem.interactive.map.temzone.repositories.temzone.data

import com.google.gson.annotations.SerializedName

data class Marker(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: MarkerType,
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("obtained") val obtained: Boolean
)
