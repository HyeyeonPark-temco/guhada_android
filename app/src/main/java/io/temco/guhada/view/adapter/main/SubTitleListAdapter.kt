package io.temco.guhada.view.adapter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.databinding.ItemMainhomeSubtitleBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class SubTitleListAdapter : RecyclerView.Adapter<SubTitleListAdapter.Holder>() {
    var mList: MutableList<CategoryEntity> = arrayListOf()

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mainhome_subtitle, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<CategoryEntity>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemMainhomeSubtitleBinding) : BaseViewHolder<ItemMainhomeSubtitleBinding>(binding.root) {
        fun bind(data: CategoryEntity, position: Int) {
            mBinding.title = data.title
            itemView.setOnClickListener {
                mClickSelectItemListener?.clickSelectItemListener(0,position,data)
            }
            mBinding.executePendingBindings()
        }

    }
}