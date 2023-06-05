package com.temtem.interactive.map.temzone.domain.exceptions

class NetworkException(exception: Exception) : Exception(exception.message.orEmpty())
