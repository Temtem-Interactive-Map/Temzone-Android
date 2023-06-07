package com.temtem.interactive.map.temzone.domain.exception

import android.app.Application
import com.temtem.interactive.map.temzone.R

class WeakPasswordException(application: Application) :
    Exception(application.getString(R.string.password_length_error))
