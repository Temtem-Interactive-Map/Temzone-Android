package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class UnknownException(context: Context) : Exception(context.getString(R.string.unknown_error))
