package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.Order
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface OrderService {
    @GET("/order/orderForm")
    fun findOrderForm(@Header("Authorization") accessToken: String, @Query("cartItemIdList") itemIdList: Array<Long>): Call<BaseModel<Order>>
}