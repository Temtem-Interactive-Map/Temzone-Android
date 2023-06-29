package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class WeakPasswordException(context: Context) :
    Exception(context.getString(R.string.password_length_error))
