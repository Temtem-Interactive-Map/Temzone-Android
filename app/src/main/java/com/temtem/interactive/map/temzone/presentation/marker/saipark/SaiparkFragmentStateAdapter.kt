package com.temtem.interactive.map.temzone.presentation.marker.saipark

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.temtem.interactive.map.temzone.domain.repository.temzone.model.marker.saipark.Saipark
import com.temtem.interactive.map.temzone.presentation.marker.saipark.area.AreaFragment

class SaiparkFragmentStateAdapter(
    fragment: Fragment,
    private val saipark: Saipark,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AreaFragment(saipark.areas.first())
            1 -> AreaFragment(saipark.areas.last())
            else -> throw IllegalStateException("Position $position is invalid for this viewpager")
        }
    }
}
