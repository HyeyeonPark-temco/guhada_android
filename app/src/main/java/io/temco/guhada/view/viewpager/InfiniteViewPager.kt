package io.temco.guhada.view.viewpager

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 무한 스크롤 되는 InfiniteViewPager
 * 주로 ViewPager안에 ViewPager가 들어가 상위 ViewPager의 좌우 제스쳐를 막아서 내부로 사용
 */
class InfiniteViewPager : MultiTouchViewPager {
    private var realCount: Int = 0
    var isInfinity: Boolean = false

    val offsetAmount: Int
        get() {
            if (isInfinity) {
               if (adapter is InfiniteGeneralFixedPagerAdapter<*>) {
                    this.realCount = (adapter as InfiniteGeneralFixedPagerAdapter<*>).realCount
                    return 100 * this.realCount
                } else
                    return adapter!!.count
            } else {
                return adapter!!.count
            }
        }

    val realCurrentItem: Int
        get() {
            if (isInfinity) {
                var i = getCurrentItem()
                if (adapter != null && adapter is InfiniteGeneralFixedPagerAdapter<*>) {
                    i %= (adapter as InfiniteGeneralFixedPagerAdapter<*>).realCount
                    return i
                } else {
                    return i
                }
            } else {
                return getCurrentItem()
            }
        }

    constructor(paramContext: Context) : super(paramContext) {
        isInfinity = true
    }

    constructor(paramContext: Context, paramAttributeSet: AttributeSet) : super(paramContext, paramAttributeSet) {
        isInfinity = true
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        setCurrentItem(0)
    }


    override fun setCurrentItem(paramInt: Int) {
        if (CustomLog.flag) CustomLog.L("InfiniteViewPager", "1 setCurrentItem paramInt $paramInt")
        if (isInfinity) {
            if (adapter!!.count === 0) {
                super.setCurrentItem(paramInt, false)
                return
            }
            val item = offsetAmount + paramInt % adapter!!.count
            super.setCurrentItem(item, false)
        } else {
            super.setCurrentItem(paramInt, false)
        }
    }


    interface OnTabClickListener {
        fun onClick()
    }


}
