package io.temco.guhada.view.adapter.productdetail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.databinding.ItemProductdetailOptionspinnerBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 옵션 하단 팝업 recyclerView adapter
 * @author Hyeyeon Park
 * @since 2019.09.06
 */
class ProductDetailOptionListAdapter : RecyclerView.Adapter<ProductDetailOptionListAdapter.Holder>() {
    var mList = mutableListOf<OptionInfo>()
    var mItemClickTask: (option: OptionInfo) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_optionspinner, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemProductdetailOptionspinnerBinding) : BaseViewHolder<ItemProductdetailOptionspinnerBinding>(binding.root) {
        fun bind(option: OptionInfo) {
            setOptionRgb(option = option)
            setPadding(position = adapterPosition)
            setOptionText(option = option)

            mBinding.textviewProductdetailOptionspinner.text = option.getOptionText()
            mBinding.viewProductdetailOptionspinnerLine1.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE
            mBinding.linearlayoutProductdetailOptionspinner.setOnClickListener { mItemClickTask(option) }

            if (option.rgb1?.isNotEmpty() == true) {
                mBinding.imageviewProductdetailOptionspinner.visibility = View.VISIBLE
                mBinding.imageviewProductdetailOptionspinner.setBackgroundColor(Color.parseColor(option.rgb1))
            } else
                mBinding.imageviewProductdetailOptionspinner.visibility = View.GONE

            mBinding.executePendingBindings()
        }

        private fun setOptionText(option: OptionInfo) {
            mBinding.textviewProductdetailOptionspinner.text = option.getOptionText()
            mBinding.textviewProductdetailOptionspinnerExtraprice.text = when {
                option.stock == 0 -> mBinding.root.context.getString(R.string.productdetail_option_soldout)
                option.price > 0 -> String.format(mBinding.root.context.getString(R.string.productdetail_option_extraprice_format), option.price)
                else -> ""
            }
            val textColor = if (option.stock == 0) mBinding.root.context.resources.getColor(R.color.pinkish_grey) else mBinding.root.context.resources.getColor(R.color.greyish_brown_two)
            mBinding.textviewProductdetailOptionspinner.setTextColor(textColor)
            mBinding.textviewProductdetailOptionspinnerExtraprice.setTextColor(textColor)
        }

        private fun setOptionRgb(option: OptionInfo) {
            if (!option.rgb1.isNullOrEmpty())
                mBinding.imageviewProductdetailOptionspinner.setBackgroundColor(Color.parseColor(option.rgb1))
        }

        private fun setPadding(position: Int) {
            val padding = CommonViewUtil.convertDpToPixel(dp = 16, context = mBinding.root.context)
            if (position == 0)
                mBinding.linearlayoutProductdetailOptionspinner.setPadding(padding, padding, padding, padding)
            else
                mBinding.linearlayoutProductdetailOptionspinner.setPadding(padding, 0, padding, padding)
        }
    }
}