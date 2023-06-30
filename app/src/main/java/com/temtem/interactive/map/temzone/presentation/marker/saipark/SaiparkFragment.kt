package com.temtem.interactive.map.temzone.presentation.marker.saipark

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.di.GlideApp
import com.temtem.interactive.map.temzone.databinding.SaiparkFragmentBinding
import com.temtem.interactive.map.temzone.presentation.marker.saipark.state.SaiparkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SaiparkFragment(
    private val id: String,
) : Fragment(R.layout.saipark_fragment) {

    private val viewModel: SaiparkViewModel by viewModels()
    private val viewBinding: SaiparkFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSaipark(id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.retryButton.setOnClickListener {
            viewModel.getSaipark(id)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.saiparkState.collect {
                    when (it) {
                        is SaiparkState.Loading -> {
                            viewBinding.tabLayout.visibility = View.GONE
                            viewBinding.progressBar.visibility = View.VISIBLE
                            viewBinding.errorTextView.visibility = View.GONE
                            viewBinding.retryButton.visibility = View.GONE
                        }

                        is SaiparkState.Success -> {
                            viewBinding.progressBar.visibility = View.GONE

                            viewBinding.tabLayout.visibility = View.VISIBLE
                            viewBinding.viewPager.adapter = SaiparkFragmentStateAdapter(
                                this@SaiparkFragment,
                                it.saipark,
                            )
                            viewBinding.tabLayout.addOnTabSelectedListener(
                                object : TabLayout.OnTabSelectedListener {
                                    override fun onTabSelected(tab: TabLayout.Tab) {
                                        val area = when (tab.position) {
                                            0 -> it.saipark.areas.first()
                                            1 -> it.saipark.areas.last()
                                            else -> throw IllegalStateException("Position ${tab.position} is invalid for this viewpager")
                                        }

                                        viewBinding.nameTextView.text =
                                            area.temtem.name.split(" ").first()
                                        viewBinding.idTextView.text = resources.getString(
                                            R.string.id_template,
                                            area.temtem.id,
                                        )
                                        GlideApp.with(requireContext())
                                            .load(area.temtem.imageUrl)
                                            .into(viewBinding.temtemImageView)
                                        viewBinding.primaryTypeImageView.contentDescription =
                                            area.temtem.types.first().name
                                        GlideApp.with(requireContext())
                                            .load(area.temtem.types.first().imageUrl)
                                            .into(viewBinding.primaryTypeImageView)
                                        if (area.temtem.types.size == 2) {
                                            viewBinding.secondaryTypeImageView.contentDescription =
                                                area.temtem.types.last().name
                                            GlideApp.with(requireContext())
                                                .load(area.temtem.types.last().imageUrl)
                                                .into(viewBinding.secondaryTypeImageView)
                                        } else {
                                            viewBinding.secondaryTypeImageView.visibility =
                                                View.GONE
                                        }
                                    }

                                    override fun onTabUnselected(tab: TabLayout.Tab) {}

                                    override fun onTabReselected(tab: TabLayout.Tab) {}
                                }
                            )

                            TabLayoutMediator(
                                viewBinding.tabLayout,
                                viewBinding.viewPager,
                            ) { tab, position ->
                                when (position) {
                                    0 -> tab.text = it.saipark.areas.first().name
                                    1 -> tab.text = it.saipark.areas.last().name
                                }
                            }.attach()
                        }

                        is SaiparkState.Error -> {
                            viewBinding.progressBar.visibility = View.GONE
                            viewBinding.errorTextView.visibility = View.VISIBLE
                            viewBinding.errorTextView.text = it.message
                            viewBinding.retryButton.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
