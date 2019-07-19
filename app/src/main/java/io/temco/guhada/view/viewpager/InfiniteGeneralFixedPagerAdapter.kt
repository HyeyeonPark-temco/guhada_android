package io.temco.guhada.view.viewpager

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import io.temco.guhada.common.util.CustomLog
import kr.co.pjsoft87.androidcommon.ktx.view.RecycleUtils

/**
 * @author park jungho
 * 19.07.18
 * 무한 스크롤 되는 viewpager adapter
 */
abstract class InfiniteGeneralFixedPagerAdapter<T> : PagerAdapter {
    protected var list: ArrayList<T>
    protected var isDestroyView: Boolean = false
    var isInfinity: Boolean = false
    protected var viewGroup: ViewGroup? = null

    val realCount: Int
        get() = list.size

    constructor(list: ArrayList<T>) {
        this.list = list
        this.isInfinity = true
        this.isDestroyView = true
    }

    constructor(list: ArrayList<T>, isInfinity: Boolean) : super() {
        this.list = list
        this.isInfinity = isInfinity
        this.isDestroyView = true
    }

    constructor(list: ArrayList<T>, isInfinity: Boolean, isDestroyView: Boolean) : super() {
        this.list = list
        this.isInfinity = isInfinity
        this.isDestroyView = isDestroyView
    }

    override fun getCount(): Int {
        return if (isInfinity) {
            if (this.realCount > 1) realCount * 200 else this.realCount
        } else {
            realCount
            //this.count
        }
    }

    abstract fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: T): View

    fun getItem(paramViewGroup: ViewGroup, paramInt: Int): View {
        return getPageView(paramViewGroup, paramInt, list[paramInt])
    }


    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
        //return false;
    }

    override fun instantiateItem(paramViewGroup: ViewGroup, paramInt: Int): Any {
        if (viewGroup == null) viewGroup = paramViewGroup
        if (isInfinity) {
            val i = paramInt % realCount
            if (CustomLog.flag) CustomLog.L(
                "InfiniteGeneralFixedPagerAdapter",
                "1 instantiateItem getRealCount $realCount"
            )
            if (CustomLog.flag) CustomLog.L("InfiniteGeneralFixedPagerAdapter", "1 instantiateItem paramInt $paramInt")
            if (CustomLog.flag) CustomLog.L("InfiniteGeneralFixedPagerAdapter", "1 instantiateItem i $i")
            val localBaseFragment = this.getItem(paramViewGroup, i)
            paramViewGroup.addView(localBaseFragment)
            return localBaseFragment
        } else {
            val localBaseFragment = this.getItem(paramViewGroup, paramInt)
            paramViewGroup.addView(localBaseFragment)
            return localBaseFragment
        }
    }

    override fun destroyItem(paramViewGroup: ViewGroup, paramInt: Int, paramObject: Any) {
        if (isDestroyView) {
            if (isInfinity) {
                val i = paramInt % realCount
                if (CustomLog.flag) CustomLog.L("InfiniteGeneralFixedPagerAdapter", "destroyItem paramInt $paramInt")
                if (CustomLog.flag) CustomLog.L("InfiniteGeneralFixedPagerAdapter", "destroyItem i $i")
                paramViewGroup.removeView(paramObject as View)
                //this.destroyItem(paramViewGroup, i, paramObject);
            } else {
                paramViewGroup.removeView(paramObject as View)
                //this.destroyItem(paramViewGroup, paramInt, paramObject);
            }
        }
    }

    fun recycle() {
        try {
            if (viewGroup != null) RecycleUtils.recursiveRecycle(viewGroup)
            try {
                System.gc()
            } catch (e2: Exception) {
                if (CustomLog.flag) CustomLog.E(e2)
            }

        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }

    }


    abstract override fun getPageTitle(position: Int): CharSequence?
    abstract fun getPagerIcon(position: Int): Int
    abstract fun getPagerIconBackground(position: Int): Int


}