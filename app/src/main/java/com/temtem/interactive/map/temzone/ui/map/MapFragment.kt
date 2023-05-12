package com.temtem.interactive.map.temzone.ui.map

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.search.SearchView.TransitionState
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.data.Coordinates
import com.temtem.interactive.map.temzone.data.Marker
import com.temtem.interactive.map.temzone.data.MarkerType
import com.temtem.interactive.map.temzone.databinding.MapFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBinding
import com.temtem.interactive.map.temzone.utils.extensions.MarkerView
import com.temtem.interactive.map.temzone.utils.extensions.moveToPosition
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

    private val binding: MapFragmentBinding by viewBinding()
    private val toolbar by lazy { binding.toolbar }
    private val searchBar by lazy { binding.searchBar }
    private val searchView by lazy { binding.searchView }
    private val mapLayersButton by lazy { binding.mapLayersButton }
    private val mapView by lazy { binding.mapView }
    private val bottomSheetBehavior by lazy { BottomSheetBehavior.from(binding.bottomDrawer) }

    private var canCollapseBottomDrawer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            bottomSheetBehavior.expandedOffset =
                resources.getDimension(com.google.android.material.R.dimen.m3_appbar_size_compact)
                    .toInt()

            windowInsets
        }

        // region Toolbar configuration
        toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }
        // endregion

        // region Search bar configuration
        searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_bar_menu_settings -> {
                }
            }
            true
        }
        // endregion

        // region Search view configuration
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            false
        }
        // endregion

        // region Map layers button configuration
        mapLayersButton.setOnClickListener {
            val direction = MapFragmentDirections.fromMapFragmentToMapLayersDialogFragment()

            findNavController().navigate(direction)
        }
        // endregion

        // region Map view configuration
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
                val markerView = view as MarkerView

                mapView.moveToPosition(
                    markerView.x,
                    markerView.y + (resources.displayMetrics.heightPixels / (resources.getInteger(R.integer.marker_height_scale) * SCALE)),
                    SCALE,
                    true
                )

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                canCollapseBottomDrawer = false
                Handler(Looper.getMainLooper()).postDelayed({
                    canCollapseBottomDrawer = true
                }, 500)
            }
        })

        mapView.addReferentialListener {
            if (canCollapseBottomDrawer && bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN && bottomSheetBehavior.state != BottomSheetBehavior.STATE_SETTLING) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        // endregion

        // region Bottom sheet configuration
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                searchBar.isClickable =
                    !(newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING)

                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    mapLayersButton.isClickable = false
                    mapLayersButton.hide()
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mapLayersButton.isClickable = true
                    mapLayersButton.show()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        // endregion

        // region Back button configuration
        val onSearchViewStateHiddenBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                searchView.hide()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onSearchViewStateHiddenBackPressedCallback
        )

        val onBottomSheetStateHalfExpandedBackPressedCallback =
            object : OnBackPressedCallback(false) {
                override fun handleOnBackPressed() {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBottomSheetStateHalfExpandedBackPressedCallback
        )

        val onBottomSheetStateHiddenBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBottomSheetStateHiddenBackPressedCallback
        )

        searchView.addTransitionListener { _, _, newState ->
            when (newState) {
                TransitionState.SHOWN -> {
                    onSearchViewStateHiddenBackPressedCallback.isEnabled = true
                    onBottomSheetStateHiddenBackPressedCallback.isEnabled = false
                }

                else -> {
                    onSearchViewStateHiddenBackPressedCallback.isEnabled = false
                    onBottomSheetStateHiddenBackPressedCallback.isEnabled = true
                }
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        onBottomSheetStateHalfExpandedBackPressedCallback.isEnabled = true
                        onBottomSheetStateHiddenBackPressedCallback.isEnabled = false
                    }

                    BottomSheetBehavior.STATE_HALF_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                        onBottomSheetStateHalfExpandedBackPressedCallback.isEnabled = false
                        onBottomSheetStateHiddenBackPressedCallback.isEnabled = true
                    }

                    else -> {
                        onBottomSheetStateHalfExpandedBackPressedCallback.isEnabled = false
                        onBottomSheetStateHiddenBackPressedCallback.isEnabled = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        // endregion

        // region Markers configuration
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
        // endregion
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState == null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            bottomSheetBehavior.state = savedInstanceState.getInt("bottomSheetState")

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                mapLayersButton.isClickable = false
                mapLayersButton.hide()
            } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                mapLayersButton.isClickable = true
                mapLayersButton.show()
            }

            canCollapseBottomDrawer = false
            Handler(Looper.getMainLooper()).postDelayed({
                canCollapseBottomDrawer = true
            }, 100)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("bottomSheetState", bottomSheetBehavior.state)
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
