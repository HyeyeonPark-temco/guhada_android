package io.temco.guhada.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.viewmodel.productdetail.ProductDetailStoreViewModel
import io.temco.guhada.databinding.ItemProductdetailStoregridBinding
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세- 셀러스토어, 연관상품, 추천상품 Adapter
 * @author Hyeyeon Park
 * @since 2019.08.13
 */
class ProductDetailStoreGridAdapter : RecyclerView.Adapter<ProductDetailStoreGridAdapter.Holder>() {
    private val RIGHT_MARGIN = 20
    private val LAST_RIGHT_MARGIN = 40

    lateinit var mViewModel: ProductDetailStoreViewModel
    var mList: List<Deal> = arrayListOf()
    var mIsGridLayout = false
    var mSpanCount = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_storegrid, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: List<Deal>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemProductdetailStoregridBinding) : BaseViewHolder<ItemProductdetailStoregridBinding>(binding.root) {
        fun bind(deal: Deal) {
            setSpacing()
            mBinding.deal = deal
            mBinding.constraintlayoutProductdetailStore.setOnClickListener {
                val dealId = deal.dealId.toLong()
                this@ProductDetailStoreGridAdapter.mViewModel.getDetail(dealId = dealId, redirectProductDetailActivity = { this.redirectProductDetailActivity(dealId) })
            }
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

        private fun redirectProductDetailActivity(dealId: Long) {
            val intent = Intent(binding.root.context, ProductFragmentDetailActivity::class.java)
            intent.putExtra("dealId", dealId)
            binding.root.context.startActivity(intent)
            (binding.root.context as Activity).finish()
        }
    }
}