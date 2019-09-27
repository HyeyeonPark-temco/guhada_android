package io.temco.guhada.data.server

import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.shipping.ShippingTrackingInfo
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.ShippingService

class ShippingServer {

    /**
     * 배송 조회
     * @author Hyeyeon Park
     * @since 2019.09.27
     */
    fun getShippingTracking(listener: OnServerListener, companyNo: String, invoiceNo: String) =
            RetrofitManager.createService(Type.Server.SHIP, ShippingService::class.java, true).getShippingTracking(companyNo, invoiceNo).enqueue(
                    ServerCallbackUtil.ServerResponseCallback<BaseModel<ShippingTrackingInfo>>(
                            successTask = { listener.onResult(true, it.body()) },
                            failedTask = { ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror)) }))

}