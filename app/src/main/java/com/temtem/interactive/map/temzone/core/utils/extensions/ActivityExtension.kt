package com.temtem.interactive.map.temzone.core.utils.extensions

import android.app.Activity
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Sets the status bar to light or dark mode.
 *
 * @param lightStatusBars True if the status bar should be light, false if it should be dark.
 */
fun Activity.setLightStatusBar(lightStatusBars: Boolean) {
    WindowInsetsControllerCompat(
        window, window.decorView
    ).apply {
        isAppearanceLightStatusBars = lightStatusBars
    }
}
