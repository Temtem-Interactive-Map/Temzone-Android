package com.temtem.interactive.map.temzone.domain.exceptions

class EmailCollisionException(exception: Exception) : Exception(exception.message.orEmpty())
