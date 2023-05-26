package com.temtem.interactive.map.temzone.ui.settings

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.SettingsFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val viewBinding: SettingsFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        setLightStatusBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Configure window insets

        ViewCompat.setOnApplyWindowInsetsListener(viewBinding.root) { windowView, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            windowView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            windowInsets
        }

        // endregion

        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        viewBinding.signOutButton.setOnClickListener {
            val direction = SettingsFragmentDirections.fromSettingsFragmentToSignInFragment()

            findNavController().navigate(direction)
        }
    }

    override fun onResume() {
        super.onResume()

        setLightStatusBar(true)
    }
}
