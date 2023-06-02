package com.temtem.interactive.map.temzone.repositories.temzone.data

import com.google.gson.annotations.SerializedName

enum class MarkerType(val value: String) {

    @SerializedName("spawn")
    SPAWN("spawn"),

    @SerializedName("saipark")
    SAIPARK("saipark")
}
