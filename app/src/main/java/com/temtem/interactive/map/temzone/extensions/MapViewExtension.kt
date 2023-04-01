package com.temtem.interactive.map.temzone.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import ovh.plrapps.mapview.MapView
import ovh.plrapps.mapview.api.addMarker

@SuppressLint("ViewConstructor")
class MarkerView(
    context: Context,
    val id: String,
    val x: Double,
    val y: Double
) : AppCompatImageView(context)

fun MapView.addMarker(id: String, drawable: Drawable, x: Double, y: Double) {
    val marker = MarkerView(context, id, x, y).apply {
        setImageDrawable(drawable)
    }

    addMarker(marker, marker.x, marker.y, -0.5f, -0.5f, 0f, 0f, marker.id)
}

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
