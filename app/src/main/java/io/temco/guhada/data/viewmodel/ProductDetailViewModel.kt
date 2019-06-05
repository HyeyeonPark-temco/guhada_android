package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.Flag.ResultCode.DATA_NOT_FOUND
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.ProductDetailOptionAdapter

class ProductDetailViewModel(val listener: OnProductDetailListener?) : BaseObservableViewModel() {
    var optionMap: MutableMap<String, Int> = mutableMapOf()
    var dealId: Int = 0
    var product: MutableLiveData<Product> = MutableLiveData()
    var tags: List<String> = ArrayList()

    var menuVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var colorName = ObservableField<String>("")
        @Bindable
        get() = field
    var bottomBtnVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.bottomBtnVisibility)
        }
    var imagePos = 1
        @Bindable
        get() = field
    var selectedTab = ObservableInt(0)
        @Bindable
        get() = field

    var productCount = ObservableInt(1)
        @Bindable
        get() = field

    var totalPrice = ObservableInt(0)
        @Bindable
        get() = field

    var refundInfoExpanded = ObservableBoolean(false)
        @Bindable
        get() = field

    var productNotifiesExpanded = ObservableBoolean(false)
        @Bindable
        get() = field

    var advantageInfoExpanded = ObservableBoolean(false)
        @Bindable
        get() = field

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

    // CLICK LISTENER
    fun onClickTab(view: View) {
        val pos = view.tag.toString()
        selectedTab = ObservableInt(pos.toInt())
        listener?.scrollToElement(pos.toInt())
        notifyPropertyChanged(BR.selectedTab)
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

    fun onClickRefundInfo() {
        refundInfoExpanded = ObservableBoolean(!refundInfoExpanded.get())
        notifyPropertyChanged(BR.refundInfoExpanded)
    }

    fun onClickProductNotifies() {
        productNotifiesExpanded = ObservableBoolean(!productNotifiesExpanded.get())
        notifyPropertyChanged(BR.productNotifiesExpanded)
    }

    fun onClickAdvantageInfo() {
        advantageInfoExpanded = ObservableBoolean(!advantageInfoExpanded.get())
        notifyPropertyChanged(BR.advantageInfoExpanded)
    }

    fun onClickBag() {
        if (optionMap.keys.size == product.value?.options?.size) {
            // 옵션 선택 완료 -> 장바구니 이동
        } else {
            // 옵션 선택 미완료 -> 메뉴 view 노출
            menuVisibility = ObservableInt(View.VISIBLE)
            notifyPropertyChanged(BR.menuVisibility)
        }
    }

    fun onClickCloseMenu() {
        menuVisibility = ObservableInt(View.GONE)
        notifyPropertyChanged(BR.menuVisibility)
    }

    fun onSelectAttr(optionAttr: ProductDetailOptionAdapter.OptionAttr, type: String, position: Int) {
        optionMap[type] = position
        listener?.setColorName(optionAttr)
    }
}