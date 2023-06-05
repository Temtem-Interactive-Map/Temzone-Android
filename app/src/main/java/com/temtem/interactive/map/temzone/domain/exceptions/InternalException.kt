package com.temtem.interactive.map.temzone.domain.exceptions

class InternalException(exception: Exception) : Exception(exception.message.orEmpty())
