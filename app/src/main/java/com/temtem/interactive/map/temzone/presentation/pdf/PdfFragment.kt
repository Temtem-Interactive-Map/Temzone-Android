package com.temtem.interactive.map.temzone.presentation.pdf

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.binding.viewBindings
import com.temtem.interactive.map.temzone.core.extension.setLightStatusBar
import com.temtem.interactive.map.temzone.databinding.PdfFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PdfFragment : Fragment(R.layout.pdf_fragment) {

    companion object {
        const val PRIVACY_POLICY = "privacy_policy.pdf"
        const val TERMS_OF_SERVICE = "terms_of_service.pdf"
    }

    private val arguments: PdfFragmentArgs by navArgs()
    private val viewBinding: PdfFragmentBinding by viewBindings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        requireActivity().setLightStatusBar(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // region PDF

        viewBinding.toolbar.title = arguments.title
        viewBinding.pdfView.fromAsset(arguments.filename).load()

        // endregion

        // region Navigation

        viewBinding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // endregion
    }

    override fun onResume() {
        super.onResume()

        requireActivity().setLightStatusBar(true)
    }
}
