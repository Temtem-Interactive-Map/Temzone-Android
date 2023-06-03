package com.temtem.interactive.map.temzone.ui.auth.sign_in

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
import com.temtem.interactive.map.temzone.databinding.SignInFragmentBinding
import com.temtem.interactive.map.temzone.ui.auth.sign_in.state.SignInUiState
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.closeKeyboard
import com.temtem.interactive.map.temzone.utils.extensions.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private val viewModel: SignInViewModel by viewModels()
    private val viewBinding: SignInFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

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
                viewModel.signInUiState.collect {
                    when (it) {
                        is SignInUiState.Empty -> Unit

                        is SignInUiState.Loading -> {
                            viewBinding.signInButton.isEnabled = false
                        }

                        is SignInUiState.Success -> {
                            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToMapFragment())
                        }

                        is SignInUiState.Error -> {
                            viewBinding.signInButton.isEnabled = true

                            if (it.message != null) {
                                viewBinding.passwordTextInputLayout.setErrorAndRequestFocus(null)
                                viewBinding.emailTextInputLayout.setErrorAndRequestFocus(null)

                                Snackbar.make(
                                    viewBinding.root,
                                    it.message,
                                    Snackbar.LENGTH_SHORT,
                                ).show()
                            } else {
                                viewBinding.passwordTextInputLayout.setErrorAndRequestFocus(it.passwordMessage)
                                viewBinding.emailTextInputLayout.setErrorAndRequestFocus(it.emailMessage)
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
