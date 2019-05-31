package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.Product
import io.temco.guhada.view.adapter.base.BaseViewHolder

class ProductDetailOptionAdapter : RecyclerView.Adapter<ProductDetailOptionAdapter.Holder>() {
    var list: List<Product.Option> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_productdetail_option, parent, false))
    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: List<Product.Option>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: io.temco.guhada.databinding.ItemProductdetailOptionBinding) : BaseViewHolder<io.temco.guhada.databinding.ItemProductdetailOptionBinding>(binding.root) {
        fun bind(option: Product.Option) {
            if (adapterPosition > 0) {
                val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                params.setMargins(0, 50, 0, 0)
                binding.linearlayoutProductdetailOption.layoutParams = params
            }

            binding.option = option
            binding.recyclerviewProductdetailOption.layoutManager = LinearLayoutManager(BaseApplication.getInstance().applicationContext, RecyclerView.HORIZONTAL, false)
            binding.executePendingBindings()
        }
    }
}
