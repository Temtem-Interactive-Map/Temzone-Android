package com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.databinding.StatsFragmentBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn

class StatsFragment(
    private val spawn: Spawn,
) : Fragment(R.layout.stats_fragment) {

    private val viewBinding: StatsFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.hpTextView.text = spawn.temtem.stats.hp.toString()
        viewBinding.hpProgressIndicator.progress = spawn.temtem.stats.hp
        viewBinding.staTextView.text = spawn.temtem.stats.sta.toString()
        viewBinding.staProgressIndicator.progress = spawn.temtem.stats.sta
        viewBinding.spdTextView.text = spawn.temtem.stats.spd.toString()
        viewBinding.spdProgressIndicator.progress = spawn.temtem.stats.spd
        viewBinding.atkTextView.text = spawn.temtem.stats.atk.toString()
        viewBinding.atkProgressIndicator.progress = spawn.temtem.stats.atk
        viewBinding.defTextView.text = spawn.temtem.stats.def.toString()
        viewBinding.defProgressIndicator.progress = spawn.temtem.stats.def
        viewBinding.spatkTextView.text = spawn.temtem.stats.spatk.toString()
        viewBinding.spatkProgressIndicator.progress = spawn.temtem.stats.spatk
        viewBinding.spdefTextView.text = spawn.temtem.stats.spdef.toString()
        viewBinding.spdefProgressIndicator.progress = spawn.temtem.stats.spdef
        viewBinding.totalTextView.text = spawn.temtem.stats.total.toString()
        viewBinding.totalProgressIndicator.progress = spawn.temtem.stats.total
    }
}
