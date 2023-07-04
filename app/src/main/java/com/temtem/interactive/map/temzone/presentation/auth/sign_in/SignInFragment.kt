package com.temtem.interactive.map.temzone.presentation.auth.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.temtem.interactive.map.temzone.core.extension.requestNotificationPermission
import com.temtem.interactive.map.temzone.core.extension.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.SignInFragmentBinding
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInFormState
import com.temtem.interactive.map.temzone.presentation.auth.sign_in.state.SignInGoogleState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class SignInFragment : Fragment(R.layout.sign_in_fragment) {

    private companion object {
        private const val REQUEST_GOOGLE = 1000
    }

    @Inject
    lateinit var signInClient: SignInClient

    private val viewModel: SignInViewModel by viewModels()
    private val viewBinding: SignInFragmentBinding by viewBindings()
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // region Sign in with email and password

        viewBinding.signInButton.setOnClickListener {
            val email = viewBinding.emailEditText.text.toString().trim()
            val password = viewBinding.passwordEditText.text.toString().trim()

            closeKeyboard()
            viewModel.signInWithEmailAndPassword(email, password)
        }

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
                            requestNotificationPermission(requestPermissionLauncher)
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

        viewBinding.googleButton.setOnClickListener {
            closeKeyboard()
            viewModel.requestSignInWithGoogle()
        }

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
                            requestNotificationPermission(requestPermissionLauncher)
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

        viewBinding.forgotPasswordTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToForgotPasswordFragment())
        }

        viewBinding.signUpTextView.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.fromSignInFragmentToSignUpFragment())
        }

        // endregion
    }

    @Deprecated("Deprecated in Java")
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
