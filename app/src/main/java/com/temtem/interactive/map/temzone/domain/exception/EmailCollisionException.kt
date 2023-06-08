package com.temtem.interactive.map.temzone.domain.exception

import android.content.Context
import com.temtem.interactive.map.temzone.R

class EmailCollisionException(context: Context) :
    Exception(context.getString(R.string.email_collision_error))
