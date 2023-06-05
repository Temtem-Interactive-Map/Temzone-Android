package com.temtem.interactive.map.temzone.domain.use_case.data

data class ValidationResult(
    val error: String? = null,
    val successful: Boolean = error == null,
)
