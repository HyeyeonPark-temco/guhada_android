package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.data.model.order.PurchaseOrderResponse
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.PaymentResultActivity

class PaymentResultViewModel(val listener: PaymentResultActivity.OnPaymentResultListener) : BaseObservableViewModel() {
    var pointVisibility: ObservableInt = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var purchaseOrderResponse: PurchaseOrderResponse = PurchaseOrderResponse()
        set(value) {
            field = value

            when (value.payment.parentMethod) {
                "Card" -> {
                    paymentMethod = ObservableField("신용/체크카드")
                    methodName = ObservableField(value.payment.method)
                    setCreatedAtText(value.payment.completeAt)
                }
                "VBank" -> {
                    paymentMethod = ObservableField("무통장입금")
                    methodName = ObservableField(value.payment.vbankBankName)
                    setCreatedAtText(value.payment.requestAt)
                }
                "DirectBank" -> paymentMethod = ObservableField("실시간 계좌이체")
                "TOKEN" -> paymentMethod = ObservableField("토큰결제")
                else -> paymentMethod = ObservableField("기타")
            }

            notifyPropertyChanged(BR.paymentMethod)
            notifyPropertyChanged(BR.methodName)
        }

    var methodName: ObservableField<String> = ObservableField("")
        @Bindable
        get() = field
    var paymentMethod = ObservableField("")
        @Bindable
        get() = field
    var shippingMemo: String = ""
    var userName = ""
    var completeAtText = ObservableField("")
        @Bindable
        get() = field


    fun onClickBag() {
        val message = "장바구니 이동"
        listener.showMessage(message)
    }

    fun onClickContinue() {
        listener.redirectMainActivity()
       // listener.closeActivity()
    }

    fun onClickPointHistory() {
        pointVisibility = if (pointVisibility.get() == View.GONE) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)
        notifyPropertyChanged(BR.pointVisibility)
    }

    private fun setCreatedAtText(arr: IntArray) {
        if (arr.isNotEmpty()) {
            completeAtText = ObservableField("${arr[0]}.${arr[1]}.${arr[2]} ${arr[3] ?: ""}:${arr[4]
                    ?: ""}")
            notifyPropertyChanged(BR.completeAtText)
        }
    }

}