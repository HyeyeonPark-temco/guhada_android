package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.cart.Cart
import io.temco.guhada.data.model.cart.CartResponse
import io.temco.guhada.data.model.order.*
import io.temco.guhada.data.model.payment.PGAuth
import io.temco.guhada.data.model.payment.PGResponse
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.OrderService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderServer {
    companion object {
        /**
         * 주문서 조회 API
         * @param cartIdList 장바구니 id
         */
        @JvmStatic
        fun getOrderForm(listener: OnServerListener, accessToken: String, cartIdList: IntArray) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).getOrderForm(accessToken, cartIdList)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Order>> {
                override fun onFailure(call: Call<BaseModel<Order>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

                override fun onResponse(call: Call<BaseModel<Order>>, response: Response<BaseModel<Order>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }
            })
        }

        /**
         * 장바구니 상품 담기 API
         * @param productId dealId
         * @param optionId dealOptionId
         * @param quantity 주문 수량
         */
        @JvmStatic
        fun addCartItem(listener: OnServerListener, accessToken: String, dealId: Long, dealOptionId: Long?, quantity: Int) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).addCartItem(accessToken, dealId, dealOptionId, quantity)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Cart>> {
                override fun onResponse(call: Call<BaseModel<Cart>>, response: Response<BaseModel<Cart>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Cart>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 결제창(PG사 화면)을 띄우기위한 데이터 생성 API
         */
        @JvmStatic
        fun requestOrder(listener: OnServerListener, accessToken: String, requestOrder: RequestOrder) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).requestOrder(accessToken, requestOrder)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<PGResponse>> {
                override fun onResponse(call: Call<BaseModel<PGResponse>>, response: Response<BaseModel<PGResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<PGResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun setOrderApproval(listener: OnServerListener, accessToken: String, pgAuth: PGAuth) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true, false).setOrderApproval(accessToken, pgAuth)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun setOrderCompleted(listener: OnServerListener, accessToken: String, purchaseId: Double) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).setOrderCompleted(accessToken, purchaseId)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<PurchaseOrderResponse>> {
                override fun onResponse(call: Call<BaseModel<PurchaseOrderResponse>>, response: Response<BaseModel<PurchaseOrderResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<PurchaseOrderResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 장바구니 조회 API
         */
        @JvmStatic
        fun getCart(listener: OnServerListener, accessToken: String) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).getCart(accessToken)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<CartResponse>> {
                override fun onResponse(call: Call<BaseModel<CartResponse>>, response: Response<BaseModel<CartResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<CartResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 장바구니 아이템의 옵션 리스트 조회 API
         */
        @JvmStatic
        fun getCartItemOptionList(listener: OnServerListener, accessToken: String, cartItemId: Long) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).getCartItemOptionList(accessToken, cartItemId).enqueue(object : Callback<BaseModel<Any>> {
                    override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })


        /**
         * 장바구니 옵션 변경 API
         */
        @JvmStatic
        fun updateCartItemOption(listener: OnServerListener, accessToken: String, cartItemId: Long, selectDealOptionId: Int, quantity: Int) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).updateCartItemOption(accessToken, cartItemId, selectDealOptionId, quantity).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<CartResponse>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) })


        /**
         * 장바구니 상품 수량 변경 API
         */
        @JvmStatic
        fun updateCartItemQuantity(listener: OnServerListener, accessToken: String, cartItemId: Long, quantity: Int) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).updateCartItemQuantity(accessToken, cartItemId, quantity).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<CartResponse>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) })


        /**
         * 장바구니 상품 삭제 API
         */
        @JvmStatic
        fun deleteCartItem(listener: OnServerListener, accessToken: String, cartItemIdList: IntArray) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).deleteCartItem(accessToken, cartItemIdList).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<CartResponse>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) })


        /**
         * 주문 상품 조회 API
         */
        @JvmStatic
        fun getOrders(listener: OnServerListener, accessToken: String, startDate: String, endDate: String, page: Int) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).getOrders(accessToken, startDate, endDate, page).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<OrderHistoryResponse>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) }
                )


        /**
         * 주문 상태 조회 API
         */
        @JvmStatic
        fun getOrderStatus(listener: OnServerListener, accessToken: String) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).getOrderStatus(accessToken).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<OrderStatus>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) }
                )

        /**
         * 주문 배송지 수정 API
         */
        @JvmStatic
        fun updateOrderShippingAddress(listener: OnServerListener, accessToken: String, shippingAddress: UserShipping) =
                RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).updateOrderShippingAddress(accessToken, shippingAddress.pId, shippingAddress).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(successResponse.isSuccessful, successResponse.body()) }
                )
    }
}