package com.temtem.interactive.map.temzone.utils.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.temtem.interactive.map.temzone.R

class HideAppBarLayoutBehavior(context: Context, attrs: AttributeSet) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private companion object {
        private const val SLIDE_OFFSET_THRESHOLD = 0.6
    }

    private val animationDuration =
        context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private val childOffset = context.resources.getDimension(R.dimen.hide_app_bar_layout_offset)

    private var childVisible = false
    private var childStartY: Float? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        if (childStartY == null) {
            childStartY = child.y
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
                translationY(childStartY!! - childOffset)
                duration = animationDuration
                start()
            }

            return true
        }

        if (childVisible && slideOffset < SLIDE_OFFSET_THRESHOLD) {
            childVisible = false

            child.animate().apply {
                translationY(childStartY!!)
                duration = animationDuration
                start()
            }

            return true
        }

        return false
    }
}
