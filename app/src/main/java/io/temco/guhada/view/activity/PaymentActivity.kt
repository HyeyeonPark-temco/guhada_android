package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.reflect.TypeToken
import com.kochava.base.Tracker
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PaymentWayType
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.common.util.TrackingUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.order.OrderItemResponse
import io.temco.guhada.data.model.order.PaymentMethod
import io.temco.guhada.data.model.order.RequestOrder
import io.temco.guhada.data.model.payment.CalculatePaymentInfo
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.point.PointProcessParam
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.viewmodel.payment.PaymentViewModel
import io.temco.guhada.databinding.ActivityPaymentBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
import io.temco.guhada.view.adapter.payment.PaymentOrderItemAdapter
import io.temco.guhada.view.adapter.payment.PaymentWayAdapter
import io.temco.guhada.view.custom.CustomSpinnerView
import org.json.JSONArray

/**
 * 주문 결제 화면
 * @author Hyeyeon Park
 */
class PaymentActivity : BindActivity<ActivityPaymentBinding>() {
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var mViewModel: PaymentViewModel
    private lateinit var mPaymentWayAdapter: PaymentWayAdapter

    override fun getBaseTag(): String = PaymentActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_payment

    override fun getViewType(): Type.View = Type.View.PAYMENT

    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this@PaymentActivity)
        mLoadingIndicatorUtil.show()

        initViewModel()
        initHeader()
        initDiscountCouponView()
        initDiscountPointView()

        // [장바구니]에서 진입 시, 상품 정보
        setProductFromCart()

        // [바로구매]에서 진입 시, 상품 정보
        setProductFromNowBuy()

        // 결제수단
        initPaymentWay()

        // 현금영수증
        initReceipt()

        // 적립 예정 포인트
        // initDueSavePoint()

        // 하단 결제 금액
        initCalculatePaymentInfo()

        // 배송메세지
        initShippingMessage()

        // 사용 가능 쿠폰 정보
        initCouponInfo()

        // 상품 리스트
        mBinding.recyclerviewPaymentProduct.adapter = PaymentOrderItemAdapter()
        mBinding.includePaymentDiscount.viewModel = mViewModel
        mBinding.includePaymentDiscountresult.viewModel = mViewModel
        mBinding.includePaymentPaymentway.viewModel = mViewModel
        mBinding.includePaymentEtcmessage.viewModel = mViewModel
        mBinding.includePaymentPaymentway.setPurchaseClickListener { CommonUtilKotlin.startTermsPurchase(this@PaymentActivity) }
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initShippingMessage() {
        mBinding.spinnerPaymentShippingmemo.setOnItemClickTask { position ->
            val selectedShippingMessage = mViewModel.shippingMessages[position]
            mViewModel.selectedShippingMessage = ObservableField(ShippingMessage().apply {
                this.message = selectedShippingMessage.message
                this.type = selectedShippingMessage.type
            })
            mViewModel.shippingMemoVisible = ObservableBoolean(position == mViewModel.shippingMessages.size - 1)
            mViewModel.notifyPropertyChanged(BR.shippingMemoVisible)
        }
    }

    override fun onPause() {
        super.onPause()
        mLoadingIndicatorUtil.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLoadingIndicatorUtil.dismiss()
    }

    private fun initViewModel() {
        mViewModel = PaymentViewModel(object : OnPaymentListener {
            override fun onClickCloseShippingMemoSpinner() {

            }

            override fun redirectShippingAddressActivity() {
                Intent(this@PaymentActivity, ShippingAddressActivity::class.java).let {
                    it.putExtra("shippingAddress", mViewModel.order.shippingAddress)
                    startActivityForResult(it, Flag.RequestCode.SHIPPING_ADDRESS)
                }
            }

            override fun setUsedPointViewFocused() {
                mBinding.includePaymentDiscount.edittextPaymentDiscountpoint.requestFocus()
            }

            override fun showMessage(message: String) {
                ToastUtil.showMessage(message)
            }

            override fun notifyRadioButtons() {
                (mBinding.includePaymentPaymentway.recyclerviewPaymentWay.adapter as PaymentWayAdapter).notifyDataSetChanged()

                // 현금영수증 노출 여부
                when (mPaymentWayAdapter.getPaymentWay()) {
                    PaymentWayType.VBANK.code,
                    PaymentWayType.DIRECT_BANK.code -> {
                        mViewModel.mRecipientAvailable = ObservableBoolean(true)
                        mViewModel.notifyPropertyChanged(BR.mRecipientAvailable)
                    }
                    else -> {
                        mViewModel.mRecipientAvailable = ObservableBoolean(false)
                        mViewModel.notifyPropertyChanged(BR.mRecipientAvailable)
                    }
                }
            }

            override fun redirectPaymentWebViewActivity() {
                Intent(this@PaymentActivity, PaymentWebViewActivity::class.java).let {
                    it.putExtra("payMethod", mViewModel.selectedMethod.methodCode)
                    it.putExtra("pgResponse", mViewModel.pgResponse)
                    startActivityForResult(it, Flag.RequestCode.PAYMENT_WEBVIEW)
                }
            }

            override fun redirectLoginActivity() {
                Intent(this@PaymentActivity, LoginActivity::class.java).let {
                    this@PaymentActivity.startActivityForResult(it, Flag.RequestCode.LOGIN)
                }
            }

            override fun hideLoadingIndicator() {
                mLoadingIndicatorUtil.hide()
            }

            override fun closeActivity() {
                finish()
            }
        }).apply {
            this.mVerifyTask = { this@PaymentActivity.redirectVerifyActivity() }
        }
    }

    private fun setProductFromCart() {
        if (intent.getSerializableExtra("productList") != null) {
            mViewModel.productList = intent.getSerializableExtra("productList") as ArrayList<BaseProduct>
        }
        if (intent.getSerializableExtra("cartIdList") != null) {
            mViewModel.cartIdList = (intent.getSerializableExtra("cartIdList") as Array<Int>).toMutableList()
            mViewModel.totalCount = ObservableInt(mViewModel.cartIdList.size)
        }
    }

    private fun setProductFromNowBuy() {
        mViewModel.quantity = intent.getIntExtra("quantity", 1)
        mViewModel.purchaseOrderResponse.observe(this@PaymentActivity, Observer {
            // 주문 완료 페이지 이동

            for (item in it.orderList)
            // [Tracking] 결제 성공
                Tracker.Event(TrackingEvent.Product.Buy_Product.eventName).let { event ->
                    event.addCustom("dealId", item.dealId.toString())
                    event.addCustom("sellerId", item.sellerId.toString())
                    event.addCustom("brandName", item.brandName)
                    event.addCustom("productName", item.productName)
                    event.addCustom("originalPrice", item.originalPrice.toString())
                    event.addCustom("discountPrice", item.discountPrice.toString())
                    if (!TextUtils.isEmpty(item.season)) event.addCustom("season", item.season)
                    TrackingUtil.sendKochavaEvent(event)
                }


            mLoadingIndicatorUtil.hide()
            Intent(this@PaymentActivity, PaymentResultActivity::class.java).let { intent ->
                intent.putExtra("purchaseOrderResponse", mViewModel.purchaseOrderResponse.value)
                intent.putExtra("shippingMemo", mViewModel.selectedShippingMessage.get()?.message)
                intent.putExtra("userName", mViewModel.order.user.name ?: "")

                // 할인 내역
                intent.putExtra("totalDiscountPrice", mViewModel.mTotalDiscountPrice.get())
                intent.putExtra("couponDiscountPrice", mViewModel.mCouponDiscountPrice)
                intent.putExtra("usedPoint", mViewModel.usedPointNumber.toInt())

                // 적립 예정 포인트
                intent.putExtra("expectedPoint", mViewModel.mExpectedPoint.value)
                intent.putExtra("calculatePaymentInfo", mViewModel.mCalculatePaymentInfo.value)

                this@PaymentActivity.startActivityForResult(intent, RequestCode.PAYMENT_RESULT.flag)
                // finish()
            }
        })

        intent.getSerializableExtra("product").let { product ->
            if (::mViewModel.isInitialized) {
                if (product != null) {
                    // [바로구매]에서 진입
                    mViewModel.product = product as BaseProduct
                    mViewModel.productList = arrayListOf(product)
                    mViewModel.totalCount = ObservableInt(product.totalCount)
                } else {
                    // [장바구니]에서 진입
                    mViewModel.callWithToken { accessToken -> mViewModel.getOrderForm(accessToken) }
                }
            }
        }
    }


    private fun initCalculatePaymentInfo() {
        mBinding.includePaymentDiscountresult.couponDiscount = 0
        mBinding.includePaymentDiscountresult.productDiscount = 0
        mBinding.includePaymentDiscountresult.reviewPoint = 0
        mBinding.includePaymentDiscountresult.buyPoint = 0

        mViewModel.mCalculatePaymentInfo.observe(this, Observer {
            mBinding.includePaymentDiscountresult.item = it
            if (it.totalDueSavePoint > 0)
                setDiscountPriceAndDueSavePoint(it)
        })
    }

    private fun setDiscountPriceAndDueSavePoint(info: CalculatePaymentInfo) {
        var couponDiscount = 0
        var productDiscount = 0
        var reviewPoint = 0
        var buyPoint = 0
        var firstOrderPoint = 0

        for (item in info.discountInfoResponseList) {
            when (item.discountType) {
                CalculatePaymentInfo.DiscountInfoResponse.DiscountType.PRODUCT_DISCOUNT.type -> productDiscount = item.discountPrice
                CalculatePaymentInfo.DiscountInfoResponse.DiscountType.COUPON_DISCOUNT.type -> couponDiscount = item.discountPrice
            }
        }

        for (item in info.totalDueSavePointResponseList) {
            when (item.dueSaveType) {
                PointProcessParam.PointSave.REVIEW.type -> reviewPoint = item.totalPoint
                PointProcessParam.PointSave.BUY.type -> buyPoint = item.totalPoint
                PointProcessParam.PointSave.FIRST_ORDER.type -> firstOrderPoint = item.totalPoint
            }
        }

        mBinding.includePaymentDiscountresult.couponDiscount = couponDiscount
        mBinding.includePaymentDiscountresult.productDiscount = productDiscount
        mBinding.includePaymentDiscountresult.reviewPoint = reviewPoint
        mBinding.includePaymentDiscountresult.buyPoint = buyPoint
        mBinding.includePaymentDiscountresult.firstOrderPoint = firstOrderPoint

        mBinding.includePaymentDiscountresult.constraintlayoutDiscountresultCoupondiscount.visibility = if (couponDiscount > 0) View.VISIBLE else View.GONE
        mBinding.includePaymentDiscountresult.constraintlayoutDiscountresultProductdiscount.visibility = if (productDiscount > 0) View.VISIBLE else View.GONE
        mBinding.includePaymentDiscountresult.linearlayoutDiscountresultBuypoint.visibility = if (buyPoint > 0) View.VISIBLE else View.GONE
        mBinding.includePaymentDiscountresult.linearlayoutDiscountresultReviewpoint.visibility = if (reviewPoint > 0) View.VISIBLE else View.GONE

        mBinding.includePaymentDiscountresult.executePendingBindings()
    }


    private fun initHeader() {
        mBinding.includePaymentHeader.title = BaseApplication.getInstance().getString(R.string.payment_title_header)
        mBinding.includePaymentHeader.setOnClickBackButton {
            setResult(Activity.RESULT_CANCELED)
            this.finish()
        }
    }

    private fun initDiscountPointView() {
        val editText = mBinding.includePaymentDiscount.edittextPaymentDiscountpoint
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.isNotBlank() == true) {
                    val chars = s.split(",")
                    if (chars.size < 2) {
                        mViewModel.usedPointNumber = s.toString().toLong()
                    } else {
                        var str = ""
                        for (c in chars) {
                            str += c
                        }
                        mViewModel.usedPointNumber = str.toLong()
                    }
                    mViewModel.notifyPropertyChanged(BR.usedPoint)
                    editText.setSelection(editText.length())
                } else {
                    mViewModel.usedPointNumber = 0
                    mViewModel.usedPoint = ObservableField("0")
                    mViewModel.notifyPropertyChanged(BR.usedPoint)
                }
            }
        })
    }

    private fun initDiscountCouponView() {
        mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.setOnClickListener { redirectCouponSelectDialogActivity() }
        mBinding.includePaymentDiscountresult.imageviewPaymentChangecoupon.setOnClickListener { redirectCouponSelectDialogActivity() }
    }

    private fun redirectCouponSelectDialogActivity() {
        val intent = Intent(this@PaymentActivity, CouponSelectDialogActivity::class.java)
        intent.putExtra("couponInfo", mViewModel.mCouponInfo.value)
        startActivityForResult(intent, RequestCode.COUPON_SELECT.flag)
    }

    private fun initPaymentWay() {
        mPaymentWayAdapter = PaymentWayAdapter()
        mPaymentWayAdapter.mViewModel = mViewModel
        mPaymentWayAdapter.setItems(mViewModel.order.paymentsMethod)
        mBinding.includePaymentPaymentway.recyclerviewPaymentWay.adapter = mPaymentWayAdapter
        mBinding.includePaymentPaymentway.textviewPaymentCardinterest.setOnClickListener {
            startActivity(Intent(this@PaymentActivity, CardInterestActivity::class.java))
        }
    }

    private fun initReceipt() {
        // 신청 체크박스
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptissue.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mIsRecipientIssued = true
                if (mBinding.includePaymentPaymentway.checkboxPaymentReceiptpersonal.isChecked) {
                    mViewModel.mRequestOrder.cashReceiptUsage = RequestOrder.CashReceiptUsage.PERSONAL.code
                    mViewModel.mRequestOrder.cashReceiptType = RequestOrder.CashReceiptType.MOBILE.code
                } else {
                    mViewModel.mRequestOrder.cashReceiptUsage = RequestOrder.CashReceiptUsage.BUSINESS.code
                    mViewModel.mRequestOrder.cashReceiptType = RequestOrder.CashReceiptType.BUSINESS.code
                }
            }
            mBinding.includePaymentPaymentway.checkboxPaymentReceiptunissue.isChecked = !isChecked
            mBinding.includePaymentPaymentway.constraintlayoutPaymentReceiptform.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // 신청 체크박스
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptunissue.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mViewModel.mIsRecipientIssued = false
                mViewModel.mRequestOrder.cashReceiptType = ""
                mViewModel.mRequestOrder.cashReceiptNo = ""
                mViewModel.mRequestOrder.cashReceiptUsage = ""
            }
            mBinding.includePaymentPaymentway.checkboxPaymentReceiptissue.isChecked = !isChecked
            mBinding.includePaymentPaymentway.constraintlayoutPaymentReceiptform.visibility = if (!isChecked) View.VISIBLE else View.GONE
        }

        // 개인소득공제용
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptpersonal.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mBinding.includePaymentPaymentway.checkboxPaymentCorporation.isChecked = !isChecked
                mBinding.includePaymentPaymentway.constraintlayoutPaymentPersonal.visibility = View.VISIBLE
                mBinding.includePaymentPaymentway.constraintlayoutPaymentCorporation.visibility = View.GONE
                mViewModel.mRequestOrder.cashReceiptUsage = RequestOrder.CashReceiptUsage.PERSONAL.code
                mViewModel.mRequestOrder.cashReceiptType = RequestOrder.CashReceiptType.MOBILE.code
            }
        }

        // 사업자증빙용
        mBinding.includePaymentPaymentway.checkboxPaymentCorporation.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mBinding.includePaymentPaymentway.checkboxPaymentReceiptpersonal.isChecked = !isChecked
                mBinding.includePaymentPaymentway.constraintlayoutPaymentPersonal.visibility = View.GONE
                mBinding.includePaymentPaymentway.constraintlayoutPaymentCorporation.visibility = View.VISIBLE
                mViewModel.mRequestOrder.cashReceiptUsage = RequestOrder.CashReceiptUsage.BUSINESS.code
                mViewModel.mRequestOrder.cashReceiptType = RequestOrder.CashReceiptType.BUSINESS.code
            }
        }

        // 개인소득공제용 방식 스피너
        // 주민번호 삭제 [2019.09.10]
