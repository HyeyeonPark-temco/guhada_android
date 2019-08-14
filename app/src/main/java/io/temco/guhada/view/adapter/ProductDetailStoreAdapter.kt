package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.Deal
import io.temco.guhada.databinding.ItemProductdetailStoreBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailStoreAdapter : RecyclerView.Adapter<ProductDetailStoreAdapter.Holder>() {
    private val RIGHT_MARGIN = 20
    private val LAST_RIGHT_MARGIN = 40

    var mList: List<Deal> = arrayListOf()
    var mIsGridLayout = false
    var mSpanCount = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_store, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: List<Deal>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemProductdetailStoreBinding) : BaseViewHolder<ItemProductdetailStoreBinding>(binding.root) {
        fun bind(deal: Deal) {
            setSpacing()
            mBinding.deal = deal
            mBinding.executePendingBindings()
        }

        private fun setSpacing() {
            if (mIsGridLayout) {
                (mBinding.constraintlayoutProductdetailStore.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    rightMargin = if ((adapterPosition + 1) % mSpanCount == 0) 0
                    else RIGHT_MARGIN
                }.let {
                    mBinding.constraintlayoutProductdetailStore.layoutParams = it
                }
            } else {
                (mBinding.constraintlayoutProductdetailStore.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    rightMargin = if (adapterPosition < mList.size - 1) RIGHT_MARGIN
                    else LAST_RIGHT_MARGIN
                }.let {
                    mBinding.constraintlayoutProductdetailStore.layoutParams = it
                }
            }
        }
    }
}