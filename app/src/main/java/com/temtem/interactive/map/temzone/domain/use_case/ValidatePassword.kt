package com.temtem.interactive.map.temzone.domain.use_case

import android.app.Application
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.use_case.data.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidatePassword @Inject constructor(
    private val application: Application,
) {

    fun execute(password: String): ValidationResult {
        if (password.isBlank()) {
            return ValidationResult(application.getString(R.string.password_empty_error))
        }

        return ValidationResult()
    }
}
