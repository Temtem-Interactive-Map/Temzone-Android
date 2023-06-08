package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.closeKeyboard
import com.temtem.interactive.map.temzone.core.extension.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SignInFragmentBinding
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInFormState
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInGoogleState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private companion object {
        private const val REQUEST_GOOGLE = 1000
    }

    @Inject
    lateinit var signInClient: SignInClient

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
        // region Sign in with email and password

        // Add the sign in button click listener
        viewBinding.signInButton.setOnClickListener {
            val email = viewBinding.emailEditText.text.toString().trim()
            val password = viewBinding.passwordEditText.text.toString().trim()

            closeKeyboard()
            viewModel.signInWithEmailAndPassword(email, password)
        }

        // Observe the sign in form state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInFormState.collect {
                    when (it) {
                        is SignInFormState.Empty -> Unit

                        is SignInFormState.Loading -> {
                            viewBinding.signInButton.isEnabled = false
                        }

                        is SignInFormState.Success -> {
                            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToMapFragment())
                        }

                        is SignInFormState.Error -> {
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

        // region Sign in with Google

        // Add the sign in button click listener
        viewBinding.googleButton.setOnClickListener {
            viewModel.requestSignInWithGoogle()
        }

        // Observe the sign in with Google state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInGoogleState.collect {
                    when (it) {
                        is SignInGoogleState.Empty -> Unit

                        is SignInGoogleState.Loading -> {
                            viewBinding.googleButton.isEnabled = false
                        }

                        is SignInGoogleState.Request -> {
                            startIntentSenderForResult(
                                it.result.pendingIntent.intentSender,
                                REQUEST_GOOGLE,
                                null,
                                0,
                                0,
                                0,
                                null,
                            )
                        }

                        is SignInGoogleState.Success -> {
                            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToMapFragment())
                        }

                        is SignInGoogleState.Error -> {
                            viewBinding.googleButton.isEnabled = true

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

        // endregion

        // region Navigation

        // Navigate to the forgot password fragment
        viewBinding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToForgotPasswordFragment())
        }

        // Navigate to the sign up fragment
        viewBinding.signUpTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToSignUpFragment())
        }

        // endregion
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_GOOGLE) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(data)

                viewModel.signInWithGoogle(credential.googleIdToken)
            } catch (e: ApiException) {
                viewBinding.googleButton.isEnabled = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(false)
    }
}
