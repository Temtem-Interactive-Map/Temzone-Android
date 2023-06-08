package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class TooManyRequestsException(context: Context) :
    Exception(context.getString(R.string.too_many_requests_error))
