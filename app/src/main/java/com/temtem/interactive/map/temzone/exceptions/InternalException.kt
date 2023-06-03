package com.temtem.interactive.map.temzone.exceptions

class InternalException(exception: Exception) : Exception(exception.message.orEmpty())
