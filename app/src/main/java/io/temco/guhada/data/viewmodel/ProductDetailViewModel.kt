package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.model.Seller
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.LoginServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailViewModel(val listener: OnProductDetailListener?) : BaseObservableViewModel() {
    var seller: Seller = Seller()
        @Bindable
        get() = field
    var dealId: Long = 0
    var product: MutableLiveData<Product> = MutableLiveData()
    var tags: List<String> = ArrayList()
    var menuVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var bottomBtnVisibility = ObservableInt(View.VISIBLE) // ObservableInt(View.GONE)
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
                CommonUtil.debug(o?.toString())
                listener?.hideLoadingIndicator()
            }
        }, dealId)
    }

    fun getSellerInfo() {
        if (product.value?.sellerId != null) {
            LoginServer.getSellerById(OnServerListener { success, o ->
                if (success) {
                    (o as BaseModel<Seller>).let {
                        this.seller = it.data
                        notifyPropertyChanged(BR.seller)
                    }
                } else {
                    CommonUtil.debug(o?.toString())
                }
            }, product.value?.sellerId!!)
        }
    }

    // 메뉴 이동 탭 [상세정보|상품문의|셀러스토어]
    fun onClickTab(view: View) {
        val pos = view.tag.toString()
        selectedTab = ObservableInt(pos.toInt())
        listener?.scrollToElement(pos.toInt())
        notifyPropertyChanged(BR.selectedTab)
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

    fun onClickBag() = listener?.showMenu()

    fun onClickPayment() {
        listener?.redirectPaymentActivity(menuVisibility.get() == View.VISIBLE)
    }

    fun onClickCloseMenu() {
        menuVisibility = ObservableInt(View.GONE)
        notifyPropertyChanged(BR.menuVisibility)
    }

    fun onClickBack() {
        listener?.closeActivity()
    }

    fun onClickSideMenu(){
        listener?.showSideMenu()
    }


}