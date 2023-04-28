package com.temtem.interactive.map.temzone.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.activity.viewBinding
import androidx.appcompat.app.AppCompatActivity
import com.temtem.interactive.map.temzone.databinding.MainActivityBinding

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val binding: MainActivityBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }

        setContentView(binding.root)
    }
}
