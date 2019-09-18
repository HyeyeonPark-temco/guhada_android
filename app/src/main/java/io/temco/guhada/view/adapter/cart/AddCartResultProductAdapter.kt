package io.temco.guhada.view.adapter.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.databinding.ItemAddcartproductBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 담기 완료 화면의 '이 상품과 함께 많이 구매한 상품' 리스트 adapter
 * @author Hyeyeon Park
 * @since 2019.09.18
 */
class AddCartResultProductAdapter : RecyclerView.Adapter<AddCartResultProductAdapter.Holder>() {
    var mList = mutableListOf<Deal>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_addcartproduct, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemAddcartproductBinding) : BaseViewHolder<ItemAddcartproductBinding>(binding.root) {
        fun bind(deal: Deal) {
            setSpacing()
            mBinding.deal = deal
            mBinding.executePendingBindings()
        }

        private fun setSpacing(){
            val mSpanCount = 3
            val RIGHT_MARGIN = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
            (mBinding.linearlayoutAddcartproductContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                rightMargin = if ((adapterPosition + 1) % mSpanCount == 0) 0
                else RIGHT_MARGIN
            }.let {
                mBinding.linearlayoutAddcartproductContainer.layoutParams = it
            }
        }
    }
}