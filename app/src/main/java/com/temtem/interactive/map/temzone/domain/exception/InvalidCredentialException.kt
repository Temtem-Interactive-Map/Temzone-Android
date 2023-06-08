package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class InvalidCredentialException(context: Context) :
    Exception(context.getString(R.string.credential_error))
