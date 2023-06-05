package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.closeKeyboard
import com.temtem.interactive.map.temzone.core.extension.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SignInFragmentBinding
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private val viewModel: SignInViewModel by viewModels()
    private val viewBinding: SignInFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region Configure sign in button

        viewBinding.signInButton.setOnClickListener {
            val email = viewBinding.emailEditText.text.toString().trim()
            val password = viewBinding.passwordEditText.text.toString().trim()

            closeKeyboard()
            viewModel.signIn(email, password)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect {
                    when (it) {
                        is SignInState.Empty -> Unit

                        is SignInState.Loading -> {
                            viewBinding.signInButton.isEnabled = false
                        }

                        is SignInState.Success -> {
                            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToMapFragment())
                        }

                        is SignInState.Error -> {
                            viewBinding.signInButton.isEnabled = true
                            viewBinding.passwordTextInputLayout.setErrorAndRequestFocus(it.passwordMessage)
                            viewBinding.emailTextInputLayout.setErrorAndRequestFocus(it.emailMessage)

                            if (it.snackbarMessage != null) {
                                Snackbar.make(
                                    viewBinding.root,
                                    it.snackbarMessage,
                                    Snackbar.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }
                }
            }
        }

        // endregion

        // region Configure the navigation

        viewBinding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToForgotPasswordFragment())
        }

        viewBinding.signUpTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToSignUpFragment())
        }

        // endregion
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(false)
    }
}
