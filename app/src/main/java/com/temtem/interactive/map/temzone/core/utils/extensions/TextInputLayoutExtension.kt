package com.temtem.interactive.map.temzone.core.utils.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.setErrorAndRequestFocus(message: String?) {
    error = message

    message?.let {
        requestFocus()
    }
}
