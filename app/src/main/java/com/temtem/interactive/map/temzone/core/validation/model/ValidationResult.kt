package com.temtem.interactive.map.temzone.core.validation.model

data class ValidationResult(
    val message: String? = null,
    val successful: Boolean = message == null,
)
