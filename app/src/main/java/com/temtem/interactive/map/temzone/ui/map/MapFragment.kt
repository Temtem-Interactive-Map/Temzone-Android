package com.temtem.interactive.map.temzone.ui.map

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.search.SearchView.TransitionState
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapFragmentBinding
import com.temtem.interactive.map.temzone.extensions.MarkerView
import com.temtem.interactive.map.temzone.extensions.moveToPosition
import com.temtem.interactive.map.temzone.model.Coordinates
import com.temtem.interactive.map.temzone.model.Marker
import com.temtem.interactive.map.temzone.model.MarkerType
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener
import java.io.IOException
import kotlin.math.max
import kotlin.math.pow
import kotlin.random.Random

class MapFragment : Fragment(R.layout.map_fragment) {

    private companion object {
        private const val SCALE = 4f
        private const val ZOOM = 6
        private const val TILE_SIZE = 256
        private val MAP_SIZE = TILE_SIZE * 2.0.pow(ZOOM).toInt()
        private val MAP_CENTER = MAP_SIZE / 2.0
        private const val MAP_MIN_HORIZONTAL = TILE_SIZE * 7.0
        private val MAP_MAX_HORIZONTAL = MAP_SIZE - TILE_SIZE * 7.0
        private const val MAP_MIN_VERTICAL = TILE_SIZE * 11.0
        private val MAP_MAX_VERTICAL = MAP_SIZE - TILE_SIZE * 11.0
        private const val MARKER_OPACITY = 153
    }

    private val viewModel: MapViewModel by viewModels()
    private val binding: MapFragmentBinding by viewBinding()
    private val searchBar by lazy { binding.searchBar }
    private val searchView by lazy { binding.searchView }
    private val mapView by lazy { binding.mapView }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.bottomDrawer) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureSearchBar()
        configureSearchView()
        configureMap()
        configureBottomDrawer()

        addMarker(
            Marker(
                "id", MarkerType.SAIPARK, "title", "subtitle", Coordinates(11039.0, 6692.0), false
            )
        )
        addMarker(
            Marker(
                "id", MarkerType.SPAWN, "Mimit", "subtitle", Coordinates(11039.0 / 2, 6692.0), true
            )
        )
        addMarker(
            Marker(
                "id",
                MarkerType.SPAWN,
                "Mimit",
                "subtitle",
                Coordinates(11039.0 / 2 - 100, 6692.0),
                false
            )
        )

        repeat(600) {
            addMarker(
                Marker(
                    "id", MarkerType.SPAWN, "Mimit", "subtitle", Coordinates(
                        Random.nextDouble(
                            MAP_MIN_HORIZONTAL + TILE_SIZE * 2, MAP_MAX_HORIZONTAL - TILE_SIZE * 2
                        ), Random.nextDouble(
                            MAP_MIN_VERTICAL + TILE_SIZE * 2, MAP_MAX_VERTICAL - TILE_SIZE * 2
                        )
                    ), false
                )
            )
        }
    }

    private fun configureSearchBar() {
        searchBar.inflateMenu(R.menu.search_bar_menu)
    }

    private fun configureSearchView() {
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            false
        }

        val onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                searchView.hide()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBackPressedCallback
        )

        searchView.addTransitionListener { _, _, newState ->
            onBackPressedCallback.isEnabled = newState == TransitionState.SHOWN
        }
    }

    private fun configureMap() {
        val tiles = TileStreamProvider { row, col, zoom ->
            try {
                resources.assets.open("tiles/$zoom/$col/$row.png")
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
                        view.y + (resources.displayMetrics.heightPixels / (4 * SCALE)),
                        SCALE,
                        true
                    )

                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                    viewModel.canCollapseBottomDrawer = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        viewModel.canCollapseBottomDrawer = true
                    }, 500)
                }
            }
        })

        mapView.addReferentialListener {
            if (viewModel.canCollapseBottomDrawer && bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN && bottomSheetBehavior.state != BottomSheetBehavior.STATE_SETTLING) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun configureBottomDrawer() {
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.peekHeight =
            resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        bottomSheetBehavior.halfExpandedRatio = 0.6f
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setUpdateImportantForAccessibilityOnSiblings(true)
    }

    private fun addMarker(marker: Marker) {
        val markerView = MarkerView(
            requireContext(), marker.id, marker.coordinates.x, marker.coordinates.y
        ).apply {
            val drawable = when (marker.type) {
                MarkerType.SPAWN -> try {
                    Drawable.createFromResourceStream(
                        null, TypedValue(), resources.assets.open(
                            "markers/${
                                marker.title.replace("/[()]/g", "").replace(" ", "_").lowercase()
                            }_icon.png"
                        ), null
                    )
                } catch (e: IOException) {
                    Drawable.createFromResourceStream(
                        null, TypedValue(), resources.assets.open("markers/temcard_icon.png"), null
                    )
                }

                MarkerType.SAIPARK -> Drawable.createFromResourceStream(
                    null, TypedValue(), resources.assets.open("markers/landmark_icon.png"), null
                )
            }!!

            if (marker.obtained) {
                drawable.alpha = MARKER_OPACITY
            }

            setImageDrawable(drawable)
        }

        mapView.addMarker(
            markerView, markerView.x, markerView.y, -0.5f, -0.5f, 0f, 0f, markerView.id
        )
    }
}
