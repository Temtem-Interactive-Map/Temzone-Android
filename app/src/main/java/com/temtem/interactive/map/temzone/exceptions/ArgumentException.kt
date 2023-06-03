package com.temtem.interactive.map.temzone.exceptions

class ArgumentException(exception: Exception) : Exception(exception.message.orEmpty())
