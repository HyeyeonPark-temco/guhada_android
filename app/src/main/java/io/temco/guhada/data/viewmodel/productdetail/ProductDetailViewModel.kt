package io.temco.guhada.data.viewmodel.productdetail

import android.text.TextUtils
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.kochava.base.Tracker
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.BookMarkTarget
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.enum.SaveActionType
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.listener.OnProductDetailListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.BookMark
import io.temco.guhada.data.model.BookMarkResponse
import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.order.OrderItemResponse
import io.temco.guhada.data.model.point.ExpectedPointResponse
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.model.point.PointRequest
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerSatisfaction
import io.temco.guhada.data.model.seller.SellerStore
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ProductDetailViewModel(val listener: OnProductDetailListener?) : BaseObservableViewModel() {
    var seller: Seller = Seller()
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
    var bottomBtnVisible = ObservableBoolean(false)
        @Bindable
        get() = field

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

    var advantageInfoExpanded = ObservableBoolean(true)
        @Bindable
        get() = field

    var mSellerBookMark = BookMark()
        @Bindable
        get() = field

    var mExpectedCouponList = MutableLiveData<MutableList<Coupon>>()
        @Bindable
        get() = field

    var notifySellerStoreFollow: (bookMark: BookMark) -> Unit = {}

    var mSellerStore = ObservableField<SellerStore>(SellerStore())
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
            if (initBookMarkData) {
                field = value
                notifyPropertyChanged(BR.productBookMark)
            }
        }

    var mReviewSummary = ObservableField<ReviewSummary>()
        @Bindable
        get() = field

    // 혜택 정보
    var mExpectedPoint: MutableLiveData<ExpectedPointResponse> = MutableLiveData()
    var mSetBadgeTask: () -> Unit = {}

    // 북마크
    var mIsBookMarkSaved = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            mBookMarkVisible = true
        }
    var mBookMarkVisible = false

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
        if (product.value?.sellerId != null && product.value?.sellerId ?: 0 > 0) {
            UserServer.getSellerById(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.seller = it.data as Seller
                            notifyPropertyChanged(BR.seller)
                        },
                        serverRuntimeErrorTask = {})
            }, product.value?.sellerId!!)
        }
    }

    /**
     * 셀러 팔로잉 여부
     */
    fun getSellerBookMark(target: String) {
        if (product.value?.sellerId != null && product.value?.sellerId ?: 0 > 0) {
            ServerCallbackUtil.callWithToken(
                    task = {
                        val userId = JWT(it.split("Bearer ")[1]).getClaim("userId").asLong()
                        if (userId != null) {
                            UserServer.getLike(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = { result ->
                                            this.mSellerBookMark = result.data as BookMark
                                            notifyPropertyChanged(BR.mSellerBookMark)
                                        },
                                        serverRuntimeErrorTask = {}
                                )
                            }, accessToken = it, target = target, targetId = product.value?.sellerId!!, userId = userId)
                        }
                    },
                    invalidTokenTask = {})
        }
    }


    /**
     * 상품 북마크 확인
     *
     * @exception
     * [IllegalStateException] Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $
     * [0714]  Failed to connect to dev.user.guhada.com/13.125.76.109:8080
     * line ServerCallbackUtil.kt:105
     * @since 2019.08.05
     * @author Hyeyeon Park
     *
     */
    fun getBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null) {
                        if (CustomLog.flag) CustomLog.L("getBookMark", "callWithToken", it)
                        val userId = JWT(it.substring(7, it.length)).getClaim("userId").asInt()
                                ?: -1
                        UserServer.getBookMark(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        initBookMarkData = true
                                        if (CustomLog.flag) CustomLog.L("getBookMark", "successTask")
                                        var value = (it as BaseModel<BookMark>).data
                                        if (!value.content.isNullOrEmpty()) {
                                            productBookMark.set(value.isBookMarkSet)
                                        }
                                    },
                                    dataNotFoundTask = { initBookMarkData = true; productBookMark.set(false) },
                                    failedTask = { initBookMarkData = true; productBookMark.set(false) },
                                    userLikeNotFoundTask = { initBookMarkData = true; productBookMark.set(false) },
                                    serverRuntimeErrorTask = { initBookMarkData = true; productBookMark.set(false) }
                            )
                        }, accessToken = it, target = target, targetId = targetId, userId = userId)
                    }
                }, invalidTokenTask = { productBookMark.set(false) })
    }

    /**
     * 발급 가능한 쿠폰 조회
     * @author Hyeyeon Park
     * @since 2019.08.29
     */
    fun getExpectedCoupon() {
        ServerCallbackUtil.callWithToken(
                task = { accessToken ->
                    OrderItemResponse().apply {
                        this.dCategoryId = product.value?.dCategoryId?.toLong() ?: 0L
                        this.lCategoryId = product.value?.lCategoryId?.toLong() ?: 0
                        this.mCategoryId = product.value?.mCategoryId?.toLong() ?: 0
                        this.sCategoryId = product.value?.sCategoryId?.toLong() ?: 0
                        this.dealId = product.value?.dealId ?: 0
                        this.sellerId = product.value?.sellerId ?: 0
                        this.sellPrice = product.value?.sellPrice ?: 0
                        this.discountPrice = product.value?.discountPrice ?: 0
                    }.let { orderItemResponse ->
                        BenefitServer.getExpectedCoupon(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        val list = it.data as MutableList<Coupon>
                                        val tempList = mutableListOf<Coupon>()
                                        for (item in list)
                                            if (item.saveType == PointRequest.SaveType.DOWNLOAD.type)
                                                tempList.add(item)

                                        this@ProductDetailViewModel.mExpectedCouponList.postValue(tempList)
                                    },
                                    serverRuntimeErrorTask = {})
                        }, accessToken = accessToken, item = orderItemResponse, saveActionType = SaveActionType.BUY.type, serviceType = PointRequest.ServiceType.AOS.type)
                    }
                },
                invalidTokenTask = {}
        )
    }

    /**
     * 상단 review summary 노출 여부 판단 목적
     * @author Hyeyeon Park
     * @since 2019.09.19
     */
    fun getProductReviewSummary() {
        if (product.value != null) {
            if (product.value?.productId != null && product.value?.productId ?: 0 > 0) {
                UserServer.getProductReviewSummary(OnServerListener { success, o ->
                    ServerCallbackUtil.executeByResultCode(success, o,
                            successTask = {
                                this.mReviewSummary = ObservableField(it.data as ReviewSummary)
                                notifyPropertyChanged(BR.mReviewSummary)
                            },
                            failedTask = {},
                            dataNotFoundTask = {},
                            serverRuntimeErrorTask = {})
                }, productId = product.value?.productId!!)
            }
        }
    }

    /**
     * 배송/반품/교환 판매자 정보
     * @author Hyeyeon Park
     * @since 2019.09.20
     */
    fun getSellerStoreInfo() {
        if (product.value?.sellerId != null && product.value?.sellerId ?: 0 > 0)
            UserServer.getSellerStoreInfo(OnServerListener { success, o ->
                val sellerStore = (o as BaseModel<SellerStore>).data
                this.mSellerStore = ObservableField(sellerStore)
                notifyPropertyChanged(BR.mSellerStore)
            }, sellerId = product.value?.sellerId!!, accessToken = null)
    }

    /**
     * 혜택 정보-포인트 적립
     * 미로그인 상태에서도 노출
     * @author Hyeyeon Park
     */
    fun getDueSavePoint() {
        val pointProcessParam = PointProcessParam()

        val orderProd = OrderItemResponse().apply {
            this.dcategoryId = product.value?.dCategoryId ?: 0
            this.dealId = product.value?.dealId ?: 0L
            this.discountPrice = product.value?.discountPrice ?: 0
            this.lcategoryId = product.value?.lCategoryId ?: 0
            this.mcategoryId = product.value?.mCategoryId ?: 0
            this.scategoryId = product.value?.sCategoryId ?: 0
            this.productPrice = product.value?.sellPrice ?: 0
            this.orderProdList.add(OrderItemResponse.OrderOption().apply {
                this.price = product.value?.totalPrice ?: 0
            })
        }

        val bundle = PointProcessParam.PointBundle().apply {
            this.bundlePrice = product.value?.shipExpense ?: 0
            this.orderProdList.add(orderProd)
        }

        pointProcessParam.bundleList.add(bundle)

        if (pointProcessParam.bundleList.isNotEmpty()) {
            pointProcessParam.consumptionPoint = 0
            pointProcessParam.consumptionType = PointProcessParam.PointConsumption.BUY.type
            pointProcessParam.pointType = PointProcessParam.PointSave.BUY.type
            pointProcessParam.serviceType = PointRequest.ServiceType.AOS.type

            val listener = OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            val expectedPointResponse = it.data as ExpectedPointResponse
                            this.mExpectedPoint.postValue(expectedPointResponse)
                        },
                        dataIsNull = {
                            if (it is BaseModel<*>) {
                                ToastUtil.showMessage(it.message)
                                if (CustomLog.flag) CustomLog.E("[process/total-due-save] ${it.message}")
                            }
                        },
                        serverRuntimeErrorTask = {
                            ToastUtil.showMessage(it.message)
                            if (CustomLog.flag) CustomLog.E("[process/total-due-save] ${it.message}")
                        })
            }

            ServerCallbackUtil.callWithToken(
                    task = { BenefitServer.getDueSavePoint(listener = listener, accessToken = it, pointProcessParam = pointProcessParam) },
                    invalidTokenTask = { BenefitServer.getDueSavePoint(listener = listener, pointProcessParam = pointProcessParam) })
        }
    }


