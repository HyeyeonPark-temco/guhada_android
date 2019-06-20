package io.temco.guhada.data.viewmodel

import io.temco.guhada.data.model.PurchaseOrderResponse
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class PaymentResultViewModel : BaseObservableViewModel() {
    var purchaseOrderResponse: PurchaseOrderResponse = PurchaseOrderResponse()
    var shippingMemo: String = ""
}