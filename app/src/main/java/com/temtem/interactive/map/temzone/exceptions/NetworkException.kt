package com.temtem.interactive.map.temzone.exceptions

class NetworkException(exception: Exception) : Exception(exception.message.orEmpty())
