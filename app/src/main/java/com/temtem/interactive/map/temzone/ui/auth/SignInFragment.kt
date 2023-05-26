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
import com.temtem.interactive.map.temzone.databinding.SignInFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings

class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private val viewBinding: SignInFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.forgotPasswordTextView.setOnClickListener {
            val direction = SignInFragmentDirections.fromSignInFragmentToForgotPasswordFragment()

            findNavController().navigate(direction)
        }

        viewBinding.signInButton.setOnClickListener {
            val direction = SignInFragmentDirections.fromSignInFragmentToMapFragment()

            findNavController().navigate(direction)
        }

        viewBinding.signUpTextView.setOnClickListener {
            val direction = SignInFragmentDirections.fromSignInFragmentToSignUpFragment()

            findNavController().navigate(direction)
        }
    }
}
