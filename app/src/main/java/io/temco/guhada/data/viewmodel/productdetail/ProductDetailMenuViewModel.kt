package io.temco.guhada.data.viewmodel.productdetail

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.option.OptionInfo
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * 상품 옵션 ViewModel
 * @author Hyeyeon Park
 */
class ProductDetailMenuViewModel(private val listener: OnProductDetailMenuListener) : BaseObservableViewModel() {
    private val EXTRA_PRICE = 1
    private val DEAL_OPTION_ID = 2

    /** COMMON */
    var product: Product = Product()
        set(value) {
            field = value
            totalPrice = ObservableInt(product.discountPrice)// ObservableInt(if (product.options?.isEmpty() == true) product.discountPrice else 0)
            notifyPropertyChanged(BR.totalPrice)
        }

    var extraPrice = ObservableInt(0)
        @Bindable
        get() = field

    var totalPrice = ObservableInt(0)
        @Bindable
        get() = field

    /** GRID LIST (DEPRECATED) */
    var closeButtonVisibility = View.VISIBLE

    var productCount = ObservableInt(1)
        @Bindable
        get() = field
    var colorName = ObservableField<String>("")
        @Bindable
        get() = field
    var optionMap: MutableMap<String, OptionAttr> = mutableMapOf()

    var extraPriceOperator = ObservableField<String>("+")
        @Bindable
        get() = field

    /** DROP DOWN LIST */
    var mSelectedOptionInfo: OptionInfo? = null
    var mIsSpinnerOpen = ObservableBoolean(false)
        @Bindable
        get() = field
    var mIsBottomSpinnerOpen  = ObservableBoolean(false)
        @Bindable
        get() = field

    fun onClickCloseMenu() = listener.closeMenu()

    fun onClickPlus() = changeProductCount {
        val plusCount = (productCount.get() + 1)
        val maxStock = if (product.options?.isEmpty()
                        ?: true) product.totalStock else mSelectedOptionInfo?.stock ?: 0
        if (maxStock < plusCount) ToastUtil.showMessage(String.format(BaseApplication.getInstance().getString(R.string.cart_message_maxquantity), maxStock))
        else changeTotalPrice(plusCount)
    }

    fun onClickMinus() = changeProductCount { (productCount.get() - 1).let { count -> if (count > 0) changeTotalPrice(count) } }

    fun getDealOptionId(): Long? = mSelectedOptionInfo?.dealOptionSelectId?.toLong() //getOptionInfo(DEAL_OPTION_ID)?.toLong()

    private fun changeProductCount(task: () -> Unit) {
//        if (optionMap.keys.size != product.options?.size) listener.showMessage(BaseApplication.getInstance().getString(R.string.productdetail_message_selectoption))
        if (product.options?.isNotEmpty() ?: false && mSelectedOptionInfo == null) listener.showMessage(BaseApplication.getInstance().getString(R.string.productdetail_message_selectoption))
        else task()
    }

    private fun changeTotalPrice(count: Int) {
        productCount = ObservableInt(count)
        totalPrice = ObservableInt((product.sellPrice + extraPrice.get()) * count)
        notifyPropertyChanged(BR.productCount)
        notifyPropertyChanged(BR.totalPrice)
    }

    fun onSelectAttr(optionAttr: OptionAttr, type: String, position: Int) {
        optionMap[type] = optionAttr
        if (type == "COLOR") {
            listener.setColorName(optionAttr) {
                colorName = ObservableField(optionAttr.name)
                notifyPropertyChanged(BR.colorName)
            }
        }
    }

    /**
     * GRID LIST (DEPRECATED)
     */
    fun getExtraPrice() {
        extraPrice = ObservableInt(getOptionInfo(EXTRA_PRICE) ?: 0)
        totalPrice = ObservableInt(product.discountPrice + extraPrice.get())
        getExtraPriceOperator()
        notifyPropertyChanged(BR.extraPrice)
        notifyPropertyChanged(BR.totalPrice)
    }

    private fun getOptionInfo(flag: Int): Int? {
        val color = optionMap["COLOR"]
        val size = optionMap["SIZE"]
        var dealOptionSelectId: Double? = 0.0

        if (product.optionInfos != null) {
            for (optionInfo in product.optionInfos!!) {
                if (color != null) {
                    if (size != null && optionInfo.attribute1 == color.name && optionInfo.attribute2 == size.name) {  // 색상 O, 사이즈 O
                        dealOptionSelectId = if (flag == EXTRA_PRICE) optionInfo.price.toDouble() else optionInfo.dealOptionSelectId
                    } else {
                        if (optionInfo.attribute1 == color.name) { // 색상 O, 사이즈 X
                            dealOptionSelectId = if (flag == EXTRA_PRICE) optionInfo.price.toDouble() else optionInfo.dealOptionSelectId
                        }
                    }
                } else {
                    if (size != null && optionInfo.attribute1 == size.name) {   // 색상 X, 사이즈 O
                        dealOptionSelectId = if (flag == EXTRA_PRICE) optionInfo.price.toDouble() else optionInfo.dealOptionSelectId
                    } else {  // 색상 X, 사이즈 X => 옵션 없음
                        dealOptionSelectId = if (flag == EXTRA_PRICE) 0.0 else null
                    }
                }
            }
        }

        return dealOptionSelectId?.toInt()
    }

    private fun getExtraPriceOperator() {
        if (extraPrice.get().toString().split("-").size < 2) {
            extraPriceOperator = ObservableField("+")
            notifyPropertyChanged(BR.extraPriceOperator)
        }
    }

}