package com.temtem.interactive.map.temzone.presentation.marker.spawn

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayoutMediator
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.di.GlideApp
import com.temtem.interactive.map.temzone.databinding.SpawnFragmentBinding
import com.temtem.interactive.map.temzone.presentation.map.MapViewModel
import com.temtem.interactive.map.temzone.presentation.marker.spawn.state.SpawnState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SpawnFragment(
    private val id: String,
    private val obtained: Boolean,
) : Fragment(R.layout.spawn_fragment) {

    private val viewModel: SpawnViewModel by viewModels()
    private val activityViewModel: MapViewModel by activityViewModels()
    private val viewBinding: SpawnFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getSpawn(id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.retryButton.setOnClickListener {
            viewModel.getSpawn(id)
        }

        viewBinding.obtainedSwitch.isChecked = obtained
        viewBinding.obtainedSwitch.jumpDrawablesToCurrentState()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.obtainedState.collect { obtained ->
                    viewBinding.obtainedSwitch.isChecked = obtained
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.spawnState.collect {
                    when (it) {
                        is SpawnState.Loading -> {
                            viewBinding.tabLayout.visibility = View.GONE
                            viewBinding.progressBar.visibility = View.VISIBLE
                            viewBinding.errorTextView.visibility = View.GONE
                            viewBinding.retryButton.visibility = View.GONE
                        }

                        is SpawnState.Success -> {
                            viewBinding.progressBar.visibility = View.GONE

                            viewBinding.nameTextView.text = it.spawn.temtem.name.split(" ").first()
                            viewBinding.idTextView.text =
                                resources.getString(R.string.id_template, it.spawn.temtem.id)
                            GlideApp.with(requireContext())
                                .load(it.spawn.temtem.imageUrl)
                                .into(viewBinding.temtemImageView)
                            viewBinding.primaryTypeImageView.contentDescription =
                                it.spawn.temtem.types.first().name
                            GlideApp.with(requireContext())
                                .load(it.spawn.temtem.types.first().imageUrl)
                                .into(viewBinding.primaryTypeImageView)
                            if (it.spawn.temtem.types.size == 2) {
                                viewBinding.secondaryTypeImageView.contentDescription =
                                    it.spawn.temtem.types.last().name
                                GlideApp.with(requireContext())
                                    .load(it.spawn.temtem.types.last().imageUrl)
                                    .into(viewBinding.secondaryTypeImageView)
                            } else {
                                viewBinding.secondaryTypeImageView.visibility = View.GONE
                            }
                            viewBinding.obtainedSwitch.setOnCheckedChangeListener { _, _ ->
                                viewModel.setTemtemObtained(it.spawn.temtem.id)
                                activityViewModel.setTemtemObtained(it.spawn.temtem.name)
                            }
                            viewBinding.tabLayout.visibility = View.VISIBLE
                            viewBinding.viewPager.adapter = SpawnFragmentStateAdapter(
                                this@SpawnFragment,
                                it.spawn,
                            )
                            TabLayoutMediator(
                                viewBinding.tabLayout,
                                viewBinding.viewPager,
                            ) { tab, position ->
                                when (position) {
                                    0 -> tab.text = resources.getString(R.string.about)
                                    1 -> tab.text = resources.getString(R.string.base_stats)
                                    2 -> tab.text = resources.getString(R.string.evolution)
                                }
                            }.attach()
                        }

                        is SpawnState.Error -> {
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
