package com.temtem.interactive.map.temzone.ui.map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.search.SearchView.TransitionState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapFragmentBinding
import com.temtem.interactive.map.temzone.ui.MainViewModel
import com.temtem.interactive.map.temzone.ui.map.state.MarkersUiState
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.MarkerView
import com.temtem.interactive.map.temzone.utils.extensions.getDrawable
import com.temtem.interactive.map.temzone.utils.extensions.hideAndDisable
import com.temtem.interactive.map.temzone.utils.extensions.moveToPosition
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar
import com.temtem.interactive.map.temzone.utils.extensions.showAndEnable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.getMarkerByTag
import ovh.plrapps.mapview.api.removeMarker
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener
import java.io.IOException
import kotlin.math.max
import kotlin.math.pow

@AndroidEntryPoint
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

    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewBinding: MapFragmentBinding by viewBindings()

    private var currentMarkerId: String? = null
    private var markersSnackbar: Snackbar? = null
    private var canCollapseBottomDrawer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

        // region Configure the toolbar

        viewBinding.toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Resets bottom drawer scroll position
                viewBinding.bottomDrawer.scrollTo(0, 0)
            }
        }

        // endregion

        // region Configure the search bar

        viewBinding.searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_bar_menu_settings -> {
                    findNavController().navigate(MapFragmentDirections.fromMapFragmentToSettingsFragment())
                }
            }
            true
        }

        // endregion

        // region Configure the search view

        viewBinding.searchView.editText.addTextChangedListener {
            activityViewModel.searchMarkers(it.toString())
        }

        viewBinding.searchView.addTransitionListener { _, _, newState ->
            when (newState) {
                TransitionState.SHOWN, TransitionState.SHOWING -> {
                    requireActivity().setLightStatusBar(true)
                }

                else -> {
                    requireActivity().setLightStatusBar(false)
                }
            }
        }

        // endregion

        // region Configure the map layer floating action button

        viewBinding.mapLayerFloatingActionButton.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.fromMapFragmentToMapLayersDialogFragment())
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        viewBinding.mapLayerFloatingActionButton.hideAndDisable()
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                        viewBinding.mapLayerFloatingActionButton.showAndEnable()
                    }

                    else -> {
                        viewBinding.mapLayerFloatingActionButton.isClickable = false
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // endregion

        // region Configure the map view

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
                MAP_MIN_HORIZONTAL,
                MAP_MIN_VERTICAL,
                MAP_MAX_HORIZONTAL,
                MAP_MAX_VERTICAL,
            )

            val logicalWidth = MAP_MAX_HORIZONTAL - MAP_MIN_HORIZONTAL
            val logicalHeight = MAP_MAX_VERTICAL - MAP_MIN_VERTICAL
            val scale = max(
                requireContext().resources.displayMetrics.widthPixels / logicalWidth,
                requireContext().resources.displayMetrics.heightPixels / logicalHeight,
            )

            moveToPosition(MAP_CENTER, MAP_CENTER, scale.toFloat(), false)
        }

        viewBinding.mapView.setMarkerTapListener(object : MarkerTapListener {
            override fun onMarkerTap(view: View, x: Int, y: Int) {
                val markerView = view as MarkerView

                // Save the current marker id
                currentMarkerId = markerView.id

                // Change the search bar menu to a back menu
                viewBinding.searchBar.menu.clear()
                viewBinding.searchBar.inflateMenu(R.menu.search_bar_back_menu)
                viewBinding.searchBar.setNavigationIcon(R.drawable.arrow_back_icon)
                viewBinding.searchBar.setNavigationContentDescription(R.string.arrow_back_navigation_content_description)
                viewBinding.searchBar.setNavigationOnClickListener {
                    // Reset the search bar menu
                    viewBinding.searchBar.menu.clear()
                    viewBinding.searchBar.inflateMenu(R.menu.search_bar_menu)
                    viewBinding.searchBar.setNavigationIcon(R.drawable.search_icon)
                    viewBinding.searchBar.navigationContentDescription = null
                    viewBinding.searchBar.setNavigationOnClickListener(null)

                    // Hide the bottom sheet
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }

                // Force hide the map layer floating action button before showing the bottom sheet
                viewBinding.mapLayerFloatingActionButton.hideAndDisable()

                // Show the bottom sheet
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                // Center the marker on the screen
                viewBinding.mapView.moveToPosition(
                    markerView.x,
                    markerView.y + (resources.displayMetrics.heightPixels / (4 * SCALE)),
                    SCALE,
                    true,
                )

                // Delay the re-enabling of the capability to collapse the bottom sheet in order to
                // prevent it from being immediately collapsed after moving the map
                canCollapseBottomDrawer = false
                Handler(Looper.getMainLooper()).postDelayed({
                    canCollapseBottomDrawer = true
                }, 500)
            }
        })

        // Collapse the bottom sheet when the map is tapped or panned
        viewBinding.mapView.addReferentialListener {
            if (canCollapseBottomDrawer && bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        // Update the markers in the map when the map layers are changed
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.markersUiState.collect {
                    when (it) {
                        is MarkersUiState.Empty -> {
                            activityViewModel.initializeMap()

                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }

                        is MarkersUiState.Loading -> {
                            markersSnackbar = Snackbar.make(
                                viewBinding.root,
                                R.string.loading_markers_snackbar,
                                Snackbar.LENGTH_INDEFINITE,
                            ).apply {
                                show()
                            }
                        }

                        is MarkersUiState.Success -> {
                            markersSnackbar?.dismiss()

                            // If the current marker is no longer in the list of markers to show,
                            // reset the search bar menu and hide the bottom sheet
                            if (it.newMarkers.find { marker -> marker.id == currentMarkerId } == null) {
                                // Reset the search bar menu
                                viewBinding.searchBar.menu.clear()
                                viewBinding.searchBar.inflateMenu(R.menu.search_bar_menu)
                                viewBinding.searchBar.setNavigationIcon(R.drawable.search_icon)
                                viewBinding.searchBar.navigationContentDescription = null
                                viewBinding.searchBar.setNavigationOnClickListener(null)

                                // Hide the bottom sheet
                                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            }

                            // Add new markers to the map
                            it.newMarkers.forEach { marker ->
                                val markerView =
                                    viewBinding.mapView.getMarkerByTag(marker.id) as MarkerView?

                                if (markerView == null) {
                                    MarkerView(
                                        requireContext(),
                                        marker.id,
                                        marker.coordinates.x.toDouble(),
                                        marker.coordinates.y.toDouble(),
                                    ).apply {
                                        // Set the marker's elevation to its hash code so the
                                        // markers are drawn always in the same order
                                        elevation = marker.id.hashCode().toFloat()

                                        // Set the marker's drawable
                                        marker.getDrawable(requireContext()).apply {
                                            alpha = if (marker.obtained) MARKER_OPACITY else 255

                                            // Set the drawable to the marker view
                                            setImageDrawable(this)
                                        }

                                        // Add the marker to the map
                                        viewBinding.mapView.addMarker(
                                            this,
                                            x,
                                            y,
                                            -0.5f,
                                            -0.5f,
                                            0f,
                                            0f,
                                            marker.id,
                                        )
                                    }
                                } else {
                                    // If the marker already exists in the map, update its opacity
                                    markerView.drawable.alpha =
                                        if (marker.obtained) MARKER_OPACITY else 255
                                }
                            }

                            // Remove old markers from the map
                            it.oldMarkers.forEach { marker ->
                                viewBinding.mapView.getMarkerByTag(marker.id)?.let { markerView ->
                                    viewBinding.mapView.removeMarker(markerView)
                                }
                            }
                        }

                        is MarkersUiState.Error -> {
                            markersSnackbar?.dismiss()

                            Snackbar.make(
                                viewBinding.root,
                                it.message,
                                Snackbar.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }
        }

        // endregion

        // region Configure the bottom sheet

        bottomSheetBehavior.expandedOffset =
            resources.getDimension(com.google.android.material.R.dimen.m3_appbar_size_compact)
                .toInt()

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Reset the search bar menu when the bottom sheet is hidden
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewBinding.searchBar.menu.clear()
                    viewBinding.searchBar.inflateMenu(R.menu.search_bar_menu)
                    viewBinding.searchBar.setNavigationIcon(R.drawable.search_icon)
                    viewBinding.searchBar.navigationContentDescription = null
                    viewBinding.searchBar.setNavigationOnClickListener(null)

                    // Reset the current selected marker
                    currentMarkerId = null
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // endregion

        // region Configure the back button

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
    }

    override fun onResume() {
        super.onResume()

        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomDrawer)

        requireActivity().setLightStatusBar(
            viewBinding.searchView.currentTransitionState == TransitionState.SHOWN || bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        )
    }
}
