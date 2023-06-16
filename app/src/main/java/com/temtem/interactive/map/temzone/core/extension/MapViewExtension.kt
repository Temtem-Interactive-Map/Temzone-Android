package com.temtem.interactive.map.temzone.core.extension

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import kotlinx.coroutines.delay
import ovh.plrapps.mapview.MapView
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.getMarkerByTag

@SuppressLint("ViewConstructor")
class MarkerView(
    context: Context,
    val id: String,
    val x: Double,
    val y: Double,
) : AppCompatImageView(context)

/**
 * Adds a marker to the MapView.
 *
 * @param marker The marker to add.
 */
suspend fun MapView.addMarker(marker: Marker) {
    val view = getMarkerByTag(marker.id) as MarkerView?

    if (view == null) {
        val markerView = MarkerView(
            context,
            marker.id,
            marker.x.toDouble(),
            marker.y.toDouble(),
        ).apply {
            // Set the marker's elevation to its hash code so the markers are drawn
            // always
            // in the same order
            elevation = marker.id.hashCode().toFloat()

            // Set the marker's visibility
            visibility = View.VISIBLE

            // Set the marker's drawable
            val drawable = marker.getDrawable(context).apply {
                alpha = if (marker.obtained) 153 else 255
            }

            // Set the drawable to the marker view
            setImageDrawable(drawable)
        }

        // Add the marker to the map
        addMarker(
            markerView,
            markerView.x,
            markerView.y,
            -0.5f,
            -0.5f,
            0f,
            0f,
            markerView.id,
        )
        delay(1)
    } else {
        view.visibility = View.VISIBLE

        // If the marker already exists in the map, update its opacity
        view.drawable.alpha = if (marker.obtained) 153 else 255
    }
}

/**
 * Removes a marker from the MapView.
 *
 * @param marker The marker to remove.
 */
suspend fun MapView.removeMarker(marker: Marker) {
    val view = getMarkerByTag(marker.id) as MarkerView?

    if (view == null) {
        val markerView = MarkerView(
            context,
            marker.id,
            marker.x.toDouble(),
            marker.y.toDouble(),
        ).apply {
            // Set the marker's elevation to its hash code so the
            // markers are drawn always in the same order
            elevation = marker.id.hashCode().toFloat()

            // Set the marker's visibility
            visibility = View.INVISIBLE

            // Set the marker's drawable
            val drawable = marker.getDrawable(context).apply {
                alpha = if (marker.obtained) 153 else 255
            }

            // Set the drawable to the marker view
            setImageDrawable(drawable)
        }

        // Add the marker to the map
        addMarker(
            markerView,
            markerView.x,
            markerView.y,
            -0.5f,
            -0.5f,
            0f,
            0f,
            markerView.id,
        )
        delay(1)
    } else {
        view.visibility = View.INVISIBLE
    }
}

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
