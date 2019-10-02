package io.temco.guhada.data.viewmodel.payment

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.common.enum.PaymentWayType
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
                PaymentWayType.CARD.code -> {
                    paymentMethod = ObservableField(PaymentWayType.CARD.label)
                    methodName = ObservableField(value.payment.method)
                    setCreatedAtText(value.payment.completeAt)
                }
                PaymentWayType.VBANK.code -> {
                    paymentMethod = ObservableField(PaymentWayType.VBANK.label)
                    methodName = ObservableField("${value.payment.vbankBankName} ${value.payment.vbankNo}")
                    setCreatedAtText(value.payment.requestAt)
                }
                PaymentWayType.DIRECT_BANK.code -> paymentMethod = ObservableField(PaymentWayType.DIRECT_BANK.label)
                PaymentWayType.TOKEN.code -> paymentMethod = ObservableField(PaymentWayType.TOKEN.label)
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


    fun onClickCart() {
        listener.redirectCartActivity()
    }

    fun onClickContinue() {
        listener.redirectMainActivity()
    }

    fun onClickPointHistory() {
        pointVisibility = if (pointVisibility.get() == View.GONE) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)
        notifyPropertyChanged(BR.pointVisibility)
    }


    private fun setCreatedAtText(arr: IntArray) {
        if (arr.isNotEmpty()) {
            completeAtText = ObservableField("${arr[0]}.${arr[1]}.${arr[2]} ${arr[3] ?: ""}:${arr[4]
                    ?: ""} 까지")
            notifyPropertyChanged(BR.completeAtText)
        }
    }

}