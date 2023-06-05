package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class EmailFormatException(context: Context) :
    Exception(context.getString(R.string.email_format_error))
