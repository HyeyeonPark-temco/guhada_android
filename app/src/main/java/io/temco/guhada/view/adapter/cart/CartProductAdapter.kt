package io.temco.guhada.view.adapter.cart

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.cart.CartOption
import io.temco.guhada.data.model.cart.CartValidStatus
import io.temco.guhada.data.viewmodel.CartViewModel
import io.temco.guhada.databinding.ItemCartProductBinding
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 장바구니 상품 리스트 Adapter
 * #### 코드 정리 필요 ####
 * @author Hyeyeon Park
 */
class CartProductAdapter(val mViewModel: CartViewModel) : RecyclerView.Adapter<CartProductAdapter.Holder>() {
    private var items: MutableList<Cart> = mutableListOf()
    private var expansionCollection: ExpansionLayoutCollection = ExpansionLayoutCollection()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false))
    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
        expansionCollection.add(holder.binding.constraintllayoutCartOption)
    }

    fun setItems(items: MutableList<Cart>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun setCartItemOptionList(cartOptionList: MutableList<CartOption>) {
        if (mViewModel.shownMenuPos > -1) {
            if (items[mViewModel.shownMenuPos].cartOptionList.isEmpty())
                items[mViewModel.shownMenuPos].cartOptionList = cartOptionList
        }
    }

    inner class Holder(val binding: ItemCartProductBinding) : BaseViewHolder<ItemCartProductBinding>(binding.root) {
        fun bind(cart: Cart) {
            binding.viewModel = mViewModel
            binding.constraintllayoutCartOption.addListener { expansionLayout, expanded ->
                if (expanded) {
                    if (items[adapterPosition].cartOptionList.isEmpty()) {
                        mViewModel.shownMenuPos = adapterPosition
                        mViewModel.getCartItemOptionList(items[adapterPosition].cartItemId)
                    }
                }
            }
            binding.constraintllayoutCartOption.collapse(true)

            setSpacing()
            setSoldOutOverlay(cart.cartValidStatus)
            addCancelLine(cart)
            setOptionAdapter(cart)

            cart.tempQuantity = cart.currentQuantity
            binding.amount = cart.tempQuantity
            binding.optionText = getOptionText(cart)
            binding.checkboxCart.isEnabled = cart.cartValidStatus.status
            binding.setOnClickAmountPlus {
                if (cart.tempQuantity < cart.maxQuantity) {
                    cart.tempQuantity += 1
                    binding.amount = cart.tempQuantity
                    binding.executePendingBindings()
                } else {
                    val message = String.format(BaseApplication.getInstance().getString(R.string.cart_message_maxquantity), cart.minQuantity)
                    ToastUtil.showMessage(message)
                }
            }
            binding.setOnClickAmountMinus {
                if (cart.tempQuantity > cart.minQuantity) {
                    cart.tempQuantity -= 1
                    binding.amount = cart.tempQuantity
                    binding.executePendingBindings()
                } else {
                    val message = String.format(BaseApplication.getInstance().getString(R.string.cart_message_minquantity), cart.minQuantity)
                    ToastUtil.showMessage(message)
                }
            }
            binding.setOnClickCancel {
                // 옵션 변경 취소 클릭
                cart.tempQuantity = cart.currentQuantity
                binding.cart = cart
                binding.executePendingBindings()
                binding.constraintllayoutCartOption.collapse(true)
            }
            binding.setOnClickConfirm {
                // 옵션 변경 클릭
                val selectedOptionId = getOptionId(cart.cartItemId)
                if (selectedOptionId != null) {
                    // 1. 옵션이 있는 상품
                    if (mViewModel.selectedOptionMap.keys.size == cart.cartOptionList.size)
                        mViewModel.updateCartItemOption(cartItemId = cart.cartItemId, quantity = cart.tempQuantity, selectDealOptionId = selectedOptionId)
                    else
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
                } else {
                    // 2. 옵션이 없는 상품 (수량만 변경)
                    mViewModel.updateCartItemQuantity(cartItemId = cart.cartItemId, quantity = cart.tempQuantity)
                }
            }
            binding.setOnClickShowOption {
                val expansionLayout = binding.constraintllayoutCartOption
                if (expansionLayout.isExpanded) expansionLayout.collapse(true)
                else expansionLayout.expand(true)
            }
            binding.setOnClickDelete {
                CustomMessageDialog(message = BaseApplication.getInstance().getString(R.string.cart_message_delete),
                        cancelButtonVisible = true,
                        confirmTask = {
                            mViewModel.selectCartItemId = arrayListOf(cart.cartItemId.toInt())
                            mViewModel.deleteCartItem()
                        }).show(manager = (binding.root.context as AppCompatActivity).supportFragmentManager, tag = CartProductAdapter::class.java.simpleName)
            }
            binding.checkboxCart.setOnCheckedChangeListener { buttonView, isChecked ->
                mViewModel.notNotifyAllChecked = false

                if (isChecked) {
                    mViewModel.selectCartItemId.add(cart.cartItemId.toInt())

                    // allChecked notify 가능하도록 변경
                    if (mViewModel.notNotifyAllChecked) mViewModel.notNotifyAllChecked = false
                    mViewModel.notifyPropertyChanged(BR.selectCartItemId)

                    // 상품 가격 추가
                    setTotalPrices(cart = cart, isAdd = true)
                } else {
                    mViewModel.selectCartItemId.remove(cart.cartItemId.toInt())

                    // notify..(BR.selectCartItemId) 시 allChecked 변경 방지
                    if (!mViewModel.notNotifyAllChecked) mViewModel.notNotifyAllChecked = true
                    mViewModel.notifyPropertyChanged(BR.selectCartItemId)

                    // [전체선택] 선택된 상태에서 상품 체크박스 해제 시, [전체선택] 체크박스 해제
                    if (mViewModel.allChecked.get()) mViewModel.allChecked = ObservableBoolean(false)
                    else mViewModel.notNotifyAllChecked = false

                    // 상품 가격 제외
                    setTotalPrices(cart = cart, isAdd = false)
                }
            }
            binding.cart = cart
            binding.executePendingBindings()
        }

        private fun setTotalPrices(cart: Cart, isAdd: Boolean) {
            if (isAdd) {
                mViewModel.totalPaymentPrice = ObservableInt(mViewModel.totalPaymentPrice.get() + cart.discountPrice)
                mViewModel.totalProductPrice = ObservableInt(mViewModel.totalProductPrice.get() + cart.sellPrice)
                mViewModel.totalDiscountPrice = ObservableInt(mViewModel.totalDiscountPrice.get() + cart.discountDiffPrice)
            } else {
                mViewModel.totalPaymentPrice = ObservableInt(mViewModel.totalPaymentPrice.get() - cart.discountPrice)
                mViewModel.totalProductPrice = ObservableInt(mViewModel.totalProductPrice.get() - cart.sellPrice)
                mViewModel.totalDiscountPrice = ObservableInt(mViewModel.totalDiscountPrice.get() - cart.discountDiffPrice)
            }

            mViewModel.notifyPropertyChanged(BR.totalPaymentPrice)
            mViewModel.notifyPropertyChanged(BR.totalProductPrice)
            mViewModel.notifyPropertyChanged(BR.totalDiscountPrice)
        }

        // 선택한 옵션 값의 dealOptionId 추출
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

        private fun setOptionAdapter(cart: Cart) {
            binding.recyclerviewCartOption.adapter = CartOptionAdapter(mViewModel)
//            if (cart.cartOptionList.isNotEmpty())
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

        private fun setSoldOutOverlay(cartValidStatus: CartValidStatus) {
            val errorMessage = cartValidStatus.cartErrorMessage
            val resources = binding.root.resources
            val optionSoldOutMessage = resources.getString(R.string.cart_message_optionsoldout)
            val productSoldOutMessage = resources.getString(R.string.cart_message_productsoldout)
            val eosMessage = resources.getString(R.string.cart_message_eos)
            val eolMessage = resources.getString(R.string.cart_message_eol)

            if (!cartValidStatus.status && (errorMessage == optionSoldOutMessage || errorMessage == productSoldOutMessage || errorMessage == eosMessage || errorMessage == eolMessage))
                binding.imageviewCartProduct.setColorFilter(Color.argb(212, 255, 255, 255))
            else binding.imageviewCartProduct.colorFilter = null

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