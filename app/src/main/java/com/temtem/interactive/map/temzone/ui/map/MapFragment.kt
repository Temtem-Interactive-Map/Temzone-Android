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
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.MarkerView
import com.temtem.interactive.map.temzone.utils.extensions.moveToPosition
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar
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
        private const val MARKER_X = "marker_x"
        private const val MARKER_Y = "marker_y"
        private const val NULL_MARKER_COORDINATE = -1.0
        private const val BOTTOM_SHEET_STATE = "bottom_sheet_state"
    }

    private val viewModel: MapViewModel by viewModels()
    private val viewBinding: MapFragmentBinding by viewBindings()

    private var markerX = NULL_MARKER_COORDINATE
    private var markerY = NULL_MARKER_COORDINATE
    private var canCollapseBottomDrawer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

        // region Configure window insets

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { _, windowInsets ->
            bottomSheetBehavior.expandedOffset =
                resources.getDimension(com.google.android.material.R.dimen.m3_appbar_size_compact)
                    .toInt()

            windowInsets
        }

        // endregion

        // region Configure toolbar

        viewBinding.toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Resets bottom drawer scroll position
                viewBinding.bottomDrawer.scrollTo(0, 0)
            }
        }

        // endregion

        // region Configure search bar

        viewBinding.searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_bar_menu_settings -> {
                    val direction = MapFragmentDirections.fromMapFragmentToSettingsFragment()

                    findNavController().navigate(direction)
                }
            }
            true
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewBinding.searchBar.isClickable =
                    !(newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // endregion

        // region Configure search view

        viewBinding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            false
        }

        viewBinding.searchView.addTransitionListener { _, _, newState ->
            when (newState) {
                TransitionState.SHOWN, TransitionState.SHOWING -> {
                    setLightStatusBar(true)
                }

                else -> {
                    setLightStatusBar(false)
                }
            }
        }

        // endregion

        // region Configure map layers button

        viewBinding.mapLayersButton.setOnClickListener {
            val direction = MapFragmentDirections.fromMapFragmentToMapLayersDialogFragment()

            findNavController().navigate(direction)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        viewBinding.mapLayersButton.isClickable = false
                        viewBinding.mapLayersButton.hide()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                        viewBinding.mapLayersButton.isClickable = true
                        viewBinding.mapLayersButton.show()
                    }

                    else -> {
                        viewBinding.mapLayersButton.isClickable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // endregion

        // region Configure map view

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

        viewBinding.mapView.apply {
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

        viewBinding.mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                val markerView = view as MarkerView

                // Change the search bar menu to a back menu
                showSearchBarBackMenu()

                // Hide the map layers button
                viewBinding.mapLayersButton.isClickable = false
                viewBinding.mapLayersButton.hide()

                // Set the marker coordinates
                markerX = markerView.x
                markerY = markerView.y

                // Show the bottom sheet
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Center the marker on the screen
                viewBinding.mapView.moveToPosition(
                    markerView.x,
                    markerView.y + (resources.displayMetrics.heightPixels / (resources.getInteger(
                        R.integer.marker_height_scale
                    ) * SCALE)),
                    SCALE,
                    true
                )

                delayCollapseBottomDrawer()
            }
        })

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    viewBinding.mapView.moveToPosition(
                        markerX,
                        markerY + (resources.displayMetrics.heightPixels / (resources.getInteger(
                            R.integer.marker_height_scale
                        ) * SCALE)),
                        SCALE,
                        false
                    )

                    delayCollapseBottomDrawer()
                } else if (canCollapseBottomDrawer && newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    viewBinding.mapView.moveToPosition(
                        markerX,
                        markerY + (resources.displayMetrics.heightPixels / (resources.getInteger(
                            R.integer.marker_height_scale
                        ) * SCALE)),
                        SCALE,
                        true
                    )

                    delayCollapseBottomDrawer()
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    // Reset the search bar menu
                    viewBinding.searchBar.menu.clear()
                    viewBinding.searchBar.inflateMenu(R.menu.search_bar_menu)
                    viewBinding.searchBar.setNavigationIcon(R.drawable.search_icon)
                    viewBinding.searchBar.setNavigationOnClickListener(null)

                    // Reset the marker coordinates
                    markerX = NULL_MARKER_COORDINATE
                    markerY = NULL_MARKER_COORDINATE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // Collapse the bottom sheet when the map is tapped or panned
        viewBinding.mapView.addReferentialListener {
            if (canCollapseBottomDrawer && bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // endregion

        // region Configure back button

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (viewBinding.searchView.currentTransitionState) {
                    TransitionState.SHOWN, TransitionState.SHOWING -> {
                        viewBinding.searchView.hide()
                    }

                    else -> when (bottomSheetBehavior.state) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }

                        else -> {
                            requireActivity().finish()
                        }
                    }
                }
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                onBackPressedCallback.isEnabled = true
            }

            override fun onPause(owner: LifecycleOwner) {
                onBackPressedCallback.isEnabled = false
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBackPressedCallback
        )

        // endregion

        // region Add markers to map
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

    override fun onResume() {
        super.onResume()

        setLightStatusBar(false)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

        if (savedInstanceState == null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            // Restore the marker coordinates
            markerX = savedInstanceState.getDouble(MARKER_X)
            markerY = savedInstanceState.getDouble(MARKER_Y)

            if (markerX != NULL_MARKER_COORDINATE && markerY != NULL_MARKER_COORDINATE) {
                // Change the search bar menu to a back menu
                showSearchBarBackMenu()

                // Center the marker on the screen
                viewBinding.mapView.moveToPosition(
                    markerX,
                    markerY + (resources.displayMetrics.heightPixels / (resources.getInteger(R.integer.marker_height_scale) * SCALE)),
                    SCALE,
                    false
                )
            }

            // Restore the bottom sheet state
            bottomSheetBehavior.state = savedInstanceState.getInt(BOTTOM_SHEET_STATE)

            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                viewBinding.mapLayersButton.isClickable = false
                viewBinding.mapLayersButton.hide()
            } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                viewBinding.mapLayersButton.isClickable = true
                viewBinding.mapLayersButton.show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

        outState.putDouble(MARKER_X, markerX)
        outState.putDouble(MARKER_Y, markerY)
        outState.putInt(BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
    }

    private fun showSearchBarBackMenu() {
        viewBinding.searchBar.menu.clear()
        viewBinding.searchBar.inflateMenu(R.menu.search_bar_back_menu)
        viewBinding.searchBar.setNavigationIcon(R.drawable.arrow_back_icon)
        viewBinding.searchBar.setNavigationOnClickListener {
            hideSearchBarBackMenu()

            // Reset the marker coordinates
            markerX = NULL_MARKER_COORDINATE
            markerY = NULL_MARKER_COORDINATE

            // Hide the bottom sheet
            val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun hideSearchBarBackMenu() {
        viewBinding.searchBar.menu.clear()
        viewBinding.searchBar.inflateMenu(R.menu.search_bar_menu)
        viewBinding.searchBar.setNavigationIcon(R.drawable.search_icon)
        viewBinding.searchBar.setNavigationOnClickListener(null)
    }

    private fun delayCollapseBottomDrawer() {
        // Delay the re-enabling of the capability to collapse the bottom sheet in order to prevent
        // it from being immediately collapsed after moving the map
        canCollapseBottomDrawer = false
        Handler(Looper.getMainLooper()).postDelayed({
            canCollapseBottomDrawer = true
        }, 500)
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

        viewBinding.mapView.addMarker(
            markerView, markerView.x, markerView.y, -0.5f, -0.5f, 0f, 0f, markerView.id
        )
    }
}
