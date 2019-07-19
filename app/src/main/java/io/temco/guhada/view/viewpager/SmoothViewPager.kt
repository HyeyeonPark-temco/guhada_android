package io.temco.guhada.view.viewpager

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import io.temco.guhada.common.util.CustomLog


/**
 * @author park jungho
 * 19.07.18
 * ViewPager 의 스크롤 속도 조절
 */
class SmoothViewPager : ViewPager {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setPagerSmoothScroller() {
        try {
            val localField = ViewPager::class.java.getDeclaredField("mScroller")
            localField.isAccessible = true
            localField.set(this, ViewPagerScroller(context))
            return
        } catch (localException: Exception) {
            if (CustomLog.flag) CustomLog.L("error of change scroller ", arrayOf<Any>(localException).toString())
        }

    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (CustomLog.flag) CustomLog.L(
            "MultiTouchViewPager",
            "SmoothViewPager onInterceptTouchEvent " + (visibility == ViewPager.VISIBLE)
        )
        //return super.onInterceptTouchEvent(ev);
        return visibility == ViewPager.VISIBLE
    }

    private inner class ViewPagerScroller : Scroller {

        private val scrollDuration = 850

        constructor(context: Context) : super(context) {}

        constructor(context: Context, interpolator: Interpolator) : super(context, interpolator) {}

        override fun startScroll(paramInt1: Int, paramInt2: Int, paramInt3: Int, paramInt4: Int) {
            super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.scrollDuration)
        }

        override fun startScroll(paramInt1: Int, paramInt2: Int, paramInt3: Int, paramInt4: Int, paramInt5: Int) {
            super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.scrollDuration)
        }
    }

}