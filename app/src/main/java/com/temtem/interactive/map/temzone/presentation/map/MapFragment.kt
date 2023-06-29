package com.temtem.interactive.map.temzone.presentation.map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.search.SearchView.TransitionState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.MarkerView
import com.temtem.interactive.map.temzone.core.extension.closeKeyboard
import com.temtem.interactive.map.temzone.core.extension.dpToPx
import com.temtem.interactive.map.temzone.core.extension.hideAndDisable
import com.temtem.interactive.map.temzone.core.extension.moveToPosition
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.core.extension.showAndEnable
import com.temtem.interactive.map.temzone.databinding.MapFragmentBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.Marker
import com.temtem.interactive.map.temzone.presentation.map.search.LoaderAdapter
import com.temtem.interactive.map.temzone.presentation.map.search.MarkerAdapter
import com.temtem.interactive.map.temzone.presentation.map.search.MarkerComparator
import com.temtem.interactive.map.temzone.presentation.map.state.MapState
import com.temtem.interactive.map.temzone.presentation.marker.saipark.SaiparkFragment
import com.temtem.interactive.map.temzone.presentation.marker.spawn.SpawnFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ovh.plrapps.mapview.MapViewConfiguration
import ovh.plrapps.mapview.api.MinimumScaleMode
import ovh.plrapps.mapview.api.addMarker
import ovh.plrapps.mapview.api.constrainScroll
import ovh.plrapps.mapview.api.getMarkerByTag
import ovh.plrapps.mapview.api.setMarkerTapListener
import ovh.plrapps.mapview.core.TileStreamProvider
import ovh.plrapps.mapview.markers.MarkerTapListener
import java.io.IOException
import kotlin.math.max
import kotlin.math.pow

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.map_fragment) {

    private companion object {
        private const val SCALE = 3f
        private const val ZOOM = 6
        private const val TILE_SIZE = 256
        private val MAP_SIZE = TILE_SIZE * 2.0.pow(ZOOM).toInt()
        private val MAP_CENTER = MAP_SIZE / 2.0
        private const val MAP_MIN_HORIZONTAL = TILE_SIZE * 7.0
        private val MAP_MAX_HORIZONTAL = MAP_SIZE - TILE_SIZE * 7.0
        private const val MAP_MIN_VERTICAL = TILE_SIZE * 11.0
        private val MAP_MAX_VERTICAL = MAP_SIZE - TILE_SIZE * 11.0
    }

    private val activityViewModel: MapViewModel by activityViewModels()
    private val viewBinding: MapFragmentBinding by viewBindings()
    private val markerAdapter by lazy {
        MarkerAdapter(MarkerComparator, requireContext()) { marker ->
            viewBinding.mapView.getMarkerByTag(marker.id)?.let { view ->
                viewBinding.searchBar.text = viewBinding.searchView.text
                viewBinding.searchView.hide()

                // Make sure the layer with the marker is visible
                val previousSelectedMarker = selectedMarker
                selectedMarker = view as MarkerView
                when (marker.type) {
                    is Marker.Type.Spawn -> {
                        activityViewModel.changeTemtemLayerVisibility(true)
                    }

                    is Marker.Type.Saipark -> {
                        activityViewModel.changeLandmarkLayerVisibility(true)
                    }
                }
                selectedMarker = previousSelectedMarker

                // Delay the show of the marker details to let the search view animation finish
                Handler(Looper.getMainLooper()).postDelayed({
                    showMarkerDetails(view)
                }, 500)
            }
        }
    }

    private var selectedMarker: MarkerView? = null
    private var canCollapseBottomSheet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet)

        // region Search

        lifecycleScope.launch {
            markerAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        viewBinding.searchRecyclerView.visibility = View.GONE
                        viewBinding.noResultsLayout.visibility = View.GONE
                        viewBinding.progressBar.visibility = View.VISIBLE
                    }

                    is LoadState.Error -> {
                        viewBinding.noResultsLayout.visibility = View.VISIBLE
                        viewBinding.progressBar.visibility = View.GONE
                    }

                    is LoadState.NotLoading -> {
                        viewBinding.searchRecyclerView.visibility = View.VISIBLE
                        viewBinding.noResultsLayout.isVisible = markerAdapter.itemCount == 0
                        viewBinding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewBinding.searchView.toolbar.setNavigationOnClickListener {
            viewBinding.searchView.hide()

            if (viewBinding.searchBar.text.isNullOrEmpty()) {
                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        markerAdapter.submitData(PagingData.empty())
                        viewBinding.noResultsLayout.visibility = View.GONE
                    }
                }
            }
        }

        viewBinding.searchView.editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                closeKeyboard()
                lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        val query = v.text.trim().toString()

                        activityViewModel.search(query).collectLatest {
                            markerAdapter.submitData(it)
                        }
                    }
                }
            }
            true
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

        viewBinding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.searchRecyclerView.adapter =
            markerAdapter.withLoadStateFooter(LoaderAdapter(markerAdapter))

        // Fix the recycler view bottom margin to avoid the last item to be hidden
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.searchRecyclerView) { searchRecyclerView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            searchRecyclerView.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.bottom
            }

            windowInsets
        }

        // endregion

        // region Map layers

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

        // region Map

        val tiles = TileStreamProvider { row, col, zoom ->
            try {
                resources.assets.open("tiles/$zoom/$col/$row.png")
            } catch (e: IOException) {
                null
            }
        }

        val config = MapViewConfiguration(ZOOM + 1, MAP_SIZE, MAP_SIZE, TILE_SIZE, tiles).apply {
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
                if (view.visibility != View.VISIBLE) return

                showMarkerDetails(view as MarkerView)
            }
        })

        // Collapse the bottom sheet when the map is tapped or panned
        viewBinding.mapView.addReferentialListener {
            if (canCollapseBottomSheet && bottomSheetBehavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.mapState.collect {
                    when (it) {
                        is MapState.Empty -> {
                            activityViewModel.getMarkers()
                        }

                        is MapState.Loading -> Unit

                        is MapState.Success -> {
                            it.markers.forEach { marker ->
                                addMarker(marker)
                            }

                            // On notification click, show the marker details
                            requireActivity().intent.extras?.getString("id")?.let { id ->
                                val markerView =
                                    viewBinding.mapView.getMarkerByTag(id) as MarkerView

                                showMarkerDetails(markerView)
                            }
                        }

                        is MapState.Update -> {
                            it.newMarkers.forEach { marker ->
                                addMarker(marker)
                            }
                            it.oldMarkers.forEach { marker ->
                                removeMarker(marker)
                            }

                            // If the current marker is no longer in the list of markers
                            // to show, reset the search bar menu and hide the bottom sheet
                            val markerVisible = it.newMarkers.any { marker ->
                                selectedMarker?.id == marker.id
                            }

                            if (!markerVisible) {
                                hideMarkerDetails()
                            }
                        }

                        is MapState.Error -> {
                            Snackbar.make(
                                viewBinding.root,
                                it.snackbarMessage,
                                Snackbar.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            }
        }

        // endregion

        // region Bottom sheet

        viewBinding.toolbar.setNavigationOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                view.findViewById<NestedScrollView>(R.id.nested_scroll_view)?.scrollTo(0, 0)
            }
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.bottomSheet) { bottomSheetView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            bottomSheetBehavior.expandedOffset = insets.top + requireContext().dpToPx(16)
            bottomSheetView.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = insets.top + requireContext().dpToPx(16)
            }

            windowInsets
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (selectedMarker != null && newState == BottomSheetBehavior.STATE_EXPANDED) {
                    viewBinding.mapView.moveToPosition(
                        selectedMarker!!.x,
                        selectedMarker!!.y + resources.displayMetrics.heightPixels / (8 * SCALE),
                        SCALE,
                        false,
                    )
                    delayCollapseBottomSheet()
                } else if (canCollapseBottomSheet && selectedMarker != null && newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    viewBinding.mapView.moveToPosition(
                        selectedMarker!!.x,
                        selectedMarker!!.y + resources.displayMetrics.heightPixels / (8 * SCALE),
                        SCALE,
                        true,
                    )
                    delayCollapseBottomSheet()
                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    hideMarkerDetails()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        // endregion

        // region Navigation

        viewBinding.searchBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search_bar_menu_settings -> {
                    findNavController().navigate(MapFragmentDirections.fromMapFragmentToSettingsFragment())
                }
            }
            true
        }

        viewBinding.mapLayerFloatingActionButton.setOnClickListener {
            findNavController().navigate(MapFragmentDirections.fromMapFragmentToMapLayersDialogFragment())
        }

        // region Back button behavior

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (viewBinding.searchView.currentTransitionState) {
                    TransitionState.SHOWN, TransitionState.SHOWING -> {
                        viewBinding.searchView.hide()

                        if (viewBinding.searchBar.text.isNullOrEmpty()) {
                            lifecycleScope.launch {
                                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                    markerAdapter.submitData(PagingData.empty())
                                    viewBinding.noResultsLayout.visibility = View.GONE
                                }
                            }
                        }
                    }

                    else -> when (bottomSheetBehavior.state) {
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                            view.findViewById<NestedScrollView>(R.id.nested_scroll_view)
                                ?.scrollTo(0, 0)
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            hideMarkerDetails()

                            view.findViewById<NestedScrollView>(R.id.nested_scroll_view)
                                ?.scrollTo(0, 0)
                        }

                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            hideMarkerDetails()
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
            requireActivity(),
            onBackPressedCallback,
        )

        // endregion

        // endregion
    }

    private fun addMarker(marker: Marker) {
        val view = viewBinding.mapView.getMarkerByTag(marker.id) as MarkerView?

        if (view == null) {
            val markerView = MarkerView.fromMarker(marker, requireContext()).apply {
                visibility = View.VISIBLE
            }

            viewBinding.mapView.addMarker(
                markerView,
                markerView.x,
                markerView.y,
                -0.5f,
                -0.5f,
                0f,
                0f,
                markerView.id,
            )
        } else {
            view.visibility = View.VISIBLE
            view.drawable.alpha = if (marker.obtained) 153 else 255
        }
    }

    private fun removeMarker(marker: Marker) {
        val view = viewBinding.mapView.getMarkerByTag(marker.id) as MarkerView?

        if (view == null) {
            val markerView = MarkerView.fromMarker(marker, requireContext()).apply {
                visibility = View.INVISIBLE
            }

            viewBinding.mapView.addMarker(
                markerView,
                markerView.x,
                markerView.y,
                -0.5f,
                -0.5f,
                0f,
                0f,
                markerView.id,
            )
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    private fun showMarkerDetails(markerView: MarkerView) {
        viewBinding.searchBar.apply {
            menu.clear()
            inflateMenu(R.menu.search_bar_back_menu)
            setNavigationIcon(R.drawable.ic_arrow_back_24)
            setNavigationContentDescription(R.string.arrow_back_navigation_content_description)
            setNavigationOnClickListener { hideMarkerDetails() }
        }

        viewBinding.mapLayerFloatingActionButton.hideAndDisable()

        viewBinding.mapView.moveToPosition(
            markerView.x,
            markerView.y + resources.displayMetrics.heightPixels / (8 * SCALE),
            SCALE,
            true,
        )
        delayCollapseBottomSheet()

        BottomSheetBehavior.from(viewBinding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        // Only replace the fragment if it's not already the one being shown
        if (selectedMarker?.id != markerView.id) {
            parentFragmentManager.beginTransaction().replace(
                viewBinding.bottomSheet.id,
                when (markerView.type) {
                    is Marker.Type.Spawn -> SpawnFragment(markerView.id)
                    is Marker.Type.Saipark -> SaiparkFragment(markerView.id)
                },
                markerView.id,
            ).commit()
        }
        selectedMarker = markerView
    }

    private fun hideMarkerDetails() {
        selectedMarker = null

        viewBinding.searchBar.apply {
            text = null
            menu.clear()
            inflateMenu(R.menu.search_bar_menu)
            setNavigationIcon(R.drawable.ic_search_24)
            navigationContentDescription = null
            setNavigationOnClickListener(null)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                markerAdapter.submitData(PagingData.empty())
                viewBinding.noResultsLayout.visibility = View.GONE
            }
        }

        BottomSheetBehavior.from(viewBinding.bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun delayCollapseBottomSheet() {
        // Delay the capability to collapse the bottom sheet in order to
        // prevent it from being immediately collapsed after moving the map.
        canCollapseBottomSheet = false
        Handler(Looper.getMainLooper()).postDelayed({
            canCollapseBottomSheet = true
        }, 500)
    }

    override fun onResume() {
        super.onResume()

        val bottomSheetBehavior = BottomSheetBehavior.from(viewBinding.bottomSheet)

        requireActivity().setLightStatusBar(
            viewBinding.searchView.currentTransitionState == TransitionState.SHOWN || bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        selectedMarker = null
    }
}
