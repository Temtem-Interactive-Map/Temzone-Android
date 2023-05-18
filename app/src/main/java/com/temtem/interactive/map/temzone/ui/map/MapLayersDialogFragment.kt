package com.temtem.interactive.map.temzone.ui.map

import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapLayersDialogFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings

class MapLayersDialogFragment : BottomSheetDialogFragment(R.layout.map_layers_dialog_fragment) {

    private val viewModel: MapViewModel by viewModels()
    private val viewBinding: MapLayersDialogFragmentBinding by viewBindings()
}
