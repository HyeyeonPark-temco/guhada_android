package io.temco.guhada.view.adapter.base


import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.common.util.CustomLog

/**
 * @author park jungho
 * 19.07.18
 * 메인 홈에서 사용했던 base recycler adapter
 */
abstract class CommonRecyclerAdapter<T, VH : RecyclerView.ViewHolder>(val items: ArrayList<T>) :RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return setonCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        isFooter(position)
        setOnBindViewHolder(viewHolder, items[position], position)
    }

    abstract fun setonCreateViewHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun setOnBindViewHolder(viewHolder: VH, item: T, position: Int)

    fun addAll(list: ArrayList<T>) {
        try {
            items.clear()
            items.addAll(list)
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }

    }

    abstract fun isFooter(position: Int): Boolean

}