package io.temco.guhada.view.viewpager

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

/**
 * @author park jungho
 * 19.07.18
 * 수정된 CustomViewPagerAdapter
 */
abstract class CustomViewPagerAdapter<T>(var context: Context,
                                         var titleList: Array<String>,
                                         var items: Array<T>) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var v = setViewLayout(container, getItem(position), position)
        container.addView(v)
        return v
    }

    abstract fun setViewLayout(container: ViewGroup, item: T, position: Int): View

    override fun isViewFromObject(view: View, ob: Any): Boolean {
        return view === ob
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        //super.destroyItem(container, position, obj)
        container.removeView(obj as View)
    }

    fun getItem(positon: Int): T {
        return items[positon]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}