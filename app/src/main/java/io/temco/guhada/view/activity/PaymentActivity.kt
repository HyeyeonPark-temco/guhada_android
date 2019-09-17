package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.PaymentWayType
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.order.PaymentMethod
import io.temco.guhada.data.model.order.RequestOrder
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.model.shippingaddress.ShippingMessage
import io.temco.guhada.data.viewmodel.payment.PaymentViewModel
import io.temco.guhada.databinding.ActivityPaymentBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
import io.temco.guhada.view.adapter.payment.PaymentProductAdapter
import io.temco.guhada.view.adapter.payment.PaymentSpinnerAdapter
import io.temco.guhada.view.adapter.payment.PaymentWayAdapter

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

        // 결제수단
        initPaymentWay()

        // 현금영수증
        initRecipient()

        // 상품 리스트
        mBinding.recyclerviewPaymentProduct.adapter = PaymentProductAdapter()

        mBinding.includePaymentDiscount.viewModel = mViewModel
        mBinding.includePaymentDiscountresult.viewModel = mViewModel
        mBinding.includePaymentPaymentway.viewModel = mViewModel
        mBinding.viewModel = mViewModel
        mBinding.includePaymentPaymentway.setPurchaseClickListener { CommonUtilKotlin.startTermsPurchase(this@PaymentActivity) }
        mBinding.executePendingBindings()
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
            this.mVerifyTask = {
                val intent = Intent(this@PaymentActivity, VerifyActivity::class.java)
                intent.putExtra("user", mViewModel.order.user)
                startActivityForResult(intent, RequestCode.VERIFY.flag)
            }
        }
        // [장바구니]에서 진입
        if (intent.getSerializableExtra("productList") != null) {
            mViewModel.productList = intent.getSerializableExtra("productList") as ArrayList<BaseProduct>
        }
        if (intent.getSerializableExtra("cartIdList") != null) {
            mViewModel.cartIdList = (intent.getSerializableExtra("cartIdList") as Array<Int>).toMutableList()
            mViewModel.totalCount = ObservableInt(mViewModel.cartIdList.size)
        }

        // [바로구매]에서 진입
        mViewModel.quantity = intent.getIntExtra("quantity", 1)
        mViewModel.purchaseOrderResponse.observe(this@PaymentActivity, Observer {
            // 주문 완료 페이지 이동
            mLoadingIndicatorUtil.hide()
            Intent(this@PaymentActivity, PaymentResultActivity::class.java).let { intent ->
                intent.putExtra("purchaseOrderResponse", mViewModel.purchaseOrderResponse.value)
                intent.putExtra("shippingMemo", mViewModel.selectedShippingMessage.get()?.message)
                intent.putExtra("userName", mViewModel.order.user.name ?: "")
                startActivity(intent)
                finish()
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
                }
            }
        })
    }

    private fun initDiscountCouponView() {
        // COUPON
        mBinding.includePaymentDiscount.buttonPaymentDiscountcoupon.setOnClickListener {
            //
//            val map = hashMapOf<Long, String>()
//            for (product in mViewModel.productList)
//                map[product.dealId] = product.optionStr

            val intent = Intent(this@PaymentActivity, CouponSelectDialogActivity::class.java)
            intent.putExtra("productList", mViewModel.productList)
//            intent.putExtra("optionMap", map)
            intent.putExtra("cartIdList", mViewModel.cartIdList.toIntArray())
            startActivityForResult(intent, RequestCode.COUPON_SELECT.flag)
        }
    }

    // 결제 수단
    private fun initPaymentWay() {
        mPaymentWayAdapter = PaymentWayAdapter()
        mPaymentWayAdapter.mViewModel = mViewModel
        mPaymentWayAdapter.setItems(mViewModel.order.paymentsMethod)
        mBinding.includePaymentPaymentway.recyclerviewPaymentWay.adapter = mPaymentWayAdapter
    }

    // 현금영수증
    private fun initRecipient() {
        // 신청, 미신청 체크박스
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptissue.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.includePaymentPaymentway.checkboxPaymentReceiptunissue.isChecked = !isChecked
            mBinding.includePaymentPaymentway.constraintlayoutPaymentRecipientform.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptunissue.setOnCheckedChangeListener { buttonView, isChecked ->
            mBinding.includePaymentPaymentway.checkboxPaymentReceiptissue.isChecked = !isChecked
            mBinding.includePaymentPaymentway.constraintlayoutPaymentRecipientform.visibility = if (!isChecked) View.VISIBLE else View.GONE

            mViewModel.mRequestOrder.cashReceiptType = ""
            mViewModel.mRequestOrder.cashReceiptNo = ""
            mViewModel.mRequestOrder.cashReceiptUsage = ""
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
        val personalTypeList = listOf(RequestOrder.CashReceiptType.MOBILE.label)
        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.adapter = CommonSpinnerAdapter(context = this@PaymentActivity, layoutRes = R.layout.item_common_spinner, list = personalTypeList)
        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedType = personalTypeList[position]
                mBinding.includePaymentPaymentway.textviewPaymentPersonaltype.text = selectedType
                mViewModel.mRecipientByPhone = ObservableBoolean(selectedType == RequestOrder.CashReceiptType.MOBILE.label)
                mViewModel.notifyPropertyChanged(BR.mRecipientByPhone)

                mViewModel.mRequestOrder.cashReceiptType = when (selectedType) {
                    RequestOrder.CashReceiptType.MOBILE.label -> RequestOrder.CashReceiptType.MOBILE.code
                    RequestOrder.CashReceiptType.CARD.label -> RequestOrder.CashReceiptType.CARD.code
                    else -> ""
                }
            }
        }

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

        mBinding.includePaymentPaymentway.spinnerPaymentPersonaltype.setSelection(0)
        mBinding.includePaymentPaymentway.checkboxPaymentReceiptpersonal.isChecked = true

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Flag.RequestCode.LOGIN -> {
                if (resultCode == Activity.RESULT_OK) {
                    mViewModel.callWithToken { accessToken ->
                        mViewModel.addCartItem(accessToken)
                    }
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
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.payment_message_cancel))
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
                            ?: false
                    val emailVerification = data?.getBooleanExtra("emailVerification", false)
                            ?: false
                    mBinding.linearlayoutPaymentVerify.visibility = if (mobileVerification && emailVerification) View.GONE else View.VISIBLE
                    mBinding.executePendingBindings()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("userShippingMemo")
        fun Spinner.bindShippingAddress(list: MutableList<ShippingMessage>) {
            if (list.isNotEmpty()) {
                if (list[list.size - 1].message != resources.getString(R.string.payment_hint_shippingmemo))
                    list.add(ShippingMessage().apply { this.message = resources.getString(R.string.payment_hint_shippingmemo) })

                if (this.adapter == null) {
                    this.adapter = PaymentSpinnerAdapter(BaseApplication.getInstance().applicationContext, R.layout.item_payment_spinner, list)
                } else {
                    (this.adapter as PaymentSpinnerAdapter).setItems(list)
                }
                this.setSelection(list.size - 1)
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
        @BindingAdapter("paymentProduct")
        fun RecyclerView.bindPaymentProduct(list: ArrayList<BaseProduct>?) {
            if (this.adapter != null && list != null) {
                (this.adapter as PaymentProductAdapter).setItems(list)
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