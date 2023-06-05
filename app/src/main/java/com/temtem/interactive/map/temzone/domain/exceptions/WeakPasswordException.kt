package com.temtem.interactive.map.temzone.domain.exceptions

class WeakPasswordException(exception: Exception) : Exception(exception.message.orEmpty())
