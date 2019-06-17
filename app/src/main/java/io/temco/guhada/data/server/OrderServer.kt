package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.data.model.Cart
import io.temco.guhada.data.model.Order
import io.temco.guhada.data.model.base.BaseModel
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
        fun getOrderForm(listener: OnServerListener, accessToken: String, cartIdList: Array<Long>) {
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
         * @param quantity
         */
        @JvmStatic
        fun addCartItm(listener: OnServerListener, accessToken: String, productId: Long, optionId: Long?, quantity: Int) {
            val call = RetrofitManager.createService(Type.Server.ORDER, OrderService::class.java, true).addCartItem(accessToken, productId, optionId, quantity)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<Cart>> {
                override fun onResponse(call: Call<BaseModel<Cart>>, response: Response<BaseModel<Cart>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Cart>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }
    }

}