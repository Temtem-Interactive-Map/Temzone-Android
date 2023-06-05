package com.temtem.interactive.map.temzone.domain.use_case

import android.app.Application
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.domain.use_case.data.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateEmail @Inject constructor(
    private val application: Application,
) {

    fun execute(email: String): ValidationResult {
        if (email.isBlank()) {
            return ValidationResult(application.getString(R.string.email_empty_error))
        }

        return ValidationResult()
    }
}
