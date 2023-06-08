package com.temtem.interactive.map.temzone.core.validation

import android.app.Application
import com.temtem.interactive.map.temzone.R
import com.temtem.interactive.map.temzone.core.validation.model.ValidationResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateNotEmpty @Inject constructor(
    private val application: Application,
) {

    operator fun invoke(field: String): ValidationResult {
        if (field.isBlank()) {
            return ValidationResult(application.getString(R.string.email_empty_error))
        }

        return ValidationResult()
    }
}
