package com.temtem.interactive.map.temzone.presentation.marker.spawn

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.spawn.Spawn
import com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem.AboutFragment
import com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem.EvolutionFragment
import com.temtem.interactive.map.temzone.presentation.marker.spawn.temtem.StatsFragment

class SpawnFragmentStateAdapter(
    fragment: Fragment,
    private val spawn: Spawn,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AboutFragment(spawn)
            1 -> StatsFragment(spawn)
            2 -> EvolutionFragment(spawn)
            else -> throw IllegalStateException("Position $position is invalid for this viewpager")
        }
    }
}
