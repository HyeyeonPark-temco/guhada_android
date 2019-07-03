package io.temco.guhada.view.custom.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import io.temco.guhada.databinding.ItemPaymentSpinnerBinding

open class CustomSpinnerAdapter(context: Context, val viewModel: CustomSpinnerViewModel, val layout: Int, var list: List<String> = ArrayList()) : ArrayAdapter<String>(context, layout, list) {
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getCount(): Int = list.size - 1

    fun setItems(list: List<String>) {
        viewModel.list = list
        this.list = list
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ItemPaymentSpinnerBinding>(LayoutInflater.from(parent.context), layout, parent, false)
        binding.message = list[position]

        when (position) {
            list.size - 2 -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 2)
            0 -> binding.framelayoutPaymentSpinner.setPadding(2, 2, 2, 0)
            else -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 0)
        }

        binding.executePendingBindings()
        return binding.root
    }
}