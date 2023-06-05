package com.temtem.interactive.map.temzone.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.core.utils.extensions.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SettingsFragmentBinding
import com.temtem.interactive.map.temzone.presentation.map.MapViewModel
import dagger.hilt.android.AndroidEntryPoint

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
        // region Configure sign out button

        viewBinding.signOutButton.setOnClickListener {
            activityViewModel.signOut()

            findNavController().navigate(SettingsFragmentDirections.fromSettingsFragmentToSignInFragment())
        }

        // endregion

        // region Configure the navigation

        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // endregion
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(true)
    }
}
