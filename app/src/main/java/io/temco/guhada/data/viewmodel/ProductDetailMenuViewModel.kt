package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.ProductDetailActivity
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter

class ProductDetailMenuViewModel(private val menuListener: ProductDetailActivity.OnMenuListener) : BaseObservableViewModel() {
    var closeButtonVisibility = View.VISIBLE

    var product: Product = Product()
    var totalPrice = ObservableInt(0)
        @Bindable
        get() = field
    var productCount = ObservableInt(1)
        @Bindable
        get() = field
    var colorName = ObservableField<String>("")
        @Bindable
        get() = field

    var optionMap: MutableMap<String, Int> = mutableMapOf()

    fun onClickCloseMenu() = menuListener.closeMenu()

    fun onClickPlus() {
        (productCount.get() + 1).let { count ->
            productCount = ObservableInt(count)
            totalPrice = ObservableInt(product.discountPrice * count)

            notifyPropertyChanged(BR.productCount)
            notifyPropertyChanged(BR.totalPrice)
        }
    }

    fun onClickMinus() {
        (productCount.get() - 1).let { count ->
            if (count > 0) {
                productCount = ObservableInt(count)
                totalPrice = ObservableInt(product.discountPrice * count)
                notifyPropertyChanged(BR.productCount)
                notifyPropertyChanged(BR.totalPrice)
            }
        }
    }

    fun onSelectAttr(optionAttr: ProductDetailOptionAdapter.OptionAttr, type: String, position: Int) {
        optionMap[type] = position
        if (type == "COLOR") {
            menuListener.setColorName(optionAttr) {
                colorName = ObservableField(optionAttr.name)
                notifyPropertyChanged(BR.colorName)
            }
        }
    }


}