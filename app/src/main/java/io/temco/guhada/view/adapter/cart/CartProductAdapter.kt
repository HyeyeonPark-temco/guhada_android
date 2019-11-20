package io.temco.guhada.view.adapter.cart

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.cart.CartValidStatus
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.cart.CartViewModel
import io.temco.guhada.databinding.ItemCartProductBinding
import io.temco.guhada.view.CustomSpinner
import io.temco.guhada.view.activity.ProductFragmentDetailActivity
import io.temco.guhada.view.adapter.productdetail.ProductDetailOptionSpinnerAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 장바구니 상품 리스트 Adapter
 * #### 코드 정리 필요 ####
 * @author Hyeyeon Park
 */
class CartProductAdapter(val mViewModel: CartViewModel) : RecyclerView.Adapter<CartProductAdapter.Holder>() {
    var items: MutableList<Cart> = mutableListOf()
    private var expansionCollection: ExpansionLayoutCollection = ExpansionLayoutCollection()
    private var mSelectedOption: OptionInfo? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_cart_product, parent, false)).apply {
        binding.linearlayoutCartProduct.viewTreeObserver.addOnGlobalLayoutListener {
            val height = binding.linearlayoutCartProduct.measuredHeight
            val params = binding.imageviewCartProduct.layoutParams.apply {
                this.width = height
                this.height = height
            }
            binding.imageviewCartProduct.layoutParams = params
            binding.linearlayoutCartProduct.viewTreeObserver.removeOnGlobalLayoutListener { }
        }
    }

    override fun getItemCount(): Int = items.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
        expansionCollection.add(holder.binding.constraintllayoutCartOption)
    }

    fun setCartItemOptionList(cartOptionList: MutableList<OptionInfo>) {
        if (mViewModel.shownMenuPos > -1) {
            if (items[mViewModel.shownMenuPos].cartOptionInfoList.isEmpty()) {
                items[mViewModel.shownMenuPos].cartOptionInfoList = cartOptionList
                notifyItemChanged(mViewModel.shownMenuPos)
            }

//            if (items[mViewModel.shownMenuPos].cartOptionList.isEmpty())
//                items[mViewModel.shownMenuPos].cartOptionList = cartOptionList
        }
    }

    inner class Holder(val binding: ItemCartProductBinding) : BaseViewHolder<ItemCartProductBinding>(binding.root) {
        fun bind(cart: Cart) {
            cart.tempQuantity = cart.currentQuantity

            initCollapsingView()
            setSpacing()

            setSoldOutOverlay(cart.cartValidStatus)
            addCancelLine(cart)
            setOptionAdapter(cart)

            initValidCart(cart)
            setClickListeners(cart)
            setSelectCartItemListener(cart)

            binding.amount = cart.tempQuantity
            binding.optionText = cart.getOptionStr()
            binding.checkboxCart.isEnabled = cart.cartValidStatus.status
            binding.cart = cart
            binding.viewModel = mViewModel
            binding.executePendingBindings()
        }

        // 장바구니 상품 체크 Listener
        private fun setSelectCartItemListener(cart: Cart) {
            binding.checkboxCart.setOnCheckedChangeListener { buttonView, isChecked ->
                mViewModel.notNotifyAllChecked = true
                if (cart.cartValidStatus.status) {
                    if (isChecked) {
                        mViewModel.selectedCartItem.add(cart)
                        mViewModel.selectCartItemId.add(cart.cartItemId.toInt())

                        if (mViewModel.totalItemCount.get() == mViewModel.selectCartItemId.size) {
                            mViewModel.allChecked = ObservableBoolean(true)
                            mViewModel.notifyPropertyChanged(BR.allChecked)
                        }

                        setTotalPrices(cart = cart, isAdd = true)
                    } else {
                        mViewModel.selectedCartItem.remove(cart)
                        mViewModel.selectCartItemId.remove(cart.cartItemId.toInt())
                        mViewModel.allChecked = ObservableBoolean(false)

                        setTotalPrices(cart = cart, isAdd = false)
                    }

                    mViewModel.notNotifyAllChecked = false
                    mViewModel.notifyPropertyChanged(BR.selectCartItemId)
                }
            }
        }

        private fun initCollapsingView() {
            binding.constraintllayoutCartOption.addListener { expansionLayout, expanded ->
                if (expanded) mViewModel.shownMenuPos = adapterPosition
            }
            binding.constraintllayoutCartOption.collapse(true)
        }

        private fun initValidCart(cart: Cart) {
            if (cart.cartValidStatus.status) {
                // 전체 상품 수
                mViewModel.totalItemCount = ObservableInt(mViewModel.totalItemCount.get() + 1)

                // 옵션 리스트 조회
                try {
                    CoroutineScope(Dispatchers.Main).launch {
                        getCartItemList(cart)
                    }
                } catch (e: Exception) {
                    ToastUtil.showMessage("옵션 정보 조회 오류")
                }
            }
        }

        private fun setClickListeners(cart: Cart) {
            binding.setOnClickAmountPlus {
                if (cart.tempQuantity < cart.totalStock) {
                    cart.tempQuantity += 1
                    binding.amount = cart.tempQuantity
                    binding.executePendingBindings()
                } else {
                    val message = String.format(BaseApplication.getInstance().getString(R.string.cart_message_maxquantity), cart.totalStock)
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
                // val selectedOptionId = getOptionId(cart.cartItemId)
                if (cart.selectedCartOption != null) {
                    // 1. 옵션이 있는 상품
                    if (mViewModel.selectedOptionMap.keys.size == cart.cartOptionList.size) {
                        mViewModel.updateCartItemOption(cartItemId = cart.cartItemId, quantity = cart.tempQuantity, selectDealOptionId = cart.selectedCartOption?.dealOptionSelectId?.toInt()
                                ?: 0)
                    } else
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.cart_message_notselectedoption))
                } else {
                    // 2. 옵션이 없는 상품 (수량만 변경)
                    mViewModel.updateCartItemQuantity(cartItemId = cart.cartItemId, quantity = cart.tempQuantity)
                }
            }
            binding.setOnClickShowOption {
                // 옵션 변경 버튼 클릭
                val expansionLayout = binding.constraintllayoutCartOption
                if (expansionLayout.isExpanded) {
                    expansionLayout.collapse(true)
                    mBinding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_open)
                } else {
                    expansionLayout.expand(true)
                    mBinding.imagebuttonCartOptionchange.setImageResource(R.drawable.bag_btn_option_close)
                }
            }
            binding.setOnClickDelete {
                CustomMessageDialog(message = BaseApplication.getInstance().getString(R.string.cart_message_delete),
                        cancelButtonVisible = true,
                        confirmTask = {
                            mViewModel.totalItemCount = ObservableInt(0)//mViewModel.totalItemCount.get() - 1)
                            mViewModel.selectCartItemId = arrayListOf(cart.cartItemId.toInt()) // 삭제할 상품 선택
                            mViewModel.deleteCartItem()
                        }).show(manager = (binding.root.context as AppCompatActivity).supportFragmentManager, tag = CartProductAdapter::class.java.simpleName)
            }
            binding.setOnClickBuyItem {
                mViewModel.onClickItemPayment(cart = cart)
            }

            // 상품 사진, 상품 보기 버튼, 상품 명 클릭 시, 상품 상세 화면으로 이동
            val redirectProductDetailListener = View.OnClickListener {
                val intent = Intent(mBinding.root.context, ProductFragmentDetailActivity::class.java)
                intent.putExtra("dealId", cart.dealId)
                mBinding.root.context.startActivity(intent)
            }
            if (cart.cartValidStatus.status)
                mBinding.imageviewCartProduct.setOnClickListener(redirectProductDetailListener)
            mBinding.buttonCartShow.setOnClickListener(redirectProductDetailListener)
            mBinding.textviewCartProductname.setOnClickListener(redirectProductDetailListener)
        }

        private fun setTotalPrices(cart: Cart, isAdd: Boolean) {
            if (isAdd) {
                mViewModel.totalPaymentPrice = ObservableInt(mViewModel.totalPaymentPrice.get() + cart.discountPrice)
                mViewModel.totalProductPrice = ObservableInt(mViewModel.totalProductPrice.get() + cart.sellPrice)
                mViewModel.totalDiscountPrice = ObservableInt(mViewModel.totalDiscountPrice.get() + cart.discountDiffPrice)
                mViewModel.totalShipPrice = ObservableInt(mViewModel.totalShipPrice.get() + cart.shipExpense)
            } else {
                mViewModel.totalPaymentPrice = ObservableInt(mViewModel.totalPaymentPrice.get() - cart.discountPrice)
                mViewModel.totalProductPrice = ObservableInt(mViewModel.totalProductPrice.get() - cart.sellPrice)
                mViewModel.totalDiscountPrice = ObservableInt(mViewModel.totalDiscountPrice.get() - cart.discountDiffPrice)
                mViewModel.totalShipPrice = ObservableInt(mViewModel.totalShipPrice.get() + cart.shipExpense)
            }

            mViewModel.notifyPropertyChanged(BR.totalPaymentPrice)
            mViewModel.notifyPropertyChanged(BR.totalProductPrice)
            mViewModel.notifyPropertyChanged(BR.totalDiscountPrice)
            mViewModel.notifyPropertyChanged(BR.totalShipPrice)
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
//            binding.recyclerviewCartOption.adapter = CartOptionAdapter(mViewModel)
//            (binding.recyclerviewCartOption.adapter as CartOptionAdapter).setItems(cart.cartOptionList)
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


        /**
         * 옵션 드롭다운 스피너로 변경
         * @since 2019.09.05
         * @author Hyeyeon Park
         */
        private fun initMenuSpinner(cart: Cart) {
//            cart.cartOptionInfoList.add(OptionInfo()) // dummy

            val mMenuSpinnerAdapter = ProductDetailOptionSpinnerAdapter(
                    context = mBinding.root.context,
                    layout = R.layout.item_productdetail_optionspinner,
                    list = cart.cartOptionInfoList)
            mBinding.spinnerProductdetailOption.adapter = mMenuSpinnerAdapter
            mBinding.spinnerProductdetailOption.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val optionList = cart.cartOptionInfoList
                    if (optionList.size > position && optionList[position].stock > 0) {
                        val option = optionList[position]
                        //  mSelectedOption = option
                        cart.selectedCartOption = option
                        mBinding.linearlayoutProductdetailOption.visibility = View.GONE

                        if (option.rgb1 != null && option.rgb1?.isNotEmpty() == true) {
                            mBinding.imageviewProductdetailOptionselected.visibility = View.VISIBLE
                            mBinding.imageviewProductdetailOptionselected.setBackgroundColor(Color.parseColor(option.rgb1))
                        } else {
                            mBinding.imageviewProductdetailOptionselected.visibility = View.GONE
                        }

                        mBinding.textviewProductdetailOptionselected.text = option.getOptionText()
                        mBinding.executePendingBindings()
                    }
                }
            }

            val selectedOption = cart.selectedCartOption ?: OptionInfo()
            val optionInfoList = cart.cartOptionInfoList
            for (i in 0 until optionInfoList.size) {
                val option = optionInfoList[i]
                if (selectedOption.attribute1 == option.attribute1 && selectedOption.attribute2 == option.attribute2 && selectedOption.attribute3 == option.attribute3) {
                    mBinding.spinnerProductdetailOption.setSelection(i)
                }
            }

            // 스피너 드롭다운 Max Height 5개 높이로 설정
            val popup = AppCompatSpinner::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val popupWindow = popup.get(mBinding.spinnerProductdetailOption) as androidx.appcompat.widget.ListPopupWindow
            if (cart.cartOptionInfoList.size > 4) popupWindow.height = CommonViewUtil.convertDpToPixel(230, mBinding.root.context)

            // spinner arrow 리스너
            mBinding.spinnerProductdetailOption.mListener = object : CustomSpinner.OnCustomSpinnerListener {
                override fun onSpinnerOpened() {
                    mBinding.imageviewCartOptionarrow.setImageResource(R.drawable.payment_icon_selectbox_close)
                }

                override fun onSpinnerClosed() {
                    mBinding.imageviewCartOptionarrow.setImageResource(R.drawable.payment_icon_selectbox_open)
                }
            }

            mBinding.executePendingBindings()
        }

        private suspend fun getCartItemList(cart: Cart) {
            val token = Preferences.getToken()
            if (token != null && !token.accessToken.isNullOrEmpty()) {
                val model = OrderServer.getCartItemOptionListForSpinnerAsync(accessToken = "Bearer ${token.accessToken}", cartItemId = cart.cartItemId).await()
                cart.cartOptionInfoList = model.data
                mBinding.framelayoutProductdetailOption.visibility = if (model.data.isEmpty()) View.GONE else View.VISIBLE
                initMenuSpinner(cart)
            }
        }
    }

}