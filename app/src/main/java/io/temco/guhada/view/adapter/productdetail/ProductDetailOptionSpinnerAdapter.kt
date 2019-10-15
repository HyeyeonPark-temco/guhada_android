package io.temco.guhada.view.adapter.productdetail

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.databinding.ItemProductdetailOptionspinnerBinding

/**
 * 상품상세-옵션 spinner adapter
 * @author Hyeyeon Park
 * @since 2019.09.05
 */
class ProductDetailOptionSpinnerAdapter(context: Context, val layout: Int, var list: List<OptionInfo> = ArrayList()) : ArrayAdapter<OptionInfo>(context, layout, list) {
    private lateinit var mBinding: ItemProductdetailOptionspinnerBinding

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getCount(): Int = list.size - 1

    fun setItems(list: List<OptionInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
        val option = list[position]

        setOptionRgb(option = option)
        setPadding(position = position)
        setOptionText(option = option)

        mBinding.viewProductdetailOptionspinnerLine1.visibility = if (position == 0) View.VISIBLE else View.GONE
        mBinding.viewProductdetailOptionspinnerLine2.visibility = if (position == list.size - 2) View.VISIBLE else View.GONE

        if (option.rgb1?.isNotEmpty() ?: false) {
            mBinding.imageviewProductdetailOptionspinner.visibility = View.VISIBLE
            mBinding.imageviewProductdetailOptionspinner.setBackgroundColor(Color.parseColor(option.rgb1))
        } else
            mBinding.imageviewProductdetailOptionspinner.visibility = View.GONE

        mBinding.executePendingBindings()
        return mBinding.root
    }

    private fun setOptionText(option: OptionInfo) {
        var optionText = getOptionText(option = option)

        if (option.stock == 0) {
            mBinding.textviewProductdetailOptionspinner.text = "$optionText ${BaseApplication.getInstance().getString(R.string.productdetail_option_soldout)}"
            mBinding.textviewProductdetailOptionspinner.setTextColor(context.resources.getColor(R.color.pinkish_grey))
        } else {
            if (option.price > 0)
                optionText += " ${String.format(BaseApplication.getInstance().getString(R.string.productdetail_option_extraprice_format), option.price)}"
            mBinding.textviewProductdetailOptionspinner.text = optionText
            mBinding.textviewProductdetailOptionspinner.setTextColor(context.resources.getColor(R.color.greyish_brown_two))
        }
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
        val padding = CommonViewUtil.convertDpToPixel(dp = 16, context = context)
        if (position == 0)
            mBinding.linearlayoutProductdetailOptionspinner.setPadding(padding, padding, padding, padding)
        else
            mBinding.linearlayoutProductdetailOptionspinner.setPadding(padding, 0, padding, padding)
    }


}