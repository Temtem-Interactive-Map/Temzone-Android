package com.temtem.interactive.map.temzone

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import com.temtem.interactive.map.temzone.databinding.ActivityMainBinding
import com.temtem.interactive.map.temzone.extensions.moveToPosition
import com.temtem.interactive.map.temzone.markers.Marker
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random
import ovh.plrapps.mapview.MapView
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener

class MainActivity : AppCompatActivity() {
    private companion object {
        private const val scale = 4f
        private const val zoom = 6
        private const val tileSize = 256
        private val mapSize = tileSize * 2.0.pow(zoom).toInt()
        private const val mapMinHorizontal = tileSize * 7.0
        private val mapMaxHorizontal = mapSize - tileSize * 7.0
        private const val mapMinVertical = tileSize * 10.0
        private val mapMaxVertical = mapSize - tileSize * 11.0
        private val mapCenter = mapSize / 2.0
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
        configureMarkers(binding.mapView)
        addMarkers(binding.mapView)
    }

    private fun configureMap(mapView: MapView) {
        val tiles = TileStreamProvider { row, col, zoom ->
            baseContext.assets.open("tiles/$zoom/$col/$row.png")
        }
        val config = MapViewConfiguration(
            zoom + 1, mapSize, mapSize, tileSize, tiles
        ).apply {
            highFidelityColors()
            setMaxScale(scale)
            setMinimumScaleMode(MinimumScaleMode.FILL)
        }
        mapView.apply {
            configure(config)
            defineBounds(0.0, 0.0, mapSize.toDouble(), mapSize.toDouble())
            constrainScroll(mapMinHorizontal, mapMinVertical, mapMaxHorizontal, mapMaxVertical)

            val logicalWidth = mapMaxHorizontal - mapMinHorizontal
            val logicalHeight = mapMaxVertical - mapMinVertical
            val scale = max(
                baseContext.resources.displayMetrics.widthPixels / logicalWidth,
                baseContext.resources.displayMetrics.heightPixels / logicalHeight
            )
            moveToPosition(mapCenter, mapCenter, scale.toFloat(), false)
        }
    }

    private fun configureMarkers(mapView: MapView) {
        mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                if (view is Marker) {
                    mapView.moveToPosition(
                        view.x,
                        view.y + baseContext.resources.displayMetrics.heightPixels / (4 * scale),
                        scale,
                        true
                    )
                }
            }
        })
    }

    private fun addMarkers(mapView: MapView) {
        for (i in 0..100) {
            val marker = Marker(
                baseContext,
                Random.nextDouble(mapMinHorizontal + tileSize, mapMaxHorizontal - tileSize),
                Random.nextDouble(mapMinVertical + tileSize * 2, mapMaxVertical - tileSize)
            ).apply {
                setImageResource(R.drawable.marker)
            }

            mapView.addMarker(marker, marker.x, marker.y, -0.5f, -1f, 0f, 0f, i.toString())
        }
    }
}
