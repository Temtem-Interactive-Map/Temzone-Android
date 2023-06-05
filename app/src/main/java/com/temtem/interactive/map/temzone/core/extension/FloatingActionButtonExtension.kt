package com.temtem.interactive.map.temzone.core.extension

import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.hideAndDisable() {
    isClickable = false
    hide()
}

fun FloatingActionButton.showAndEnable() {
    isClickable = true
    show()
}
