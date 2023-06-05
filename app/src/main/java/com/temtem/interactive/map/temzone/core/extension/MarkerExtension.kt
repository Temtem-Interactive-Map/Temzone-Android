package com.temtem.interactive.map.temzone.core.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import com.temtem.interactive.map.temzone.domain.model.marker.Marker
import java.io.IOException

fun Marker.getDrawable(context: Context): Drawable {
    return when (type) {
        Marker.Type.Spawn -> try {
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

        Marker.Type.Saipark -> Drawable.createFromResourceStream(
            null,
            TypedValue(),
            context.resources.assets.open("markers/landmark_icon.png"),
            null,
        )
    }!!
}
