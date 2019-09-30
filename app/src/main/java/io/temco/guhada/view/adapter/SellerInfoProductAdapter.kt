package io.temco.guhada.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.viewmodel.SellerInfoViewModel
import io.temco.guhada.databinding.ItemSellerinfoProductBinding
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 상품 상세- 셀러스토어, 연관상품, 추천상품 Adapter
 * @author Hyeyeon Park
 * @since 2019.08.13
 */
class SellerInfoProductAdapter : RecyclerView.Adapter<SellerInfoProductAdapter.Holder>() {
    private val RIGHT_MARGIN = 20

    lateinit var mViewModel: SellerInfoViewModel
    var mList: MutableList<Deal> = mutableListOf()
    var mSpanCount = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sellerinfo_product, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    fun setItems(list: MutableList<Deal>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemSellerinfoProductBinding) : BaseViewHolder<ItemSellerinfoProductBinding>(binding.root) {
        fun bind(deal: Deal) {
            setSpacing()
            mBinding.deal = deal
            mBinding.constraintlayoutSellerinfoStore.setOnClickListener {
                val dealId = deal.dealId.toLong()
                this@SellerInfoProductAdapter.mViewModel.getDetail(dealId = dealId, redirectProductDetailActivity = { this.redirectProductDetailActivity(dealId) })
            }
            mBinding.executePendingBindings()
        }


        private fun setSpacing() {
            val mSpanCount = 2
            val RIGHT_MARGIN = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
            (mBinding.constraintlayoutSellerinfoStore.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if ((adapterPosition + 1) % mSpanCount == 0) 0
                else RIGHT_MARGIN
            }.let {
                mBinding.constraintlayoutSellerinfoStore.layoutParams = it
            }
        }
       /* private fun setSpacing() {
            (mBinding.constraintlayoutSellerinfoStore.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if ((adapterPosition + 1) % mSpanCount == 0) 0
                else RIGHT_MARGIN
            }.let {
                mBinding.constraintlayoutSellerinfoStore.layoutParams = it
            }
        }*/

        private fun redirectProductDetailActivity(dealId: Long) {
            val intent = Intent(binding.root.context, ProductFragmentDetailActivity::class.java)
            intent.putExtra("dealId", dealId)
            binding.root.context.startActivity(intent)
        }
    }
}