package com.temtem.interactive.map.temzone.utils.behaviors

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.temtem.interactive.map.temzone.utils.extensions.dpToPx
import com.temtem.interactive.map.temzone.utils.extensions.setLightStatusBar

class BottomSheetAppBarLayoutBehavior(
    private val context: Context,
    attrs: AttributeSet,
) : AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private companion object {
        private const val APP_BAR_SLIDE_OFFSET_THRESHOLD = 0.95
    }

    private val appBarLayoutOffset = context.dpToPx(8)
    private val animationDuration =
        context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

    private var appBarLayoutVisible = false
    private var appBarLayoutStartY: Float? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        if (appBarLayoutStartY == null) {
            appBarLayoutStartY = child.y
            child.y = appBarLayoutStartY!! - appBarLayoutOffset
            child.visibility = View.GONE
        }

        val layoutParams = dependency.layoutParams as CoordinatorLayout.LayoutParams
        val bottomSheetBehavior = layoutParams.behavior

        return bottomSheetBehavior is BottomSheetBehavior<*>
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        val layoutParams = dependency.layoutParams as CoordinatorLayout.LayoutParams
        val bottomSheetBehavior = layoutParams.behavior as BottomSheetBehavior<*>
        val slideOffset = bottomSheetBehavior.calculateSlideOffset()

        if (slideOffset >= APP_BAR_SLIDE_OFFSET_THRESHOLD) {
            if (!appBarLayoutVisible) {
                appBarLayoutVisible = true

                child.animate().apply {
                    alpha(1f)
                    y(appBarLayoutStartY!!)
                    duration = animationDuration
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            child.visibility = View.VISIBLE

                            val contextWrapper = context as ContextWrapper
                            val activity = contextWrapper.baseContext as Activity

                            activity.setLightStatusBar(true)
                        }
                    })
                    start()
                }

                return true
            }
        } else if (appBarLayoutVisible) {
            appBarLayoutVisible = false

            child.animate().apply {
                alpha(0f)
                y(appBarLayoutStartY!! - appBarLayoutOffset)
                duration = animationDuration
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        val contextWrapper = context as ContextWrapper
                        val activity = contextWrapper.baseContext as Activity

                        activity.setLightStatusBar(false)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        child.visibility = View.GONE
                    }
                })
                start()
            }

            return true
        }

        return false
    }
}
