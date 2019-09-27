package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.shipping.ShippingTrackingInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ShippingService {

    /**
     * 배송 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.27
     */
    @GET("/tracking/info")
    fun getShippingTracking(@Query("companyNo") companyNo: String, @Query("invoiceNo") invoiceNo: String): Call<BaseModel<ShippingTrackingInfo>>
}