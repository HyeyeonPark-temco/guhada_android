package io.temco.guhada.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout


class FixScrollingFooterBehavior : AppBarLayout.ScrollingViewBehavior {

    private var appBarLayout: AppBarLayout? = null

    constructor() : super() {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        //return super.onDependentViewChanged(parent, child, dependency)
        if (appBarLayout == null) {
            appBarLayout = dependency as AppBarLayout
        }

        val result = super.onDependentViewChanged(parent, child, dependency)
        val bottomPadding = calculateBottomPadding(appBarLayout!!)
        val paddingChanged = bottomPadding != child.getPaddingBottom()
        if (paddingChanged) {
            child.setPadding(
                    child.getPaddingLeft(),
                    child.getPaddingTop(),
                    child.getPaddingRight(),
                    bottomPadding)
            child.requestLayout()
        }
        return paddingChanged || result
    }

    // Calculate the padding needed to keep the bottom of the view pager's content at the same location on the screen.
    private fun calculateBottomPadding(dependency: AppBarLayout): Int {
        val totalScrollRange = dependency.totalScrollRange
        return totalScrollRange + dependency.top
    }
}