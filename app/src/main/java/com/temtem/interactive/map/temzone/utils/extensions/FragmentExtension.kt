package com.temtem.interactive.map.temzone.utils.extensions

import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

fun Fragment.setLightStatusBar(lightStatusBars: Boolean) {
    WindowInsetsControllerCompat(
        requireActivity().window, requireActivity().window.decorView
    ).apply {
        isAppearanceLightStatusBars = lightStatusBars
    }
}
