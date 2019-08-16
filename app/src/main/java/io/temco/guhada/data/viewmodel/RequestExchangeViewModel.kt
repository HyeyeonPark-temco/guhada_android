package io.temco.guhada.data.viewmodel

import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class RequestExchangeViewModel : BaseObservableViewModel(){
    var purchaseOrder = PurchaseOrder()
}