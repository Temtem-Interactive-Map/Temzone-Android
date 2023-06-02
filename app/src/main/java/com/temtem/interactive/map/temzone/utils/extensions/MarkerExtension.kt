package com.temtem.interactive.map.temzone.utils.extensions

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import com.temtem.interactive.map.temzone.repositories.temzone.data.Marker
import com.temtem.interactive.map.temzone.repositories.temzone.data.MarkerType
import java.io.IOException

fun Marker.getDrawable(context: Context): Drawable {
    return when (type) {
        MarkerType.SPAWN -> try {
            Drawable.createFromResourceStream(
                null,
                TypedValue(),
                context.resources.assets.open(
                    "markers/${
                        title.replace(Regex("[()]"), "").replace(" ", "_").lowercase()
                    }_icon.png"
                ),
                null,
            )
        } catch (e: IOException) {
            Drawable.createFromResourceStream(
                null,
                TypedValue(),
                context.resources.assets.open("markers/temcard_icon.png"),
                null,
            )
        }

        MarkerType.SAIPARK -> Drawable.createFromResourceStream(
            null,
            TypedValue(),
            context.resources.assets.open("markers/landmark_icon.png"),
            null,
        )
    }!!
}
