package io.temco.guhada.view.adapter.cart

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.cart.CartOption
import io.temco.guhada.data.viewmodel.CartViewModel
import io.temco.guhada.databinding.ItemCartProductBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 상품 리스트 Adapter
 * @author Hyeyeon Park
 */
class CartProductAdapter(val mViewModel: CartViewModel) : RecyclerView.Adapter<CartProductAdapter.Holder>() {
    private var items: MutableList<Cart> = mutableListOf()
    private lateinit var mBinding: ItemCartProductBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false)
        return Holder(mBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: MutableList<Cart>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setCartItemOptionList(cartOptionList: MutableList<CartOption>) {
        if (mViewModel.shownMenuPos > -1 && items[mViewModel.shownMenuPos].cartOptionList.isEmpty()) {
            items[mViewModel.shownMenuPos].cartOptionList = cartOptionList
            notifyItemChanged(mViewModel.shownMenuPos)
        }
    }

    inner class Holder(val binding: ItemCartProductBinding) : BaseViewHolder<ItemCartProductBinding>(binding.root), View.OnClickListener {
        fun bind(cart: Cart) {
            setSpacing()
            setSoldOutOverlay(cart.cartValidStatus)
            addCancelLine(cart)
            binding.recyclerviewCartOption.adapter = CartOptionAdapter(mViewModel)
            if (cart.cartOptionList.isNotEmpty())
                (binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(cart.cartOptionList)
            binding.optionText = getOptionText(cart)
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
                if (items[adapterPosition].cartOptionList.isEmpty()) {
                    mViewModel.shownMenuPos = adapterPosition
                    mViewModel.getCartItemOptionList(items[adapterPosition].cartItemId)
                }
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

        private fun getOptionText(cart: Cart): String {
            var text = ""
            val optionInfo = cart.selectedCartOption
            if (optionInfo != null) {
                if (optionInfo.attribute1 != null) text += (optionInfo.attribute1)
                if (optionInfo.attribute2 != null) text += (", ${optionInfo.attribute2}")
                if (optionInfo.attribute3 != null) text += (", ${optionInfo.attribute3}")
            }

            text += if (text.isEmpty()) ("${cart.currentQuantity}개")
            else (", ${cart.currentQuantity}개")

            return text
        }
    }
}