package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnProductDetailMenuListener
import io.temco.guhada.data.model.option.OptionAttr
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailMenuViewModel(private val listener: OnProductDetailMenuListener) : BaseObservableViewModel() {
    private val EXTRA_PRICE = 1
    private val DEAL_OPTION_ID = 2

    var closeButtonVisibility = View.VISIBLE
    var product: Product = Product()
        set(value) {
            field = value
            totalPrice = ObservableInt(if (product.options?.isEmpty() == true) product.discountPrice else 0)
            notifyPropertyChanged(BR.totalPrice)
        }
    var totalPrice = ObservableInt(0)
        @Bindable
        get() = field
    var productCount = ObservableInt(1)
        @Bindable
        get() = field
    var colorName = ObservableField<String>("")
        @Bindable
        get() = field
    var optionMap: MutableMap<String, OptionAttr> = mutableMapOf()
    var extraPrice = ObservableInt(0)
        @Bindable
        get() = field
    var extraPriceOperator = ObservableField<String>()
        @Bindable
        get() = field

    fun getDealOptionId(): Long? = getOptionInfo(DEAL_OPTION_ID)

    fun getExtraPrice() {
        extraPrice = ObservableInt(getOptionInfo(EXTRA_PRICE)?.toInt() ?: 0)
        totalPrice = ObservableInt(product.discountPrice + extraPrice.get())
        getExtraPriceOperator()
        notifyPropertyChanged(BR.extraPrice)
        notifyPropertyChanged(BR.totalPrice)
    }

    private fun getOptionInfo(flag: Int): Long? {
        val color = optionMap["COLOR"]
        val size = optionMap["SIZE"]
        var result: Any? = 0

        if (product.optionInfos != null) {
            for (optionInfo in product.optionInfos!!) {
                if (color != null) {
                    if (size != null && optionInfo.attribute1 == color.name && optionInfo.attribute2 == size.name) {  // 색상 O, 사이즈 O
                        result = if (flag == EXTRA_PRICE) optionInfo.price else optionInfo.dealOptionSelectId
                    } else {
                        if (optionInfo.attribute1 == color.name) { // 색상 O, 사이즈 X
                            result = if (flag == EXTRA_PRICE) optionInfo.price else optionInfo.dealOptionSelectId
                        }
                    }
                } else {
                    if (size != null && optionInfo.attribute1 == size.name) {   // 색상 X, 사이즈 O
                        result = if (flag == EXTRA_PRICE) optionInfo.price else optionInfo.dealOptionSelectId
                    } else {  // 색상 X, 사이즈 X => 옵션 없음
                        result = if (flag == EXTRA_PRICE) 0 else null
                    }
                }
            }
        }

        return result.toString().toLong()
    }

    private fun getExtraPriceOperator() {
        if (extraPrice.get().toString().split("-").size < 2) {
            extraPriceOperator = ObservableField("+")
            notifyPropertyChanged(BR.extraPriceOperator)
        }
    }

    fun onClickCloseMenu() = listener.closeMenu()

    fun onClickPlus() = changeProductCount { changeTotalPrice((productCount.get() + 1)) }

    fun onClickMinus() = changeProductCount { (productCount.get() - 1).let { count -> if (count > 0) changeTotalPrice(count) } }

    private fun changeProductCount(task: () -> Unit) {
        if (optionMap.keys.size != product.options?.size) listener.showMessage(BaseApplication.getInstance().getString(R.string.productdetail_message_selectoption))
        else task()
    }

    private fun changeTotalPrice(count: Int) {
        productCount = ObservableInt(count)
        totalPrice = ObservableInt((product.discountPrice + extraPrice.get()) * count)
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

}