package com.temtem.interactive.map.temzone.ui.auth

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
import com.temtem.interactive.map.temzone.databinding.SignUpFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar

class SignUpFragment : Fragment(R.layout.sign_up_fragment) {

    private val viewBinding: SignUpFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        setLightStatusBar(false)
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

        viewBinding.signUpButton.setOnClickListener {
            val direction = SignUpFragmentDirections.fromSignUpFragmentToMapFragment()

            findNavController().navigate(direction)
        }

        viewBinding.signInTextView.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()

        setLightStatusBar(false)
    }
}
