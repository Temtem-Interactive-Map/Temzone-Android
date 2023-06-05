package com.temtem.interactive.map.temzone.presentation.auth.forgot_password

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.core.utils.extensions.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.ForgotPasswordFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.forgot_password_fragment) {

    private val viewBinding: ForgotPasswordFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Configure the navigation

        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.fromForgotPasswordFragmentToSignInFragment())
        }

        viewBinding.signUpTextView.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.fromForgotPasswordFragmentToSignUpFragment())
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(ForgotPasswordFragmentDirections.fromForgotPasswordFragmentToSignInFragment())
            }
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                onBackPressedCallback.isEnabled = true
            }

            override fun onPause(owner: LifecycleOwner) {
                onBackPressedCallback.isEnabled = false
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBackPressedCallback
        )

        // endregion
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(false)
    }
}
