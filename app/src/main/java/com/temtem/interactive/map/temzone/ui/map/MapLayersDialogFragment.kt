package com.temtem.interactive.map.temzone.ui.map

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
import com.temtem.interactive.map.temzone.databinding.MapLayersDialogFragmentBinding
import com.temtem.interactive.map.temzone.ui.MainViewModel
import com.temtem.interactive.map.temzone.ui.map.state.MarkersUiState
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapLayersDialogFragment : BottomSheetDialogFragment(R.layout.map_layers_dialog_fragment) {

    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewBinding: MapLayersDialogFragmentBinding by viewBindings()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

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
        // region Configure the map layer buttons

        viewBinding.temtemButton.setOnClickListener {
            activityViewModel.changeTemtemLayerVisibility()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.temtemLayerUiState.collect {
                    viewBinding.temtemButton.setImageResource(if (it) R.drawable.temtem_check_button else R.drawable.temtem_uncheck_button)
                }
            }
        }

        viewBinding.landmarkButton.setOnClickListener {
            activityViewModel.changeLandmarkLayerVisibility()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.landmarkLayerUiState.collect {
                    viewBinding.landmarkButton.setImageResource(if (it) R.drawable.landmark_check_button else R.drawable.landmark_uncheck_button)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.markersUiState.collect {
                    when (it) {
                        is MarkersUiState.Success -> {
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

        // region Configure the navigation

        viewBinding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // endregion
    }
}
