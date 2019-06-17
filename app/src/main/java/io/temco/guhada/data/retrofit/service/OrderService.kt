package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.Cart
import io.temco.guhada.data.model.Order
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.http.*

interface OrderService {
    /**
     * 주문서 조회 API
     */
    @GET("/order/orderForm")
    fun getOrderForm(@Header("Authorization") accessToken: String, @Query("cartItemIdList") itemIdList: Array<Long>): Call<BaseModel<Order>>

    /**
     * 장바구니 상품 담기 API
     */
    @POST("/cart/addCartItem")
    fun addCartItem(@Header("Authorization") accessToken: String, @Query("dealId") productId: Long, @Query("dealOptionId") optionId: Long?, @Query("quantity") quantity: Int): Call<BaseModel<Cart>>
}