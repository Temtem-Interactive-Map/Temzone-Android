package com.temtem.interactive.map.temzone.presentation.map

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.databinding.MapLayersDialogFragmentBinding
import com.temtem.interactive.map.temzone.presentation.map.state.MapState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapLayersDialogFragment : BottomSheetDialogFragment(R.layout.map_layers_dialog_fragment) {

    private val activityViewModel: MapViewModel by activityViewModels()
    private val viewBinding: MapLayersDialogFragmentBinding by viewBindings()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set the dialog to be expanded by default
        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomDrawer =
                bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)!!
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomDrawer)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Map layers

        // region Temtem layer

        // Add the temtem button click listener
        viewBinding.temtemButton.setOnClickListener {
            activityViewModel.changeTemtemLayerVisibility()
        }

        // Observe the temtem button state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.temtemLayerState.collect {
                    viewBinding.temtemButton.setImageResource(if (it) R.drawable.temtem_check_button else R.drawable.temtem_uncheck_button)
                }
            }
        }

        // endregion

        // region Landmark layer

        // Add the landmark button click listener
        viewBinding.landmarkButton.setOnClickListener {
            activityViewModel.changeLandmarkLayerVisibility()
        }

        // Observe the landmark button state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.landmarkLayerState.collect {
                    viewBinding.landmarkButton.setImageResource(if (it) R.drawable.landmark_check_button else R.drawable.landmark_uncheck_button)
                }
            }
        }

        // endregion

        // Observe the map state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.mapState.collect {
                    when (it) {
                        is MapState.Success -> {
                            viewBinding.temtemButton.isEnabled = true
                            viewBinding.landmarkButton.isEnabled = true
                        }

                        else -> {
                            viewBinding.temtemButton.isEnabled = false
                            viewBinding.landmarkButton.isEnabled = false
                        }
                    }
                }
            }
        }

        // endregion

        // Navigate to the previous fragment
        viewBinding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
