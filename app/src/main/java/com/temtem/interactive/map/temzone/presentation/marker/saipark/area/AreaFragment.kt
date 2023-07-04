package com.temtem.interactive.map.temzone.presentation.marker.saipark.area

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.databinding.AreaFragmentBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.area.Area

class AreaFragment(
    private val area: Area,
) : Fragment(R.layout.area_fragment) {

    private val viewBinding: AreaFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.rateTextView.text = resources.getString(R.string.rate_template, area.rate)
        viewBinding.lumaRateTextView.text =
            resources.getString(R.string.luma_rate_template, area.lumaRate)
        viewBinding.minSvsTextView.text =
            resources.getString(R.string.min_svs_template, area.minSvs)
        viewBinding.eggMovesTextView.text =
            resources.getString(R.string.egg_moves_template, area.eggMoves)
    }
}
