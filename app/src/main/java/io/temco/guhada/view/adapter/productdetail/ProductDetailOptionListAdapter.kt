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
 * 옵션 하단 팝업 리스트 adapter
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

            mBinding.textviewProductdetailOptionspinner.text = getOptionText(option = option)
            mBinding.viewProductdetailOptionspinnerLine1.visibility = if (adapterPosition == 0) View.VISIBLE else View.GONE
            mBinding.linearlayoutProductdetailOptionspinner.setOnClickListener { mItemClickTask(option) }
            mBinding.executePendingBindings()
        }

        private fun setOptionText(option: OptionInfo) {
            var optionText = getOptionText(option = option)

            if (option.stock == 0) {
                mBinding.textviewProductdetailOptionspinner.text = "$optionText ${BaseApplication.getInstance().getString(R.string.productdetail_option_soldout)}"
                mBinding.textviewProductdetailOptionspinner.setTextColor(mBinding.root.context.resources.getColor(R.color.pinkish_grey))
            } else {
                if (option.price > 0)
                    optionText += " ${String.format(BaseApplication.getInstance().getString(R.string.productdetail_option_extraprice_format), option.price)}"
                mBinding.textviewProductdetailOptionspinner.text = optionText
                mBinding.textviewProductdetailOptionspinner.setTextColor(mBinding.root.context.resources.getColor(R.color.greyish_brown_two))
            }
//            if (option.stock == 0) {
//                mBinding.textviewProductdetailOptionspinner.text = "${option.attribute1} (품절)"
//                mBinding.textviewProductdetailOptionspinner.setTextColor(mBinding.root.context.resources.getColor(R.color.pinkish_grey))
//            }
        }

        private fun setOptionRgb(option: OptionInfo) {
            if (!option.rgb1.isNullOrEmpty())
                mBinding.imageviewProductdetailOptionspinner.setBackgroundColor(Color.parseColor(option.rgb1))
        }

        fun getOptionText(option: OptionInfo): String {
            var optionText = ""
            if (!option.attribute1.isNullOrEmpty())
                optionText = "${option.attribute1}"

            if (!option.attribute2.isNullOrEmpty())
                optionText = "$optionText, ${option.attribute2}"

            if (!option.attribute3.isNullOrEmpty())
                optionText = "$optionText, ${option.attribute3}"

            return optionText
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