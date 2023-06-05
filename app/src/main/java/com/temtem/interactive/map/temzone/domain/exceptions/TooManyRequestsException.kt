package com.temtem.interactive.map.temzone.domain.exceptions

class TooManyRequestsException(exception: Exception) : Exception(exception.message.orEmpty())
