package com.temtem.interactive.map.temzone.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.BuildConfig
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.hasNotificationPermission
import com.temtem.interactive.map.temzone.core.extension.requestNotificationPermission
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SettingsFragmentBinding
import com.temtem.interactive.map.temzone.presentation.map.MapViewModel
import com.temtem.interactive.map.temzone.presentation.pdf.PdfFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val activityViewModel: MapViewModel by activityViewModels()
    private val viewModel: SettingsViewModel by viewModels()
    private val viewBinding: SettingsFragmentBinding by viewBindings()
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewBinding.notificationsSwitch.isChecked = it
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Account

        // Add the notifications switch click listener
        viewBinding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestNotificationPermission(requestPermissionLauncher)
            }

            viewModel.updateNotificationPreference(isChecked)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewBinding.notificationsSwitch.isChecked =
                    viewModel.getNotificationPreference() && hasNotificationPermission()

                viewBinding.notificationsSwitch.jumpDrawablesToCurrentState()
            }
        }

        // endregion

        // region About

        // Navigate to the pdf fragment to show the terms of service
        viewBinding.termsOfServiceTextView.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.fromSettingsFragmentToPdfFragment(
                    title = getString(R.string.terms_of_service),
                    filename = PdfFragment.TERMS_OF_SERVICE,
                )
            )
        }

        // Navigate to the pdf fragment to show the terms of service
        viewBinding.privacyPolicyTextView.setOnClickListener {
            findNavController().navigate(
                SettingsFragmentDirections.fromSettingsFragmentToPdfFragment(
                    title = getString(R.string.privacy_policy),
                    filename = PdfFragment.PRIVACY_POLICY,
                )
            )
        }

        // endregion

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

        // Temzone version
        viewBinding.versionTextView.text = getString(
            R.string.app_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE,
        )

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
