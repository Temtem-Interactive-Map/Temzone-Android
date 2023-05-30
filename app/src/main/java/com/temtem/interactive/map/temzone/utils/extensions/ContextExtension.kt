package com.temtem.interactive.map.temzone.utils.extensions

import android.content.Context

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}
