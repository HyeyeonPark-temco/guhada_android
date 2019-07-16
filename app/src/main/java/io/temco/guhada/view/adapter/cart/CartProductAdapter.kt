package io.temco.guhada.view.adapter.cart

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.Cart
import io.temco.guhada.databinding.ItemCartProductBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 상품 리스트 Adapter
 * @author Hyeyeon Park
 */
class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.Holder>() {
    private var items: MutableList<Cart> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemCartProductBinding>(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: MutableList<Cart>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemCartProductBinding) : BaseViewHolder<ItemCartProductBinding>(binding.root), View.OnClickListener {
        fun bind(cart: Cart) {
            setSpacing()
            setSoldOutOverlay(cart.cartValidStatus)
            addCancelLine(cart)
            binding.checkboxCart.isEnabled = cart.cartValidStatus.status

            binding.onClickShowOption = this
            binding.cart = cart
            binding.executePendingBindings()
        }

        override fun onClick(v: View?) {
            if (binding.constraintllayoutCartOption.visibility == View.VISIBLE) {
                binding.constraintllayoutCartOption.visibility = View.GONE
                binding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_open)
            } else {
                binding.constraintllayoutCartOption.visibility = View.VISIBLE
                binding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_close)
            }
        }

        private fun setSpacing() {
            if (adapterPosition < items.size - 1) {
                (binding.constraintlayoutCartProduct.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    bottomMargin = 40
                }.let {
                    binding.constraintlayoutCartProduct.layoutParams = it
                }
            }
        }

        private fun setSoldOutOverlay(cartValidStatus: Cart.ValidStatus) {
            val errorMessage = cartValidStatus.cartErrorMessage
            val resources = binding.root.resources
            val optionSoldOutMessage = resources.getString(R.string.cart_message_optionsoldout)
            val productSoldOutMessage = resources.getString(R.string.cart_message_productsoldout)
            val eosMessage = resources.getString(R.string.cart_message_eos)
            val eolMessage = resources.getString(R.string.cart_message_eol)

            if (!cartValidStatus.status && (errorMessage == optionSoldOutMessage || errorMessage == productSoldOutMessage || errorMessage == eosMessage || errorMessage == eolMessage))
                binding.imageviewCartProduct.setColorFilter(Color.argb(212, 255, 255, 255))
        }

        private fun addCancelLine(cart: Cart) {
            if (cart.discountPrice != cart.sellPrice)
                binding.textviewCartProductprice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}