//        val personalTypeList = listOf(RequestOrder.CashReceiptType.MOBILE.label)
//        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.adapter = CommonSpinnerAdapter(context = this@PaymentActivity, layoutRes = R.layout.item_common_spinner, list = personalTypeList)
//        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                val selectedType = personalTypeList[position]
//                mBinding.includePaymentPaymentway.textviewPaymentPersonaltype.text = selectedType
//                mViewModel.mRecipientByPhone = ObservableBoolean(selectedType == RequestOrder.CashReceiptType.MOBILE.label)
//                mViewModel.notifyPropertyChanged(BR.mRecipientByPhone)
//
//                mViewModel.mRequestOrder.cashReceiptType = when (selectedType) {
//                    RequestOrder.CashReceiptType.MOBILE.label -> RequestOrder.CashReceiptType.MOBILE.code
//                    RequestOrder.CashReceiptType.CARD.label -> RequestOrder.CashReceiptType.CARD.code
//                    else -> ""
//                }
//            }
//        }

        // 핸드폰 번호 스피너
        val phoneList = listOf("010", "011", "016", "017", "019")
        mBinding.includePaymentPaymentway.spinnerPaymentPhone.adapter = CommonSpinnerAdapter(context = this@PaymentActivity, layoutRes = R.layout.item_common_spinner, list = phoneList)
        mBinding.includePaymentPaymentway.spinnerPaymentPhone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mBinding.includePaymentPaymentway.textviewPaymentPhone.text = phoneList[position]
                mViewModel.mRecipientPhone1 = phoneList[position]
            }
        }

