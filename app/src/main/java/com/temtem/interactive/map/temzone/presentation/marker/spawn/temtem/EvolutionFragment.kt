package com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.databinding.EvolutionFragmentBinding
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn

class EvolutionFragment(
    private val spawn: Spawn,
) : Fragment(R.layout.evolution_fragment) {

    private val viewBinding: EvolutionFragmentBinding by viewBindings()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (spawn.temtem.evolutions.isEmpty()) {
            viewBinding.evolutionTextView.text =
                resources.getString(R.string.evolution_template, spawn.temtem.name)
        } else {
            viewBinding.evolutionTextView.visibility = View.GONE
        }

        viewBinding.evolutionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.evolutionRecyclerView.adapter = EvolutionAdapter(
            requireContext(),
            spawn.temtem.evolutions,
        )
    }
}
