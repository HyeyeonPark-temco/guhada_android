package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.BaseProduct
import io.temco.guhada.data.model.Order
import io.temco.guhada.data.viewmodel.PaymentViewModel
import io.temco.guhada.databinding.ActivityPaymentBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.PaymentSpinnerAdapter
import io.temco.guhada.view.adapter.PaymentWayAdapter

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

            override fun showMessage(message: String) {
                Toast.makeText(this@PaymentActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun notifyRadioButtons() {
                (mBinding.includePaymentPaymentway.recyclerviewPaymentWay.adapter as PaymentWayAdapter).notifyDataSetChanged()
            }

            override fun redirectPayemntWebViewActivity() {
                Intent(this@PaymentActivity, PaymentWebViewActivity::class.java).let {
                    it.putExtra("pgResponse", mViewModel.pgResponse)
                    startActivityForResult(it, Flag.RequestCode.PAYMENT_WEBVIEW)
                }
            }

            override fun redirectLoginActivity() {
                Intent(this@PaymentActivity, LoginActivity::class.java).let {
                    this@PaymentActivity.startActivityForResult(it, Flag.RequestCode.LOGIN)
                }
            }
        })
        mViewModel.quantity = intent.getIntExtra("quantity", 1)
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

            mBinding.includePaymentDiscount.viewModel = mViewModel
            mBinding.includePaymentDiscountresult.viewModel = mViewModel
            mBinding.includePaymentPaymentway.viewModel = mViewModel

            // 결제수단
            PaymentWayAdapter().let {
                it.mViewModel = mViewModel
                it.setItems(mViewModel.order.paymentsMethod)
                mBinding.includePaymentPaymentway.recyclerviewPaymentWay.adapter = it
            }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Flag.RequestCode.LOGIN -> {
                    mViewModel.callWithToken { accessToken ->
                        Log.e("AccessToken", accessToken)
                        mViewModel.addCartItem(accessToken)
                    }
                }
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

    }

    companion object {
        @JvmStatic
        @BindingAdapter("userShippingAddress")
        fun Spinner.bindShippingAddress(list: MutableList<String>) {
            if (list.isNotEmpty()) {
                val emptyMessage = resources.getString(R.string.payment_hint_shippingmemo)
                list.add(emptyMessage)

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
        fun RecyclerView.bindPaymentWay(list: MutableList<Order.PaymentMethod>?) {
            if (this.adapter != null && list != null) {
                (this.adapter as PaymentWayAdapter).setItems(list)
            }
        }
    }

    interface OnPaymentListener {
        fun setUsedPointViewFocused()
        fun showMessage(message: String)
        fun notifyRadioButtons()
        fun redirectPayemntWebViewActivity()
        fun redirectLoginActivity()
    }
}