//        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.setSelection(0)
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptpersonal.isChecked = true

    }

    // 사용 가능 쿠폰, 포인트 조회 (미사용)
    private fun initCouponInfo() {
        mViewModel.mCouponInfo.observe(this, Observer {
            mBinding.includePaymentDiscount.textviewPaymentDiscountcouponcount.text = Html.fromHtml(String.format(getString(R.string.payment_couponcount_format), it.availableCouponCount, it.savedCouponCount))
            mBinding.includePaymentDiscount.textviewPaymentDiscountcoupon.text = Html.fromHtml(String.format(getString(R.string.payment_coupon_format), it.totalCouponDiscountPrice, it.selectedCouponCount))

            if (it.availableCouponCount > 0) {
                mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.isClickable = true
                mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.setTextColor(resources.getColor(R.color.black_four))
            } else {
                mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.isClickable = false
                mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.setTextColor(resources.getColor(R.color.pinkish_grey))
            }

            val list = mutableListOf<RequestOrder.CartItemPayment>()
            for (seller in it.benefitSellerResponseList) {
                for (deal in seller.benefitOrderProductResponseList) {
                    val item =
                            RequestOrder.CartItemPayment().apply {
                                this.cartItemId = deal.cartId
                            }

                    for (coupon in deal.benefitProductCouponResponseList)
                        if (coupon.selected) {
                            item.couponNumber = coupon.couponNumber
                            break
                        }
                    list.add(item)
                }
            }
            mViewModel.mRequestOrder.cartItemPayments = list
        })

    }

    private fun initDueSavePoint() {
        mViewModel.mExpectedPoint.observe(this, Observer {
            var dusSaveTotalPoint = 0
            for (item in it.dueSavePointList) {
                dusSaveTotalPoint += item.freePoint
                when (item.pointType) {
                    PointProcessParam.PointSave.BUY.type -> mBinding.includePaymentDiscountresult.textviewPaymentBuypoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                    PointProcessParam.PointSave.TEXT_REVIEW.type -> mBinding.includePaymentDiscountresult.textviewPaymentTextreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                    PointProcessParam.PointSave.IMG_REVIEW.type -> mBinding.includePaymentDiscountresult.textviewPaymentPhotoreviewpoint.text = String.format(getString(R.string.common_price_format), item.freePoint)
                }
            }
            mBinding.includePaymentDiscountresult.textviewPaymentExpectedtotalpoint.text = String.format(getString(R.string.common_price_format), dusSaveTotalPoint)
        })
    }

    private fun redirectVerifyActivity() {
        val intent = Intent(this@PaymentActivity, VerifyActivity::class.java)
        intent.putExtra("user", mViewModel.order.user)
        intent.putExtra("mobileVerification", mViewModel.mMobileVerification.get())
        intent.putExtra("emailVerification", mViewModel.mEmailVerification.get())
        startActivityForResult(intent, RequestCode.VERIFY.flag)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Flag.RequestCode.LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    mViewModel.addCartItem()
                } else {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            Flag.RequestCode.PAYMENT_WEBVIEW -> {
                if (resultCode == Activity.RESULT_OK) {
                    mLoadingIndicatorUtil.show()
                    val pgAuth = data?.getSerializableExtra("pgAuth")
                    if (pgAuth != null) {
                        mViewModel.pgAuth = pgAuth as PGAuth
                        mViewModel.setOrderApproval()
                    }
                } else {
                    val resultMessage = data?.getStringExtra("resultMessage")
                    val message = if (!resultMessage.isNullOrEmpty()) resultMessage else BaseApplication.getInstance().getString(R.string.payment_message_cancel)
                    ToastUtil.showMessage(message)
                }
            }
            Flag.RequestCode.SHIPPING_ADDRESS -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.getSerializableExtra("shippingAddress").let {
                        if (it != null) {
                            mViewModel.order.shippingAddress = it as UserShipping
                            mViewModel.selectedShippingAddress = it
                            mViewModel.notifyPropertyChanged(BR.order)
                            mViewModel.notifyPropertyChanged(BR.shippingAddressText)
                        }
                    }
                } else {
                    // 배송지 변경 취소
                }
            }
            RequestCode.VERIFY.flag -> {
                if (resultCode == Activity.RESULT_OK) {
                    val mobileVerification = data?.getBooleanExtra("mobileVerification", false)
                    val emailVerification = data?.getBooleanExtra("emailVerification", false)
                    val name = data?.getStringExtra("name")
                    val phoneNumber = data?.getStringExtra("phoneNumber")
                    val di = data?.getStringExtra("di")
                    val gender = data?.getStringExtra("gender")

                    if (!mViewModel.mMobileVerification.get()) {
                        if (name != null && phoneNumber != null) {
                            mViewModel.mRequestOrder.user.name = name
                            mViewModel.mRequestOrder.user.mobile = phoneNumber
                            mViewModel.mRequestOrder.user.phoneNumber = phoneNumber

                            mViewModel.order.user.name = name
                            mViewModel.order.user.mobile = phoneNumber
                            mViewModel.order.user.phoneNumber = phoneNumber

                            mBinding.viewModel = mViewModel
                        }

                        mViewModel.mMobileVerification = ObservableBoolean(mobileVerification
                                ?: false)
                        mViewModel.notifyPropertyChanged(BR.mMobileVerification)
                    }

                    mViewModel.mEmailVerification = ObservableBoolean(emailVerification ?: false)
                    mViewModel.notifyPropertyChanged(BR.mEmailVerification)

                    mBinding.linearlayoutPaymentVerify.visibility = if (mobileVerification == true /* && emailVerification ?: false */) View.GONE else View.VISIBLE
                    mBinding.executePendingBindings()
                }
            }
            RequestCode.COUPON_SELECT.flag -> {
                if (resultCode == Activity.RESULT_OK) {
                    val couponCount = data?.getIntExtra("couponCount", 0)
                    val discountPrice = data?.getIntExtra("discountPrice", 0)
                    data?.getStringExtra("selectedCouponArray").let { array ->
                        if (!array.isNullOrEmpty())
                            mViewModel.mSelectedCouponArray = Gson().fromJson(array, Array<RequestOrder.CartItemPayment>::class.java).toMutableList()
                    }
                    mBinding.includePaymentDiscount.textviewPaymentDiscountcoupon.text = Html.fromHtml(String.format(getString(R.string.payment_coupon_format), discountPrice, couponCount))
                }
            }
            RequestCode.PAYMENT_RESULT.flag -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("shippingMessage")
        fun CustomSpinnerView.bindShippingMessages(list: MutableList<ShippingMessage>?) {
            this.setPlaceHolder(resources.getString(R.string.payment_hint_shippingmemo))
            if (!list.isNullOrEmpty()) {
                val mList: MutableList<Any> = mutableListOf()
                for (item in list) {
                    mList.add(item.message)
                }
                this.setItems(mList)
            }
        }

        @JvmStatic
        @BindingAdapter("paymentWay")
        fun RecyclerView.bindPaymentWay(list: MutableList<PaymentMethod>?) {
            if (this.adapter != null && list != null) {
                (this.adapter as PaymentWayAdapter).setItems(list)
            }
        }

        @JvmStatic
        @BindingAdapter("orderItem")
        fun RecyclerView.bindPaymentOrderItem(list: List<OrderItemResponse>?) {
            if (this.adapter != null && list != null) {
                (this.adapter as PaymentOrderItemAdapter).setItems(list.toMutableList())
            }
        }
    }

    interface OnPaymentListener {
        fun setUsedPointViewFocused()
        fun showMessage(message: String)
        fun notifyRadioButtons()
        fun redirectPaymentWebViewActivity()
        fun redirectLoginActivity()
        fun hideLoadingIndicator()
        fun redirectShippingAddressActivity()
        fun onClickCloseShippingMemoSpinner()
        fun closeActivity()
    }
}