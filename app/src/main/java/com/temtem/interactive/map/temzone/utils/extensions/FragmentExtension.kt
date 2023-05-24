package com.temtem.interactive.map.temzone.utils.extensions

import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

/**
 * Sets the status bar to light or dark mode.
 *
 * @param lightStatusBars True if the status bar should be light, false if it should be dark.
 */
fun Fragment.setLightStatusBar(lightStatusBars: Boolean) {
    WindowInsetsControllerCompat(
        requireActivity().window, requireActivity().window.decorView
    ).apply {
        isAppearanceLightStatusBars = lightStatusBars
    }
}
