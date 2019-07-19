package io.temco.guhada.view.viewpager

import android.content.Context
import android.util.AttributeSet
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 수정된 CustomViewPager
 */
class CustomViewPager : ViewPager {


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setPagerSmoothScroller() {
        try {
            val localField = ViewPager::class.java.getDeclaredField("mScroller")
            localField.isAccessible = true
            localField.set(this, CustomViewPager.ViewPagerScroller(context))
            return
        } catch (localException: Exception) {
            if (CustomLog.flag) CustomLog.L("error of change scroller ", arrayOf<Any>(localException).toString())
        }

    }

    class ViewPagerScroller(context: Context) : Scroller(context) {

        private val scrollDuration = 850

        override fun startScroll(paramInt1: Int, paramInt2: Int, paramInt3: Int, paramInt4: Int) {
            super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.scrollDuration)
        }

        override fun startScroll(paramInt1: Int, paramInt2: Int, paramInt3: Int, paramInt4: Int, paramInt5: Int) {
            super.startScroll(paramInt1, paramInt2, paramInt3, paramInt4, this.scrollDuration)
        }
    }

}
