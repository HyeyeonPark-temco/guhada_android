package io.temco.guhada.view.adapter.cart

import android.app.Activity
import android.content.ContextWrapper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Deal
import io.temco.guhada.databinding.ItemAddcartproductBinding
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_addcartproduct.view.*

/**
 * 장바구니 담기 완료 화면의 '이 상품과 함께 많이 구매한 상품' 리스트 adapter
 * 장바구니 empty view의 실시간 인기상품 adapter
 * @author Hyeyeon Park
 * @since 2019.09.18
 */
class AddCartResultProductAdapter : RecyclerView.Adapter<AddCartResultProductAdapter.Holder>() {
    var mList = mutableListOf<Deal>()
    var mClickItemTask: (dealId: Long) -> Unit = {}

    var width = 0
    var height = 0
    var layoutHeight = 0
    var margin = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_addcartproduct, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position], position)
    }

    inner class Holder(binding: ItemAddcartproductBinding) : BaseViewHolder<ItemAddcartproductBinding>(binding.root) {
        fun bind(deal: Deal, position: Int) {
            if (width == 0) {
                val matrix = DisplayMetrics()
                if ((itemView.context as ContextWrapper).baseContext is Activity) {
                    ((itemView.context as ContextWrapper).baseContext as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                    width = (matrix.widthPixels - CommonViewUtil.dipToPixel(binding.root.context, 30)) / 3
                    height = width
                    margin = CommonViewUtil.dipToPixel(binding.root.context, 5)
                    layoutHeight = height + CommonViewUtil.dipToPixel(binding.root.context, 80)
                }
                if (CustomLog.flag) CustomLog.L("ProductListAdapter $position", "matrix.widthPixels", matrix.widthPixels, "pwidth", width)
            }
            val params = LinearLayout.LayoutParams(width, layoutHeight)
            val params2 = LinearLayout.LayoutParams(width, width)
            if (position % 3 == 0) {
                params.leftMargin = margin
                params.rightMargin = margin
            } else if (position % 3 == 1) {
                params.leftMargin = margin
                params.rightMargin = margin
            } else if (position % 3 == 2) {
                params.leftMargin = margin
                params.rightMargin = margin
            }
            if (CustomLog.flag) CustomLog.L("ProductListAdapter $position", "params.leftMargin", params.leftMargin, "params.rightMargin", params.rightMargin)
            mBinding.linearlayoutAddcartproductContainer.linearlayout_addcartproduct_container.layoutParams = params
            mBinding.linearlayoutAddcartproductContainer.imageview_addcartproduct.layoutParams = params2

//            setSpacing()
            mBinding.linearlayoutAddcartproductContainer.setOnClickListener { mClickItemTask(deal.dealId.toLong()) }
            mBinding.deal = deal
            mBinding.executePendingBindings()
        }

        private fun setSpacing() {
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