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
        super.onViewCreated(view, savedInstanceState)

        // region Map layers

        // region Temtem layer

        viewBinding.temtemButton.setOnClickListener {
            activityViewModel.changeTemtemLayerVisibility()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.temtemLayerState.collect {
                    viewBinding.temtemButton.setImageResource(if (it) R.drawable.temtem_button_check else R.drawable.temtem_button_uncheck)
                }
            }
        }

        // endregion

        // region Landmark layer

        viewBinding.landmarkButton.setOnClickListener {
            activityViewModel.changeLandmarkLayerVisibility()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.landmarkLayerState.collect {
                    viewBinding.landmarkButton.setImageResource(if (it) R.drawable.landmark_button_check else R.drawable.landmark_button_uncheck)
                }
            }
        }

        // endregion

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.mapState.collect {
                    when (it) {
                        is MapState.Success, is MapState.Update -> {
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

        // region Navigation

        viewBinding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // endregion
    }
}
