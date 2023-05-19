package com.temtem.interactive.map.temzone.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.databinding.LoginFragmentBinding
import com.temtem.interactive.map.temzone.utils.bindings.viewBindings
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar

class LoginFragment : Fragment(R.layout.login_fragment) {

    private val viewBinding: LoginFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        setLightStatusBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewBinding.loginButton.setOnClickListener {
            val direction = LoginFragmentDirections.fromLoginFragmentToMapFragment()

            findNavController().navigate(direction)
        }
    }

    override fun onResume() {
        super.onResume()

        setLightStatusBar(true)
    }
}
