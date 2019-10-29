package io.temco.guhada.view.adapter.cart

import android.app.Activity
import android.content.ContextWrapper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
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
 * @author Hyeyeon Park
 * @since 2019.09.18
 */
class AddCartResultProductAdapter : RecyclerView.Adapter<AddCartResultProductAdapter.Holder>() {
    var mList = mutableListOf<Deal>()
    var mClickItemTask: (dealId: Long) -> Unit = {}

    var widthParent = 0
    var width = 0
    var margin = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_addcartproduct, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position], position)
    }

    inner class Holder(binding: ItemAddcartproductBinding) : BaseViewHolder<ItemAddcartproductBinding>(binding.root) {
        fun bind(deal: Deal, position : Int) {
            if (width == 0) {
                val matrix = DisplayMetrics()
                (itemView.context as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                widthParent = matrix.widthPixels / 3
                width = (matrix.widthPixels - CommonViewUtil.dipToPixel(itemView.context, 30)) / 3
                margin = CommonViewUtil.dipToPixel(itemView.context, 6)
            }
            //val params = LinearLayout.LayoutParams(widthParent, LinearLayout.LayoutParams.WRAP_CONTENT)
            val params2 = LinearLayout.LayoutParams(width, width)
            params2.leftMargin = 0
            params2.rightMargin = margin
            /*if (position == 0) {
            } else {
                if (position % 3 == 0) {
                    params2.leftMargin = 0
                    params2.rightMargin = margin
                } else if (position % 3 == 1) {
                    params2.leftMargin = 0
                    params2.rightMargin = margin
                } else if (position % 3 == 2) {
                    params2.leftMargin = 0
                    params2.rightMargin = margin
                }
            }*/
            mBinding.relativeImageview.layoutParams = params2
            /*if (CustomLog.flag) CustomLog.L("ProductListAdapter $position", "params.leftMargin", params.leftMargin, "params.rightMargin", params.rightMargin)
            mBinding.linearlayoutAddcartproductContainer.layoutParams = params*/
           /* mBinding.linearlayoutAddcartproductContainer.layoutParams = params
            if (position == 0) {
                if (CustomLog.flag) CustomLog.L("1 ProductListAdapter $position", "params",margin)
                mBinding.linearlayoutAddcartproductContainer.setPadding(margin,0,margin,margin*2)
            } else {
                if (position % 3 == 0) {
                    if (CustomLog.flag) CustomLog.L("2 ProductListAdapter $position", "params",margin)
                    mBinding.linearlayoutAddcartproductContainer.setPadding(margin,0,margin,margin*2)
                } else if (position % 3 == 1) {
                    if (CustomLog.flag) CustomLog.L("3 ProductListAdapter $position", "params",margin)
                    mBinding.linearlayoutAddcartproductContainer.setPadding(0,0,0,margin*2)
                } else if (position % 3 == 2) {
                    if (CustomLog.flag) CustomLog.L("4 ProductListAdapter $position", "params",margin)
                    mBinding.linearlayoutAddcartproductContainer.setPadding(margin,0,margin,margin*2)
                }
            }*/

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