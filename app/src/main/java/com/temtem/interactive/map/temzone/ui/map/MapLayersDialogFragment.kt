package com.temtem.interactive.map.temzone.ui.map

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.MapLayersDialogFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBinding

class MapLayersDialogFragment : BottomSheetDialogFragment(R.layout.map_layers_dialog_fragment) {

    private val binding: MapLayersDialogFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
