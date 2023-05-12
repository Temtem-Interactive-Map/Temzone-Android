package com.temtem.interactive.map.temzone.utils.behaviors

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.temtem.interactive.map.temzone.R

class SearchAppBarLayoutBehavior(context: Context, attrs: AttributeSet) :
    AppBarLayout.ScrollingViewBehavior(context, attrs) {

    private companion object {
        private const val APP_BAR_SLIDE_OFFSET_THRESHOLD = 0.6
    }

    private val animationDuration =
        context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    private val appBarLayoutOffset =
        context.resources.getDimension(R.dimen.search_app_bar_layout_offset)

    private var appBarLayoutVisible = false
    private var appBarLayoutStartY: Float? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        if (appBarLayoutStartY == null) {
            appBarLayoutStartY = child.y
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
                    y(appBarLayoutStartY!! - appBarLayoutOffset)
                    duration = animationDuration
                    setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            val floatingActionButton =
                                child.findViewById<FloatingActionButton>(R.id.map_layers_button)

                            floatingActionButton.animate().apply {
                                alpha(0f)
                                duration = animationDuration
                                setListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationStart(animation: Animator) {
                                        floatingActionButton.isClickable = false
                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                        floatingActionButton.visibility = View.GONE
                                    }
                                })
                                start()
                            }
                        }
                    })
                    start()
                }

                return true
            }
        } else if (appBarLayoutVisible) {
            appBarLayoutVisible = false
            println("UPDATE")

            child.animate().apply {
                y(appBarLayoutStartY!!)
                duration = animationDuration
                start()
            }

            return true
        }

        return false
    }
}
