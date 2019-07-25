package io.temco.guhada.view.adapter.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.cart.CartOption
import io.temco.guhada.data.viewmodel.cart.CartViewModel
import io.temco.guhada.databinding.ItemCartOptionBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 상품 옵션 리스트 Adapter
 * @author Hyeyeon Park
 */
class CartOptionAdapter(val mViewModel: CartViewModel) : RecyclerView.Adapter<CartOptionAdapter.Holder>() {
    private var items: MutableList<CartOption> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_option, parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])

    fun setItems(items: MutableList<CartOption>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemCartOptionBinding) : BaseViewHolder<ItemCartOptionBinding>(binding.root) {
        fun bind(cartOption: CartOption) {
            setSpacing()
            binding.cartOption = cartOption
            CartOptionAttrAdapter(mViewModel).let {
                it.setItems(cartOption.attrList)
                binding.recyclerviewCartOptionattr.adapter = it
            }

            binding.executePendingBindings()
        }

        private fun setSpacing() {
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