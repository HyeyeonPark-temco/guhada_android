package io.temco.guhada.data.viewmodel

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.shipping.ShippingTrackingInfo
import io.temco.guhada.data.server.ShippingServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ShippingTrackingViewModel : BaseObservableViewModel() {
    var mInvoiceNo = ""
    var mCompanyNo = ""
    var mShippingTrackingInfo = MutableLiveData<ShippingTrackingInfo>()

    fun getShippingTracking() {
        ShippingServer().getShippingTracking(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = { mShippingTrackingInfo.postValue(it.data as ShippingTrackingInfo) })
        }, companyNo = mCompanyNo, invoiceNo = mInvoiceNo)
    }

}