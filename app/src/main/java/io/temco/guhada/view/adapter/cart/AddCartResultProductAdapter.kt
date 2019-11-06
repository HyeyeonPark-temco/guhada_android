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
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Deal
import io.temco.guhada.databinding.ItemAddcartproductBinding
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.android.synthetic.main.item_addcartproduct.view.*
import java.lang.Exception

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
    var margin = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_addcartproduct, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position], position)
    }

    inner class Holder(binding: ItemAddcartproductBinding) : BaseViewHolder<ItemAddcartproductBinding>(binding.root) {
        fun bind(deal: Deal, position: Int) {
            try{
                if (width == 0) {
                    if(BaseApplication.getInstance().matrix != null){
                        width = (BaseApplication.getInstance().matrix .widthPixels - CommonViewUtil.dipToPixel(itemView.context, 30)) / 3
                        margin = CommonViewUtil.dipToPixel(itemView.context, 6)
                    }else{
                        val matrix = DisplayMetrics()
                        if(binding.root.context is ContextWrapper && (binding.root.context as ContextWrapper).baseContext is Activity){
                            ((binding.root.context as ContextWrapper).baseContext as Activity).windowManager.defaultDisplay.getMetrics(matrix)
                            width = (matrix.widthPixels - CommonViewUtil.dipToPixel(itemView.context, 30)) / 3
                            margin = CommonViewUtil.dipToPixel(itemView.context, 6)
                            if (CustomLog.flag) CustomLog.L("ProductListAdapter $position", "matrix.widthPixels", matrix.widthPixels, "pwidth", width)
                        }
                    }
                }
                val params2 = LinearLayout.LayoutParams(width, width)
                params2.leftMargin = 0
                params2.rightMargin = margin
                mBinding.relativeImageview.layoutParams = params2
            }catch (e : Exception){
                if(CustomLog.flag)CustomLog.E(e)
            }
            mBinding.linearlayoutAddcartproductContainer.setOnClickListener { mClickItemTask(deal.dealId.toLong()) }
            mBinding.deal = deal
            mBinding.executePendingBindings()
        }

    }
}