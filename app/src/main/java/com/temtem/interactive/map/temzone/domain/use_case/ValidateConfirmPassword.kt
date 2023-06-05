package com.temtem.interactive.map.temzone.domain.use_case

import android.app.Application
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.use_case.data.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateConfirmPassword @Inject constructor(
    private val application: Application,
) {

    fun execute(password: String, confirmPassword: String): ValidationResult {
        if (confirmPassword.isBlank()) {
            return ValidationResult(application.getString(R.string.confirm_password_empty_error))
        }

        if (password != confirmPassword) {
            return ValidationResult(application.getString(R.string.passwords_do_not_match_error))
        }

        return ValidationResult()
    }
}
