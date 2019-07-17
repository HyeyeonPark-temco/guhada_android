package io.temco.guhada.view.adapter.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.cart.CartOption
import io.temco.guhada.databinding.ItemCartOptionBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class CartOptionAdapter : RecyclerView.Adapter<CartOptionAdapter.Holder>() {
    private var items: MutableList<CartOption> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemCartOptionBinding>(LayoutInflater.from(parent.context), R.layout.item_cart_option, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: MutableList<CartOption>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemCartOptionBinding) : BaseViewHolder<ItemCartOptionBinding>(binding.root) {
        fun bind(cartOption: CartOption) {
            setSpacing(cartOption)
            binding.cartOption = cartOption
            binding.executePendingBindings()
        }

        private fun setSpacing(cartOption: CartOption) {
            if (adapterPosition < items.size - 1) {
                (binding.constraintlayoutCartOption.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    bottomMargin = 40
                }.let {
                    binding.constraintlayoutCartOption.layoutParams = it
                }
            }
        }
    }
}