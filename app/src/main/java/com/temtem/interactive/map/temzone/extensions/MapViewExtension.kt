package com.temtem.interactive.map.temzone.extensions

import ovh.plrapps.mapview.MapView

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
