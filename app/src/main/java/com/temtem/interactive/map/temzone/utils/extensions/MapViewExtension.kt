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
    val y: Double,
) : AppCompatImageView(context)

/**
 * Scrolls the MapView so it's centered on the x and y coordinates.
 * Takes an additional [destinationScale] parameter.
 *
 * The scroll position and scale are animated if [shouldAnimate] is set to `true`.
 *
 * @param x The x coordinate in the MapView's coordinate system.
 * @param y The y coordinate in the MapView's coordinate system.
 * @param destinationScale The scale of the MapView when centered on the marker
 * @param shouldAnimate True if the movement should use a transition effect.
 */
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
