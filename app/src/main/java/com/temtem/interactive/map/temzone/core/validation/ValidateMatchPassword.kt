package com.temtem.interactive.map.temzone.core.validation

import android.app.Application
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.validation.model.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateMatchPassword @Inject constructor(
    private val application: Application,
) {

    operator fun invoke(password: String, confirmPassword: String): ValidationResult {
        if (confirmPassword.isBlank()) {
            return ValidationResult(application.getString(R.string.confirm_password_empty_error))
        }

        if (password != confirmPassword) {
            return ValidationResult(application.getString(R.string.passwords_do_not_match_error))
        }

        return ValidationResult()
    }
}
