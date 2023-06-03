package com.temtem.interactive.map.temzone.utils.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.setErrorAndRequestFocus(errorMessage: String?) {
    error = errorMessage

    errorMessage?.let {
        requestFocus()
    }
}
