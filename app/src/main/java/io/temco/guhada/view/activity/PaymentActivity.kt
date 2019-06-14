package io.temco.guhada.view.activity

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.BaseProduct
import io.temco.guhada.data.viewmodel.PaymentViewModel
import io.temco.guhada.databinding.ActivityPaymentBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.PaymentSpinnerAdapter

class PaymentActivity : BindActivity<ActivityPaymentBinding>() {
    private lateinit var mViewModel: PaymentViewModel

    override fun getBaseTag(): String = PaymentActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_payment

    override fun getViewType(): Type.View = Type.View.PAYMENT

    override fun init() {
        mViewModel = PaymentViewModel(object : OnPaymentListener {
            override fun setUsedPointViewFocused() {
                mBinding.includePaymentDiscount.edittextPaymentDiscountpoint.requestFocus()
            }
        })
        intent.getSerializableExtra("product").let { product ->
            if (product != null) {
                mViewModel.product = product as BaseProduct
            }
        }
        if (::mViewModel.isInitialized) {
            mBinding.includePaymentHeader.title = "주문 결제"
            mBinding.includePaymentHeader.setOnClickBackButton {
                setResult(Activity.RESULT_CANCELED)
                this.finish()
            }

            initDiscountView()
            initShippingMemoView()

            mBinding.includePaymentDiscount.viewModel = mViewModel
            mBinding.includePaymentDiscountresult.viewModel = mViewModel
            mBinding.includePaymentPaymentway.viewModel = mViewModel
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    private fun initDiscountView() {
        // POINT
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

        // COUPON
        val emptyMessage = resources.getString(R.string.payment_hint_coupon)
        val items = listOf<String>("장바구니 3,000원 할인쿠폰", "선착순 5% 할인쿠폰", "웰컴 5,000원 할인쿠폰", emptyMessage)
        mBinding.includePaymentDiscount.spinnerPaymentShippingmemo.adapter = PaymentSpinnerAdapter(this@PaymentActivity, R.layout.item_payment_spinner, items)
        mBinding.includePaymentDiscount.spinnerPaymentShippingmemo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mViewModel.selectedDiscountCoupon = ObservableField(items[position])
                mViewModel.notifyPropertyChanged(BR.selectedDiscountCoupon)
            }
        }
        mBinding.includePaymentDiscount.spinnerPaymentShippingmemo.setSelection(items.size - 1)
    }

    private fun initShippingMemoView() {
        val emptyMessage = resources.getString(R.string.payment_hint_shippingmemo)
        val items = listOf<String>("부재 시 경비실에 연락주세요.", " 부재 시 문 앞에 놓아주세요 .", "배송 전에 연락주세요.", emptyMessage)
        mBinding.spinnerPaymentShippingmemo.adapter = PaymentSpinnerAdapter(this@PaymentActivity, R.layout.item_payment_spinner, items)
        mBinding.spinnerPaymentShippingmemo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mViewModel.selectedShippingMessage = ObservableField(items[position])
                mViewModel.notifyPropertyChanged(BR.selectedShippingMessage)
            }
        }
        mBinding.spinnerPaymentShippingmemo.setSelection(items.size - 1)
    }


    interface OnPaymentListener {
        fun setUsedPointViewFocused()
    }
}