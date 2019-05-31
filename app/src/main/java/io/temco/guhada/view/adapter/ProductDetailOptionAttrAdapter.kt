package io.temco.guhada.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.view.adapter.base.BaseViewHolder

class ProductDetailOptionAttrAdapter : RecyclerView.Adapter<ProductDetailOptionAttrAdapter.Holder>() {
    var list: List<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_optionattr, parent, false))
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: List<String>?) {
        if (list != null && list.isNotEmpty()) {
            this.list = list
            notifyDataSetChanged()
        }
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionattrBinding) : BaseViewHolder<io.temco.guhada.databinding.ItemProductdetailOptionattrBinding>(binding.root) {
        fun bind(attr: String) {
            if (attr.split("#").size >= 2) {
                binding.imageviewProductdetailOptionattr.setBackgroundColor(Color.parseColor(attr))
                binding.imageviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.textviewProductdetailOptionattr.visibility = View.GONE
            } else {
                binding.textviewProductdetailOptionattr.visibility = View.VISIBLE
                binding.imageviewProductdetailOptionattr.visibility = View.GONE
            }
            binding.attr = attr
            binding.executePendingBindings()
        }
    }
}