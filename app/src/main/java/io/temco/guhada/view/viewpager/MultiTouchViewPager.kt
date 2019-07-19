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
 * 무한 스크롤 되는 InfiniteViewPager 의 Base
 */
open class MultiTouchViewPager : ViewPager {
    var isAllowSwiping = true

    constructor(paramContext: Context) : super(paramContext) {}

    constructor(paramContext: Context, paramAttributeSet: AttributeSet) : super(paramContext, paramAttributeSet) {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            if (CustomLog.flag) CustomLog.L("MultiTouchViewPager onInterceptTouchEvent", "allowSwiping $isAllowSwiping")
            val bool1 = this.isAllowSwiping
            var bool2 = false
            if (bool1) {
                val bool3 = super.onInterceptTouchEvent(ev)
                bool2 = bool3
            }
            if (CustomLog.flag) CustomLog.L("MultiTouchViewPager onInterceptTouchEvent", "bool2 $bool2")
            return bool2
        } catch (localIllegalArgumentException: IllegalArgumentException) {
            val str = javaClass.simpleName
            val arrayOfObject = arrayOfNulls<Any>(1)
            arrayOfObject[0] = localIllegalArgumentException.message
            if (CustomLog.flag) CustomLog.L(str, arrayOfObject.toString())
        }

        return false
    }

    override fun onTouchEvent(paramMotionEvent: MotionEvent): Boolean {
        try {
            val bool1 = this.isAllowSwiping
            if (CustomLog.flag) CustomLog.L("MultiTouchViewPager onTouchEvent", "allowSwiping $isAllowSwiping")
            var bool2 = false
            if (bool1) {
                val bool3 = super.onTouchEvent(paramMotionEvent)
                bool2 = bool3
            }
            if (CustomLog.flag) CustomLog.L("MultiTouchViewPager onInterceptTouchEvent", "bool2 $bool2")
            return bool2
        } catch (localIllegalArgumentException: IllegalArgumentException) {
            val str = javaClass.simpleName
            val arrayOfObject = arrayOfNulls<Any>(1)
            arrayOfObject[0] = localIllegalArgumentException.message
            if (CustomLog.flag) CustomLog.L(str, arrayOfObject.toString())
        }

        return false
    }

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

    private inner class ViewPagerScroller : Scroller {

        private val scrollDuration = 600

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