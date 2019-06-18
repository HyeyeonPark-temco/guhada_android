package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.Cart
import io.temco.guhada.data.model.Order
import io.temco.guhada.data.model.PGResponse
import io.temco.guhada.data.model.RequestOrder
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

    /**
     * 결제창(PG사 화면)을 띄우기위한 데이터 생성 API
     */
    @POST("/order/requestOrder")
    fun requestOrder(@Header("Authorization") accessToken: String, @Body requestOrder: RequestOrder): Call<BaseModel<PGResponse>>
}