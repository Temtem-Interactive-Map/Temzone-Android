package com.temtem.interactive.map.temzone.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import ovh.plrapps.mapview.MapView

@SuppressLint("ViewConstructor")
class MarkerView(
    context: Context,
    val id: String,
    val x: Double,
    val y: Double
) : AppCompatImageView(context)

fun MapView.moveToPosition(x: Double, y: Double, destinationScale: Float, shouldAnimate: Boolean) {
    val scaledX = (x * destinationScale).toInt()
    val scaledY = (y * destinationScale).toInt()

    post {
        if (shouldAnimate) {
            slideToAndCenterWithScale(scaledX, scaledY, destinationScale)
        } else {
            scale = destinationScale
            scrollToAndCenter(scaledX, scaledY)
        }
    }
}
