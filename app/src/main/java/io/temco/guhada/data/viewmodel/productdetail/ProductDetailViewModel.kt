package io.temco.guhada.data.viewmodel.productdetail

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerFollower
import io.temco.guhada.data.model.seller.SellerSatisfaction
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailViewModel(val listener: OnProductDetailListener?) : BaseObservableViewModel() {
    var seller: Seller = Seller()
        @Bindable
        get() = field
    var sellerFollower = SellerFollower()
        @Bindable
        get() = field

    var sellerSatisfaction = SellerSatisfaction()
        @Bindable
        get() = field
    var dealId: Long = 0
    var product: MutableLiveData<Product> = MutableLiveData()
    var tags: List<String> = ArrayList()
    var menuVisibility = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var bottomBtnVisibility = ObservableInt(View.GONE) // ObservableInt(View.GONE)
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
        ProductServer.getProductDetail(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val data = it.data as Product
                        tags = data.tag.split("/")
                        product.postValue(data)
                    },
                    failedTask = {
                        CommonUtil.debug(o?.toString())
                        listener?.hideLoadingIndicator()
                    },
                    productNotFoundTask = {
                        listener?.closeActivity()
                        listener?.hideLoadingIndicator()
                        ToastUtil.showMessage(it.message)
                    })
        }, dealId)
    }

    fun getSellerInfo() {
        if (product.value?.sellerId != null) {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.seller = it.data as Seller
                            notifyPropertyChanged(BR.seller)
                        })
            }, product.value?.sellerId!!)
        }
    }

    /**
     * 셀러 팔로잉
     */
    fun getLike(target: String) {
        if (product.value?.sellerId != null) {
            ServerCallbackUtil.callWithToken(
                    task = {
                        UserServer.getLike(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        this.sellerFollower.isFollower = true
                                        notifyPropertyChanged(BR.sellerFollower)
                                    },
                                    userLikeNotFoundTask = {
                                        this.sellerFollower.isFollower = false
                                        notifyPropertyChanged(BR.sellerFollower)
                                    }
                            )
                        }, accessToken = it, target = target, userId = product.value?.sellerId as Long)
                    }, invalidTokenTask = {
                this.sellerFollower.isFollower = false
                notifyPropertyChanged(BR.sellerFollower)
            })
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

    fun onClickCart() {
        listener?.showMenu()
    }

    // 바로 구매 클릭
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

    fun onClickSideMenu() {
        listener?.showSideMenu()
    }

    fun onClickBrand() {
        if (product.value != null) {
            Brand().apply {
                this.id = product.value?.brandId ?: 0
                this.nameDefault = product.value?.brandName
            }.let { brand ->
                listener?.closeActivity()
                listener?.setBrandProductList(brand)
            }
        } else {
            listener?.showMessage("일시적인 오류입니다. 다시 시도해주세요.")
        }
    }

    fun onClickHome() {
        listener?.redirectHome()
    }

    fun getSellerSatisfaction() {
        val sellerId = product.value?.sellerId
        if (sellerId != null) {
            UserServer.getSellerSatisfaction(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            sellerSatisfaction = it.data as SellerSatisfaction
                            notifyPropertyChanged(BR.sellerSatisfaction)
                        })
            }, sellerId)
        }
    }

    // 장바구니 담기
    fun addCartItem() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            OrderServer.addCartItem(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { listener?.showAddCartResult() })
            }, accessToken = accessToken, quantity = listener?.getSelectedProductQuantity()!!, dealId = dealId, dealOptionId = listener.getSelectedOptionDealId())
        }, invalidTokenTask = {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            listener?.redirectLoginActivity()
        })
    }
}