//    LISTENER

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
                listener?.setBrandProductList(brand)
                /*listener?.closeActivity()
                listener?.setBrandProductList(brand)*/
            }
        } else {
            // listener?.showMessage("일시적인 오류입니다. 다시 시도해주세요.")
        }
    }

    fun onClickHome() {
        listener?.redirectHome()
    }

    fun onClickSellerBookMark() {
        if (product.value?.sellerId != null) {
            if (this.mSellerBookMark.content.isEmpty())
                saveBookMark(target = BookMarkTarget.SELLER.target, targetId = product.value?.sellerId!!)
            else
                deleteBookMark(target = BookMarkTarget.SELLER.target, targetId = product.value?.sellerId!!)
        }
    }

    fun getSellerSatisfaction() {
        val sellerId = product.value?.sellerId
        if (sellerId != null && sellerId > 0) {
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
                if (success) {
                    val resultCode = (o as BaseModel<*>).resultCode
                    if (resultCode == ResultCode.SUCCESS.flag) {
                        val cart = o.data as Cart

                        // [Tracking] 장바구니 담기
                        Tracker.Event(TrackingEvent.Cart.Add_To_Cart.eventName).let {
                            it.addCustom("dealId", cart.dealId)
                            it.addCustom("sellerId", cart.sellerId)
                            it.addCustom("brandName", cart.brandName)
                            it.addCustom("dealName", cart.dealName)
                            it.addCustom("sellPrice", cart.sellPrice.toString())
                            it.addCustom("discountPrice", cart.discountPrice.toString())
                            if (!TextUtils.isEmpty(cart.season)) it.addCustom("season", cart.season)

//                            it.addCustom("dealId", product.value?.dealId.toString())
//                            it.addCustom("productId", product.value?.productId.toString())
//                            it.addCustom("brandId", product.value?.brandId.toString())
//                            it.addCustom("sellerId", product.value?.sellerId.toString())
//                            it.addCustom("season", product.value?.season ?: cart.season)
//                            it.addCustom("name", product.value?.name ?: cart.dealName)
//                            it.addCustom("sellPrice", product.value?.sellPrice.toString())
//                            it.addCustom("discountPrice", product.value?.discountPrice.toString())

                            TrackingUtil.sendKochavaEvent(it)
                        }

                        if (cart.cartValidStatus.status) {
                            listener?.showAddCartResult()
                            CommonUtil.getCartItemCount()
                            //BaseApplication.getInstance().plusCartCount()
                            mSetBadgeTask()
                        } else {
                            ToastUtil.showMessage(cart.cartValidStatus.cartErrorMessage)
                        }
                    } else {
                        ToastUtil.showMessage(o.message ?: "장바구니 담기 오류")
                        listener?.closeActivity()
                    }
                } else {
                    val json = JsonParser().parse(o as String)
                    val model = Gson().fromJson(json, BaseModel::class.java)
                    ToastUtil.showMessage(model.message ?: "장바구니 담기 오류")
                    listener?.closeActivity()
                }
                listener?.hideLoadingIndicator()
            }, accessToken = accessToken, dealId = dealId, dealOptionId = listener?.getSelectedOptionDealId(), quantity = listener?.getSelectedProductQuantity()!!)
        }, invalidTokenTask = {
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
            listener?.redirectLoginActivity()
        })
    }

    private fun saveBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    var bookMarkResponse = BookMarkResponse(target, targetId)
                    UserServer.saveBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    mIsBookMarkSaved = ObservableBoolean(true)
                                    notifyPropertyChanged(BR.mIsBookMarkSaved)

                                    if (CustomLog.flag) CustomLog.L("saveBookMark", "successTask")

                                    when (target) {
                                        BookMarkTarget.SELLER.target -> {
                                            this.mSellerBookMark.content = mutableListOf(BookMark().Content().apply {
                                                this.target = target
                                                this.targetId = targetId
                                            })
                                            notifyPropertyChanged(BR.mSellerBookMark)
                                            notifySellerStoreFollow(mSellerBookMark)
                                        }
                                    }
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, response = bookMarkResponse.getProductBookMarkRespose())
                }, invalidTokenTask = { ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin)) })
    }

    private fun deleteBookMark(target: String, targetId: Long) {
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    mIsBookMarkSaved = ObservableBoolean(false)
                                    notifyPropertyChanged(BR.mIsBookMarkSaved)

                                    if (CustomLog.flag) CustomLog.L("deleteBookMark", "successTask")

                                    when (target) {
                                        BookMarkTarget.SELLER.target -> {
                                            this.mSellerBookMark.content = mutableListOf()
                                            notifyPropertyChanged(BR.mSellerBookMark)
                                            notifySellerStoreFollow(mSellerBookMark)
                                        }
                                    }
                                },
                                dataNotFoundTask = { },
                                failedTask = { },
                                userLikeNotFoundTask = { },
                                serverRuntimeErrorTask = { }
                        )
                    }, accessToken = it, target = target, targetId = targetId)
                }, invalidTokenTask = { })
    }


    fun onClickBookMark() {
        if (initBookMarkData) {
            if (productBookMark.get())
                deleteBookMark(Type.BookMarkTarget.PRODUCT.name, product.value!!.productId)
            else
                saveBookMark(Type.BookMarkTarget.PRODUCT.name, product.value!!.productId)
            productBookMark.set(!productBookMark.get())
        } else
            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.login_message_requiredlogin))
    }

    fun onClickSearch() = listener?.showSearchWordActivity()

    fun onClickShoppingBag() = listener?.showShoppingBagActivity()

    fun onClickReport() = listener?.showReportActivity()

}