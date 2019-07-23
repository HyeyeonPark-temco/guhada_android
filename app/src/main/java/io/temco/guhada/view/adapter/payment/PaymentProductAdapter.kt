package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.databinding.ItemPaymentProductBinding

class PaymentProductAdapter : RecyclerView.Adapter<PaymentProductAdapter.Holder>() {
    private var items = mutableListOf<BaseProduct>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemPaymentProductBinding>(LayoutInflater.from(parent.context), R.layout.item_payment_product, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: MutableList<BaseProduct>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemPaymentProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: BaseProduct) {
            setSpacing()
            binding.product = product
            binding.executePendingBindings()
        }
        private fun setSpacing() {
            if (adapterPosition < items.size - 1) {
                (binding.linearlayoutPaymentproductContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    bottomMargin = 40
                }.let {
                    binding.linearlayoutPaymentproductContainer.layoutParams = it
                }
            }
        }
    }
}