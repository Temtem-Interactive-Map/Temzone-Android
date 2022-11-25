package com.temtem.interactive.map.temzone.markers

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView

@SuppressLint("ViewConstructor")
class Marker(context: Context, val x: Double, val y: Double) : AppCompatImageView(context)
