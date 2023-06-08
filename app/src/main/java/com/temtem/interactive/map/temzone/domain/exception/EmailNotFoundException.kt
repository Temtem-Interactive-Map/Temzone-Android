package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class EmailNotFoundException(context: Context) :
    Exception(context.getString(R.string.email_not_found_error))
