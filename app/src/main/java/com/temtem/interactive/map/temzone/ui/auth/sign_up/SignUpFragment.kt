package com.temtem.interactive.map.temzone.ui.auth.sign_up

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.SignUpFragmentBinding
import com.temtem.interactive.map.temzone.ui.auth.sign_up.state.SignUpUiState
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.sign_up_fragment) {

    private val viewModel: SignUpViewModel by viewModels()
    private val viewBinding: SignUpFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Configure the sign up button

        viewBinding.signUpButton.setOnClickListener {
            val email = viewBinding.emailEditText.text.toString().trim()
            val password = viewBinding.passwordEditText.text.toString().trim()
            val confirmPassword = viewBinding.confirmPasswordEditText.text.toString().trim()

            viewModel.signUp(email, password, confirmPassword)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpUiState.collect {
                    when (it) {
                        is SignUpUiState.Empty -> Unit

                        is SignUpUiState.Loading -> {
                            viewBinding.signUpButton.isEnabled = false
                        }

                        is SignUpUiState.Success -> {
                            findNavController().navigate(SignUpFragmentDirections.fromSignUpFragmentToMapFragment())
                        }

                        is SignUpUiState.Error -> {
                            viewBinding.signUpButton.isEnabled = true
                            viewBinding.confirmPasswordTextInputLayout.setErrorAndRequestFocus(it.confirmPasswordError)
                            viewBinding.passwordTextInputLayout.setErrorAndRequestFocus(it.passwordError)
                            viewBinding.emailTextInputLayout.setErrorAndRequestFocus(it.emailError)
                        }
                    }
                }
            }
        }

        // endregion

        // region Configure the navigation

        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.fromSignUpFragmentToSignInFragment())
        }

        viewBinding.signInTextView.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.fromSignUpFragmentToSignInFragment())
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(SignUpFragmentDirections.fromSignUpFragmentToSignInFragment())
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
