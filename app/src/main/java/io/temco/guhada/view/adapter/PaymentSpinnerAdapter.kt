package io.temco.guhada.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import io.temco.guhada.data.viewmodel.PaymentViewModel
import io.temco.guhada.databinding.ItemPaymentSpinnerBinding

class PaymentSpinnerAdapter(context: Context, val layout: Int, val items: List<String>) : ArrayAdapter<String>(context, layout, items) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getCount(): Int = items.size - 1

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ItemPaymentSpinnerBinding>(LayoutInflater.from(parent.context), layout, parent, false)
        binding.message = items[position]

        when (position) {
            items.size - 2 -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 2)
            0 -> binding.framelayoutPaymentSpinner.setPadding(2, 2, 2, 0)
            else -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 0)
        }

        binding.executePendingBindings()
        return binding.root
    }
}