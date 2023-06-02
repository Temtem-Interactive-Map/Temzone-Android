package com.temtem.interactive.map.temzone.repositories.temzone.data

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("x") val x: Int,
    @SerializedName("y") val y: Int,
)
