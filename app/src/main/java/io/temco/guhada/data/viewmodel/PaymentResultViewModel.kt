package io.temco.guhada.data.viewmodel

import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.data.model.PurchaseOrderResponse
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.activity.PaymentResultActivity

class PaymentResultViewModel(val listener: PaymentResultActivity.OnPaymentResultListener) : BaseObservableViewModel() {
    var pointVisibility: ObservableInt = ObservableInt(View.GONE)
        @Bindable
        get() = field
    var purchaseOrderResponse: PurchaseOrderResponse = PurchaseOrderResponse()
        set(value) {
            field = value
            val completeAt = value.payment.completeAt
            completeAtText = ObservableField("${completeAt[0]}.${completeAt[1]}.${completeAt[2]} ${completeAt[3]
                    ?: ""}:${completeAt[4] ?: ""}")
            notifyPropertyChanged(BR.completeAtText)

            paymentMethod = when (value.payment.parentMethod) {
                "CARD" -> ObservableField("신용/체크카드")
                "VBank" -> ObservableField("무통장입금")
                "DirectBank" -> ObservableField("실시간 계좌이체")
                "TOKEN" -> ObservableField("토큰결제")
                else -> ObservableField("기타")
            }
            notifyPropertyChanged(BR.paymentMethod)
        }
    var shippingMemo: String = ""
    var userName = ""
    var completeAtText = ObservableField("")
        @Bindable
        get() = field
    var paymentMethod = ObservableField("")
        @Bindable
        get() = field

    fun onClickBag() {
        val message = "장바구니 이동"
        listener.showMessage(message)
    }

    fun onClickContinue() {
        listener.closeActivity()
    }

    fun onClickPointHistory() {
        pointVisibility = if (pointVisibility.get() == View.GONE) ObservableInt(View.VISIBLE) else ObservableInt(View.GONE)
        notifyPropertyChanged(BR.pointVisibility)
    }

}