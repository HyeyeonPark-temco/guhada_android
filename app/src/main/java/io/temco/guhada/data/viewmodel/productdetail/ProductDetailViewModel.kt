package io.temco.guhada.data.viewmodel.productdetail

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.base.BaseModel
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
    /**
     * 북마크 여부 데이터 가져왔는지 여부
     * 처음 상품상세에 진입해서 확인하지 않은 상태에서 북마크 버튼을 누르면
     * 값을 변경하지 않음 삭제,취소도 하지 않음
     */
    var initBookMarkData = false
    var productBookMark = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            if(initBookMarkData){
                field = value
                notifyPropertyChanged(BR.productBookMark)
            }
        }

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


    /**
     * 상품 북마크 확인
     */
    fun getBookMark(target : String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    if(CustomLog.flag)CustomLog.L("getBookMark","callWithToken",it)
                    var userId = -1
                    if (it != null){
                        userId = JWT(it.substring(7,it.length)).getClaim("userId").asInt() ?: -1
                        UserServer.getBookMark(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        initBookMarkData = true
                                        if(CustomLog.flag)CustomLog.L("getBookMark","successTask")
                                        var value = (it as BaseModel<BookMark>).data
                                        if(!value.content.isNullOrEmpty()){
                                            productBookMark.set(value.isBookMarkSet)
                                        }
                                    },
                                    dataNotFoundTask = {initBookMarkData = true; productBookMark.set(false) },
                                    failedTask = {initBookMarkData = true; productBookMark.set(false) },
                                    userLikeNotFoundTask = {initBookMarkData = true; productBookMark.set(false) },
                                    serverRuntimeErrorTask = {initBookMarkData = true; productBookMark.set(false) }
                            )
                        }, accessToken = it, target = target, targetId = targetId, userId = userId)
                    }
                }, invalidTokenTask = { productBookMark.set(false) })
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

    private fun saveBookMark(target : String, targetId: Long){
        ServerCallbackUtil.callWithToken(
                task = {
                    var bookMarkResponse = BookMarkResponse(target,targetId)
                    UserServer.saveBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("saveBookMark","successTask")
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, response = bookMarkResponse.getProductBookMarkRespose())
                }, invalidTokenTask = {  })
    }

    private fun deleteBookMark(target : String, targetId: Long){
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("deleteBookMark","successTask")
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, target = target,targetId = targetId)
                }, invalidTokenTask = { })
    }


    fun onClickBookMark(){
        if(initBookMarkData){
            if(productBookMark.get()){
                deleteBookMark(Type.BookMarkTarget.PRODUCT.name, dealId)
            }else{
                saveBookMark(Type.BookMarkTarget.PRODUCT.name, dealId)
            }
            productBookMark.set(!productBookMark.get())
        }else{
            ToastUtil.showMessage("test")
        }
    }


    fun onClickSearch() {
        listener?.showSearchWordActivity()
    }

}