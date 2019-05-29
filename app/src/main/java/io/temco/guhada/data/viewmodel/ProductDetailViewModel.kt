package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailViewModel(val listener: OnProductDetailListener) : BaseObservableViewModel() {
    var dealId: Int = 0
    var product: MutableLiveData<Product> = MutableLiveData()
    var tags: List<String> = ArrayList()
    var bottomBtnVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.bottomBtnVisibility)
        }

    var selectedTab = ObservableInt(0)
        @Bindable
        get() = field

    var productCount = ObservableInt(1)
        @Bindable
        get() = field

    var totalPrice = ObservableInt(product.value?.sellPrice ?: 0)
        @Bindable
        get() = field

    fun onClickTab(view: View) {
        val pos = view.tag.toString()
        selectedTab = ObservableInt(pos.toInt())
        listener.scrollToElement(pos.toInt())
        notifyPropertyChanged(BR.selectedTab)
    }

    fun getDetail() {
        ProductServer.getProductDetail({ success, o ->
            if (success) {
                (o as BaseModel<Product>).let {
                    tags = it.data.tag.split("/")
                    product.postValue(it.data)
                }
            } else {
                CommonUtil.debug(o.toString())
            }
        }, dealId)
    }

    fun onClickPlus() {
        (productCount.get() + 1).let { count ->
            if (product.value != null) {
                productCount = ObservableInt(count)
                totalPrice = ObservableInt(product.value!!.discountPrice * count)

                notifyPropertyChanged(BR.productCount)
                notifyPropertyChanged(BR.totalPrice)
            }
        }
    }

    fun onClickMinus() {
        (productCount.get() - 1).let { count ->
            if (count > 0) {
                productCount = ObservableInt(count)
                totalPrice = ObservableInt(product.value?.discountPrice ?: product.value?.sellPrice
                ?: 0
                * count)
                notifyPropertyChanged(BR.productCount)
                notifyPropertyChanged(BR.totalPrice)
            }
        }
    }
}