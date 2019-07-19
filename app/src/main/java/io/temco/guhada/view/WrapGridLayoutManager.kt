package io.temco.guhada.view

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 수정된 RecyclerView GridLayoutManager
 */
class WrapGridLayoutManager : GridLayoutManager {

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context,
            attrs,
            defStyleAttr,
            defStyleRes
    ) {
    }

    constructor(context: Context, spanCount: Int) : super(context, spanCount) {}

    constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean) : super(
            context,
            spanCount,
            orientation,
            reverseLayout
    ) {
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        //return super.supportsPredictiveItemAnimations();
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            if (CustomLog.flag) CustomLog.L("Error", "IndexOutOfBoundsException in RecyclerView happens")
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }

    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {
        val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return super.computeScrollVectorForPosition(targetPosition)
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }

            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    companion object {
        private val MILLISECONDS_PER_INCH = 7f
    }

}