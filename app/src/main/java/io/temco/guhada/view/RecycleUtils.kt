package kr.co.pjsoft87.androidcommon.ktx.view

import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * InfiniteGeneralFixedPagerAdapter 에서 사용한 view를 초기화 해주는 util
 */
object RecycleUtils {

    fun recursiveRecycle(root: View?) {
        var root : View? = root ?: return
        try {
            if (root?.getBackground() != null) root.setBackgroundResource(0)
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }

        if (root is ViewGroup) {
            val group = root as ViewGroup
            val count = group.childCount
            for (i in 0 until count) {
                recursiveRecycle(group.getChildAt(i))
            }

            if (root !is AdapterView<*>) {
                try {
                    if (root is SwipeRefreshLayout) {
                        group.alpha = 0f
                        group.setOnDragListener(null)
                        group.removeAllViews()
                    } else {
                        group.removeAllViews()
                    }
                } catch (e: Exception) {
                }

                if (CustomLog.flag) CustomLog.L("RecycleUtils", "recursiveRecycle ViewGroup removeAllViews")
            }

        }
        try {
            if (root is ImageView) {
                if ((root as ImageView).getDrawable() != null) (root as ImageView).setImageDrawable(null)
                if (CustomLog.flag) CustomLog.L("RecycleUtils", "recursiveRecycle ImageView")
            }
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }

        root = null
        return
    }


    fun recursiveRecycle(root: Array<ImageView?>?) {
        if (root == null) return
        if (CustomLog.flag) CustomLog.L("RecycleUtils", "recursiveRecycle [] not null")
        for (i in root.indices) {
            if (root[i] is ImageView) {
                if ((root[i] as ImageView).getDrawable() != null) (root[i] as ImageView).setImageDrawable(null)
            }
            root[i] = null
        }
        return
    }
}