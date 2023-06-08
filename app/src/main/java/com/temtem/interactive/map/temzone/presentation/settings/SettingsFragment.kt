package com.temtem.interactive.map.temzone.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SettingsFragmentBinding
import com.temtem.interactive.map.temzone.presentation.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val activityViewModel: MapViewModel by activityViewModels()
    private val viewBinding: SettingsFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Sign out

        // Add the sign out button click listener
        viewBinding.signOutButton.setOnClickListener {
            viewBinding.signOutButton.isEnabled = false
            activityViewModel.signOut()
        }

        // Observe the sign out state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                activityViewModel.signOutState.collect {
                    findNavController().navigate(SettingsFragmentDirections.fromSettingsFragmentToSignInFragment())
                }
            }
        }

        // endregion

        // Navigate to the previous fragment
        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(true)
    }
}
