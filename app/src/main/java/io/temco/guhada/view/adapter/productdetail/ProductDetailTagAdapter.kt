package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.databinding.ItemProductdetailTagBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailTagAdapter : RecyclerView.Adapter<ProductDetailTagAdapter.Holder>() {
    private var list: MutableList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate<ItemProductdetailTagBinding>(LayoutInflater.from(parent.context), R.layout.item_productdetail_tag, parent, false))
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemProductdetailTagBinding) : BaseViewHolder<ItemProductdetailTagBinding>(binding.root) {
        fun bind(value: String) {
            binding.value = value
            binding.executePendingBindings()
        }
    }
}