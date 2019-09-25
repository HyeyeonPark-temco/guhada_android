package io.temco.guhada.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import io.temco.guhada.databinding.ItemCommonSpinner2Binding
import io.temco.guhada.databinding.ItemCommonSpinnerBinding

/**
 * Text Spinner (no border)
 * @author Hyeyeon Park
 */
class CommonNonBoxSpinnerAdapter(context: Context, private val layoutRes: Int, var list: List<String> = ArrayList()) : ArrayAdapter<String>(context, layoutRes, list) {
    var mItemCount: Int? = null
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = getCustomView(position, convertView, parent)
    override fun getCount(): Int = if (mItemCount == null) list.size else mItemCount ?: 0

    fun setItems(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = DataBindingUtil.inflate<ItemCommonSpinner2Binding>(LayoutInflater.from(parent.context), layoutRes, parent, false)
        if (list.isNotEmpty()) binding.message = list[position]

        when (position) {
            list.size - 1 -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 2)
            0 -> binding.framelayoutPaymentSpinner.setPadding(2, 2, 2, 0)
            else -> binding.framelayoutPaymentSpinner.setPadding(2, 0, 2, 0)
        }
        binding.executePendingBindings()
        return binding.root
    }
}
