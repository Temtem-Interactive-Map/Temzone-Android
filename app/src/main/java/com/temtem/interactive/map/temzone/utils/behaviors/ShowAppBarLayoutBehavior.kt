package com.temtem.interactive.map.temzone.utils.behaviors

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.temtem.interactive.map.temzone.R

class ShowAppBarLayoutBehavior(context: Context, attrs: AttributeSet) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private companion object {
        private const val SLIDE_OFFSET_THRESHOLD = 0.95
    }

    private val animationDuration =
        context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private val childOffset = context.resources.getDimension(R.dimen.show_app_bar_layout_offset)

    private var childVisible = false
    private var childStartY: Float? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        if (childStartY == null) {
            childStartY = child.y
            child.y = childStartY!! - childOffset
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

        if (!childVisible && slideOffset >= SLIDE_OFFSET_THRESHOLD) {
            childVisible = true

            child.animate().apply {
                alpha(1f)
                translationY(childStartY!!)
                duration = animationDuration
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        child.visibility = View.VISIBLE
                    }
                })
                start()
            }

            return true
        }

        if (childVisible && slideOffset < SLIDE_OFFSET_THRESHOLD) {
            childVisible = false

            child.animate().apply {
                alpha(0f)
                translationY(childStartY!! - childOffset)
                duration = animationDuration
                setListener(object : AnimatorListenerAdapter() {
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
