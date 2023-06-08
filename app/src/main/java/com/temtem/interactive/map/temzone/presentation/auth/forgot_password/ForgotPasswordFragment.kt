package com.temtem.interactive.map.temzone.presentation.auth.forgot_password

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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.closeKeyboard
import com.temtem.interactive.map.temzone.core.extension.setErrorAndRequestFocus
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.ForgotPasswordFragmentBinding
import com.temtem.interactive.map.temzone.presentation.auth.forgot_password.state.ForgotPasswordFormState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.forgot_password_fragment) {

    private val viewModel: ForgotPasswordViewModel by viewModels()
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
        // region Send email to reset password

        // Add the send email button click listener
        viewBinding.sendEmailButton.setOnClickListener {
            val email = viewBinding.emailEditText.text.toString().trim()

            closeKeyboard()
            viewModel.sendPasswordResetEmail(email)
        }

        // Observe the forgot password form state
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.forgotPasswordFormState.collect {
                    when (it) {
                        is ForgotPasswordFormState.Empty -> Unit

                        is ForgotPasswordFormState.Loading -> {
                            viewBinding.sendEmailButton.isEnabled = false
                        }

                        is ForgotPasswordFormState.Success -> {
                            viewBinding.sendEmailButton.isEnabled = true
                            viewBinding.emailTextInputLayout.setErrorAndRequestFocus(null)
                            viewBinding.emailEditText.text = null

                            Snackbar.make(
                                viewBinding.root,
                                R.string.password_recovery_message,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        is ForgotPasswordFormState.Error -> {
                            viewBinding.sendEmailButton.isEnabled = true
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

        // region Navigation

        // Navigate to the sign in fragment
        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.fromForgotPasswordFragmentToSignInFragment())
        }

        // Navigate to the sign up fragment
        viewBinding.signUpTextView.setOnClickListener {
            findNavController().navigate(ForgotPasswordFragmentDirections.fromForgotPasswordFragmentToSignUpFragment())
        }

        // Override the default back button behavior
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
            requireActivity(),
            onBackPressedCallback,
        )

        // endregion
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(false)
    }
}
