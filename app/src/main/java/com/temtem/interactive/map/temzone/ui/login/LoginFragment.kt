package com.temtem.interactive.map.temzone.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.LoginFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBinding

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val binding: LoginFragmentBinding by viewBinding()
    private val loginButton by lazy { binding.loginButton }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val direction = LoginFragmentDirections.fromLoginFragmentToMapFragment()

        findNavController().navigate(direction)

        loginButton.setOnClickListener {
            val direction = LoginFragmentDirections.fromLoginFragmentToMapFragment()

            findNavController().navigate(direction)
        }
    }
}
