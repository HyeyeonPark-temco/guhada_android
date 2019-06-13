package io.temco.guhada.view.activity

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.BaseProduct
import io.temco.guhada.data.viewmodel.PaymentViewModel
import io.temco.guhada.databinding.ActivityPaymentBinding
import io.temco.guhada.view.activity.base.BindActivity

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

            mBinding.includePaymentDiscount.viewModel = mViewModel
            mBinding.includePaymentDiscountresult.viewModel = mViewModel
            mBinding.includePaymentPaymentway.viewModel = mViewModel
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    private fun initDiscountView() {
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

    interface OnPaymentListener {
        fun setUsedPointViewFocused()
    }
}