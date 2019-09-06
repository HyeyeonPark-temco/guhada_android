package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.SellerInquireOrder
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.cart.CartResponse
import io.temco.guhada.data.model.order.*
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.payment.PGResponse
import io.temco.guhada.data.model.review.MyPageOrderReview
import retrofit2.Call
import retrofit2.http.*

interface OrderService {
    /**
     * 주문서 조회 API
     */
    @GET("/order/orderForm")
    fun getOrderForm(@Header("Authorization") accessToken: String, @Query("cartItemIdList") itemIdList: IntArray): Call<BaseModel<Order>>

    /**
     * 장바구니 상품 담기 API
     */
    @POST("/cart/addCartItem")
    fun addCartItem(@Header("Authorization") accessToken: String, @Query("dealId") dealId: Long, @Query("dealOptionId") optionId: Long?, @Query("quantity") quantity: Int): Call<BaseModel<Cart>>

    /**
     * 장바구니 상품 담기 API (옵션 없는 상품)
     */
    @POST("/cart/addCartItem")
    fun addCartItem(@Header("Authorization") accessToken: String, @Query("dealId") dealId: Long, @Query("quantity") quantity: Int): Call<BaseModel<Cart>>

    /**
     * 결제창(PG사 화면)을 띄우기위한 데이터 생성 API
     */
    @POST("/order/requestOrder")
    fun requestOrder(@Header("Authorization") accessToken: String, @Body requestOrder: RequestOrder): Call<BaseModel<PGResponse>>

    /**
     * 주문 승인 요청 API
     */
    @POST("/order/orderApproval")
    fun setOrderApproval(@Header("Authorization") accessToken: String, @Body pgAuth: PGAuth): Call<BaseModel<Any>>

    /**
     * 주문 완료 API
     */
    @GET("/order/order-complete/{purchaseId}")
    fun setOrderCompleted(@Header("Authorization") accessToken: String, @Path("purchaseId") purchaseId: Double): Call<BaseModel<PurchaseOrderResponse>>

    /**
     * 장바구니 조회 API
     */
    @GET("/cart")
    fun getCart(@Header("Authorization") accessToken: String): Call<BaseModel<CartResponse>>

    /**
     * 장바구니 상품의 옵션 리스트 조회 API
     */
    @GET("/cart/getCartItemOptionList")
    fun getCartItemOptionList(@Header("Authorization") accessToken: String, @Query("cartItemId") cartItemId: Long): Call<BaseModel<Any>>

    /**
     * 장바구니 상품 옵션 변경 API
     */
    @POST("/cart/changeSelectOption")
    fun updateCartItemOption(@Header("Authorization") accessToken: String, @Query("cartItemId") cartItemId: Long, @Query("selectDealOptionId") selectDealOptionId: Int, @Query("quantity") quantity: Int): Call<BaseModel<CartResponse>>

    /**
     * 장바구니 상품 수량 변경 API
     */
    @POST("/cart/changeQuantity")
    fun updateCartItemQuantity(@Header("Authorization") accessToken: String, @Query("cartItemId") cartItemId: Long, @Query("quantity") quantity: Int): Call<BaseModel<CartResponse>>

    /**
     * 장바구니 상품 삭제 API
     */
    @POST("/cart/removeCartItem")
    fun deleteCartItem(@Header("Authorization") accessToken: String, @Query("cartItemIdList") cartItemIdList: IntArray): Call<BaseModel<CartResponse>>

    /**
     * 주문 리스트 조회 API
     * deprecated
     * @see getOrders
     */
    @GET("/order/my-orders/{startDt}/{endDt}/{page}")
    fun getOrderList(@Header("Authorization") accessToken: String, @Path("startDt") startDate: String, @Path("endDt") endDate: String, @Path("page") page: Int): Call<BaseModel<OrderHistoryResponse>>

    /**
     * 주문 리스트 조회 API
     *
     * @param unitPerPage 추가 필요 (default 10)
     * @since 2019.08.22
     * @author Hyeyeon Park
     */
    @GET("/order/my-order-list")
    fun getOrders(@Header("Authorization") accessToken: String, @Query("startTimestamp") startTimestamp: Long, @Query("endTimestamp") endTimestamp: Long, @Query("page") page: Int): Call<BaseModel<OrderHistoryResponse>>


    /**
     * 주문 상태 조회 API
     * deprecated
     */
    @GET("/order/my-orders-status")
    fun getOrderStatus(@Header("Authorization") accessToken: String): Call<BaseModel<OrderStatus>>

    /**
     * 주문 배송지 수정 API
     * response type: Boolean
     */
    @POST("/order/order-update/shipping-address")
    fun updateOrderShippingAddress(@Header("Authorization") accessToken: String, @Query("purchaseId") purchaseId: Long, @Body shippingAddress: UserShipping, @Query("addShippingAddress") addShippingAddress: Boolean): Call<BaseModel<Any>>

    /**
     * 영수증 주소 조회 API
     */
    @GET("/receiptUrlSearch")
    fun getReceiptUrl(@Query("tid") tId: String): Call<BaseModel<Any>>


    /**
     * 리뷰가 작성 가능한 주문아이템 리스트 API
     */
    @GET("/order-review/available-review-order/{page}")
    fun getAvailableReviewList(@Header("Authorization") accessToken: String, @Path("page") page: Int): Call<BaseModel<MyPageOrderReview>>

    /**
     * 구매 확정 API
     */
    @POST("/order/order-prod-confirm")
    fun confirmPurchase(@Header("Authorization") accessToken: String, @Query("orderProdGroupId") orderProdGroupId: Long): Call<BaseModel<Any?>>


    /**
     * 내가 주문한 상품에대한 문의
     */
    @GET("/order-review/seller-inquire-order")
    fun getSellerInquireOrder(@Header("Authorization") accessToken: String, @Query("sellerId") sellerId: Long): Call<BaseModel<SellerInquireOrder>>

}
