package com.temtem.interactive.map.temzone.ui.map

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapLayersDialogFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings

class MapLayersDialogFragment : BottomSheetDialogFragment(R.layout.map_layers_dialog_fragment) {

    private companion object {
        private const val TEMTEM_BUTTON_CHECKED = "TEMTEM_BUTTON_CHECKED"
        private const val LANDMARK_BUTTON_CHECKED = "LANDMARK_BUTTON_CHECKED"
    }

    private val viewModel: MapViewModel by viewModels()
    private val viewBinding: MapLayersDialogFragmentBinding by viewBindings()

    private var temtemButtonChecked = true
    private var landmarkButtonChecked = true

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
        viewBinding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.temtemButton.setOnClickListener {
            temtemButtonChecked = !temtemButtonChecked

            if (temtemButtonChecked) {
                viewBinding.temtemButton.setImageResource(R.drawable.temtem_check_button)
            } else {
                viewBinding.temtemButton.setImageResource(R.drawable.temtem_uncheck_button)
            }
        }

        viewBinding.landmarkButton.setOnClickListener {
            landmarkButtonChecked = !landmarkButtonChecked

            if (landmarkButtonChecked) {
                viewBinding.landmarkButton.setImageResource(R.drawable.landmark_check_button)
            } else {
                viewBinding.landmarkButton.setImageResource(R.drawable.landmark_uncheck_button)
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            temtemButtonChecked = savedInstanceState.getBoolean(TEMTEM_BUTTON_CHECKED)
            if (temtemButtonChecked) {
                viewBinding.temtemButton.setImageResource(R.drawable.temtem_check_button)
            } else {
                viewBinding.temtemButton.setImageResource(R.drawable.temtem_uncheck_button)
            }

            landmarkButtonChecked = savedInstanceState.getBoolean(LANDMARK_BUTTON_CHECKED)
            if (landmarkButtonChecked) {
                viewBinding.landmarkButton.setImageResource(R.drawable.landmark_check_button)
            } else {
                viewBinding.landmarkButton.setImageResource(R.drawable.landmark_uncheck_button)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(TEMTEM_BUTTON_CHECKED, temtemButtonChecked)
        outState.putBoolean(LANDMARK_BUTTON_CHECKED, landmarkButtonChecked)
    }
}
