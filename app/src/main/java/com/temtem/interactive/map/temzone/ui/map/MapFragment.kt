package com.temtem.interactive.map.temzone.ui.map

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.search.SearchView.TransitionState
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapFragmentBinding
import com.temtem.interactive.map.temzone.extensions.MarkerView
import com.temtem.interactive.map.temzone.extensions.addMarker
import com.temtem.interactive.map.temzone.extensions.moveToPosition
import com.temtem.interactive.map.temzone.model.Marker
import com.temtem.interactive.map.temzone.model.MarkerType
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener
import java.io.IOException
import kotlin.math.max
import kotlin.math.pow


class MapFragment : Fragment(R.layout.map_fragment) {

    private companion object {
        private const val SCALE = 4f
        private const val ZOOM = 6
        private const val TILE_SIZE = 256
        private val MAP_SIZE = TILE_SIZE * 2.0.pow(ZOOM).toInt()
        private val MAP_CENTER = MAP_SIZE / 2.0
        private const val MAP_MIN_HORIZONTAL = TILE_SIZE * 7.0
        private val MAP_MAX_HORIZONTAL = MAP_SIZE - TILE_SIZE * 7.0
        private const val MAP_MIN_VERTICAL = TILE_SIZE * 7.0
        private val MAP_MAX_VERTICAL = MAP_SIZE - TILE_SIZE * 11.0
    }

    private val binding: MapFragmentBinding by viewBinding()
    private val searchBar by lazy { binding.searchBar }
    private val searchView by lazy { binding.searchView }
    private val mapView by lazy { binding.mapView }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureSearchBar()
        configureSearchView()
        configureMap()

        addMarkers()
    }

    private fun configureSearchBar() {
        searchBar.inflateMenu(R.menu.search_bar_menu)
    }

    private fun configureSearchView() {
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            false
        }

        val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(false) {
                override fun handleOnBackPressed() {
                    searchView.hide()
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback
        )

        searchView.addTransitionListener { _, _, newState ->
            onBackPressedCallback.isEnabled = newState == TransitionState.SHOWN
        }
    }

    private fun configureMap() {
        val tiles = TileStreamProvider { row, col, zoom ->
            try {
                requireContext().assets.open("tiles/$zoom/$col/$row.png")
            } catch (e: IOException) {
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
                requireContext().resources.displayMetrics.widthPixels / logicalWidth,
                requireContext().resources.displayMetrics.heightPixels / logicalHeight
            )
            moveToPosition(MAP_CENTER, MAP_CENTER, scale.toFloat(), false)
        }

        mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                if (view is MarkerView) {
                    mapView.moveToPosition(
                        view.x,
                        view.y + requireContext().resources.displayMetrics.heightPixels / (4 * SCALE),
                        SCALE,
                        true
                    )
                }
            }
        })
    }

    private fun addMarkers() {
        val marker = Marker(
            "id", MarkerType.SAIPARK, "title", "subtitle", 11039.0, 6692.0
        )

        val drawable = when (marker.type) {
            MarkerType.SPAWN -> try {
                Drawable.createFromResourceStream(
                    null, TypedValue(), requireContext().assets.open(
                        "markers/${
                            marker.title.replace("/[()]/g", "").replace(" ", "_").lowercase()
                        }_icon.png"
                    ), null
                )
            } catch (e: IOException) {
                Drawable.createFromResourceStream(
                    null,
                    TypedValue(),
                    requireContext().assets.open("markers/temcard_icon.png"),
                    null
                )
            }

            MarkerType.SAIPARK -> Drawable.createFromResourceStream(
                null, TypedValue(), requireContext().assets.open("markers/landmark_icon.png"), null
            )
        }!!

        mapView.addMarker(
            marker.id, drawable, marker.x, marker.y
        )
    }
}
