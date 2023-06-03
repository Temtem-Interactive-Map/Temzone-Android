package com.temtem.interactive.map.temzone.utils.extensions

import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.hideAndDisable() {
    isClickable = false
    hide()
}

fun FloatingActionButton.showAndEnable() {
    isClickable = true
    show()
}
