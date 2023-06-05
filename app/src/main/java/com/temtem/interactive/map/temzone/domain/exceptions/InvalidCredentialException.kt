package com.temtem.interactive.map.temzone.domain.exceptions

class InvalidCredentialException(exception: Exception) : Exception(exception.message.orEmpty())
