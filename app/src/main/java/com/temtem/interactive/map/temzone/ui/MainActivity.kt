package com.temtem.interactive.map.temzone.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.ActivityMainBinding
import com.temtem.interactive.map.temzone.extensions.MarkerView
import com.temtem.interactive.map.temzone.extensions.addMarker
import com.temtem.interactive.map.temzone.extensions.moveToPosition
import com.temtem.interactive.map.temzone.model.Marker
import com.temtem.interactive.map.temzone.model.MarkerType
import ovh.plrapps.mapview.MapView
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener
import java.io.IOException
import kotlin.math.max
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private companion object {
        private const val SCALE = 4f
        private const val ZOOM = 6
        private const val TILE_SIZE = 256
        private val MAP_SIZE = TILE_SIZE * 2.0.pow(ZOOM).toInt()
        private val MAP_CENTER = MAP_SIZE / 2.0
        private const val MAP_MIN_HORIZONTAL = TILE_SIZE * 7.0
        private val MAP_MAX_HORIZONTAL = MAP_SIZE - TILE_SIZE * 7.0
        private const val MAP_MIN_VERTICAL = TILE_SIZE * 11.0
        private val MAP_MAX_VERTICAL = MAP_SIZE - TILE_SIZE * 9.0
    }

    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_main)

        configureMap(binding.mapView)

        addMarkers(binding.mapView)
    }

    private fun configureMap(mapView: MapView) {
        val tiles = TileStreamProvider { row, col, zoom ->
            try {
                println("LOAD: tiles/$zoom/$col/$row.png")

                baseContext.assets.open("tiles/$zoom/$col/$row.png")
            } catch (e: IOException) {
                println("ERROR: tiles/$zoom/$col/$row.png")

                null
            }
        }
        val config = MapViewConfiguration(
            ZOOM + 1, MAP_SIZE, MAP_SIZE, TILE_SIZE, tiles
        ).apply {
            highFidelityColors()
            setMaxScale(SCALE)
            setMinimumScaleMode(MinimumScaleMode.FILL)
        }

        mapView.apply {
            configure(config)
            defineBounds(0.0, 0.0, MAP_SIZE.toDouble(), MAP_SIZE.toDouble())
            constrainScroll(
                MAP_MIN_HORIZONTAL, MAP_MIN_VERTICAL, MAP_MAX_HORIZONTAL, MAP_MAX_VERTICAL
            )

            val logicalWidth = MAP_MAX_HORIZONTAL - MAP_MIN_HORIZONTAL
            val logicalHeight = MAP_MAX_VERTICAL - MAP_MIN_VERTICAL
            val scale = max(
                baseContext.resources.displayMetrics.widthPixels / logicalWidth,
                baseContext.resources.displayMetrics.heightPixels / logicalHeight
            )
            moveToPosition(MAP_CENTER, MAP_CENTER, scale.toFloat(), false)
        }

        mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                if (view is MarkerView) {
                    mapView.moveToPosition(
                        view.x,
                        view.y + baseContext.resources.displayMetrics.heightPixels / (4 * SCALE),
                        SCALE,
                        true
                    )
                }
            }
        })
    }

    private fun addMarkers(mapView: MapView) {
        val marker = Marker(
            "id", MarkerType.SAIPARK, "title", "subtitle", 11039.0, 6692.0
        )
        val drawable = when (marker.type) {
            MarkerType.SPAWN -> try {
                Drawable.createFromResourceStream(
                    null,
                    TypedValue(),
                    baseContext.assets.open(
                        "markers/${
                            marker.title.replace("/[()]/g", "").replace(" ", "_").lowercase()
                        }_icon.png"
                    ),
                    null
                )
            } catch (e: IOException) {
                Drawable.createFromResourceStream(
                    null, TypedValue(), baseContext.assets.open("markers/temcard_icon.png"), null
                )
            }
            MarkerType.SAIPARK -> Drawable.createFromResourceStream(
                null, TypedValue(), baseContext.assets.open("markers/landmark_icon.png"), null
            )
        }!!

        mapView.addMarker(
            marker.id, drawable, marker.x, marker.y
        )
    }
}
