package io.temco.guhada.view.adapter.productdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.Product
import io.temco.guhada.databinding.ItemProductdetailInfoBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ProductDetailInfoAdapter : RecyclerView.Adapter<ProductDetailInfoAdapter.Holder>() {
    private var list: MutableList<Product.Item> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_info, parent, false))

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<Product.Item>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemProductdetailInfoBinding) : BaseViewHolder<ItemProductdetailInfoBinding>(binding.root) {
        fun bind(item: Product.Item) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}