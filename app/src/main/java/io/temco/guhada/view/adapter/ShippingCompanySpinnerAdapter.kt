package io.temco.guhada.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import io.temco.guhada.data.model.ShippingCompany
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.databinding.ItemPaymentSpinnerBinding

/**
 * 택배사 목록 spinner adapter
 * @author Hyeyeon Park
 * @since 2019.08.19
 */
class ShippingCompanySpinnerAdapter(context: Context, val layout: Int, var list: List<ShippingCompany> = ArrayList()) : ArrayAdapter<ShippingCompany>(context, layout, list) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getCount(): Int = list.size - 1

    fun setItems(list: List<ShippingCompany>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ItemPaymentSpinnerBinding>(LayoutInflater.from(parent.context), layout, parent, false)
        if (list.isNotEmpty()) binding.message = list[position].name

        when (position) {
            list.size - 2 -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 2)
            0 -> binding.framelayoutPaymentSpinner.setPadding(2, 2, 2, 0)
            else -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 0)
        }

        binding.executePendingBindings()
        return binding.root
    }
}