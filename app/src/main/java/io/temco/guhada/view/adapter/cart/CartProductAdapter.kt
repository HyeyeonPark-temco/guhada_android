package io.temco.guhada.view.adapter.cart

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.ToastUtil
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
        if (mViewModel.shownMenuPos > -1) {
            if (items[mViewModel.shownMenuPos].cartOptionList.isEmpty())
                items[mViewModel.shownMenuPos].cartOptionList = cartOptionList
            notifyItemChanged(mViewModel.shownMenuPos)
        }
    }

    inner class Holder(val binding: ItemCartProductBinding) : BaseViewHolder<ItemCartProductBinding>(binding.root) {
        fun bind(cart: Cart) {
            setSpacing()
            setSoldOutOverlay(cart.cartValidStatus)
            addCancelLine(cart)
            setOptionAdapter(cart)

            cart.tempQuantity = cart.currentQuantity
            binding.amount = cart.tempQuantity
            binding.optionText = getOptionText(cart)
            binding.checkboxCart.isEnabled = cart.cartValidStatus.status
            binding.setOnClickAmountPlus {
                cart.tempQuantity += 1
                binding.amount = cart.tempQuantity
                binding.executePendingBindings()
            }
            binding.setOnClickAmountMinus {
                if (cart.tempQuantity == 1) {
                    ToastUtil.showMessage("최소 수량은 1개입니다.")
                } else {
                    cart.tempQuantity -= 1
                    binding.amount = cart.tempQuantity
                    binding.executePendingBindings()
                }
            }
            binding.setOnClickCancel {
                // 옵션 변경 취소 클릭
                hideOption()
                cart.tempQuantity = cart.currentQuantity
                binding.cart = cart
                notifyItemChanged(adapterPosition)
            }
            binding.setOnClickConfirm {
                // 옵션 변경 클
                val selectedOptionId = getOptionId(cart.cartItemId)
                if (selectedOptionId != null) {
                    // 1. 옵션이 있는 상품
                    if (mViewModel.selectedOptionMap.keys.size == cart.cartOptionList.size)
                        mViewModel.updateCartItemOption(cartItemId = cart.cartItemId, quantity = cart.tempQuantity, selectDealOptionId = selectedOptionId)
                    else
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
                } else {
                    // 2. 옵션이 없는 상품 (수량만 변경)
                    if (cart.tempQuantity == cart.currentQuantity) {
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
                    } else {
                        mViewModel.updateCartItemQuantity(cartItemId = cart.cartItemId, quantity = cart.tempQuantity)
                    }
                }
            }
            binding.setOnClickShowOption {
                if (binding.constraintllayoutCartOption.visibility == View.VISIBLE) hideOption()
                else showOption()
            }
            binding.cart = cart
            binding.executePendingBindings()
        }

        private fun getOptionId(cartItemId: Long): Int? {
            for (cartDealOption in mViewModel.cartDealOptionList) {
                if (cartDealOption.cartItemId == cartItemId) {
                    for (dealOption in cartDealOption.dealOptionList) {

                        for (i in 0 until dealOption.optionMap.keys.size) {
                            val optionKey = dealOption.optionMap.keys.toMutableList()[i]
                            if (dealOption.optionMap[optionKey] == mViewModel.selectedOptionMap[optionKey]) {
                                if (i == dealOption.optionMap.size - 1) {
                                    Log.e("장바구니 옵션 아이디", dealOption.dealOptionId.toString())
                                    return dealOption.dealOptionId
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }

            return null
        }


        private fun hideOption() {
            binding.constraintllayoutCartOption.visibility = View.GONE
            binding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_open)
        }

        private fun showOption() {
            binding.constraintllayoutCartOption.visibility = View.VISIBLE
            binding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_close)
            if (items[adapterPosition].cartOptionList.isEmpty()) {
                mViewModel.shownMenuPos = adapterPosition
                mViewModel.getCartItemOptionList(items[adapterPosition].cartItemId)
            }
        }

        private fun setOptionAdapter(cart: Cart) {
            binding.recyclerviewCartOption.adapter = CartOptionAdapter(mViewModel)
            if (cart.cartOptionList.isNotEmpty())
                (binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(cart.cartOptionList)
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