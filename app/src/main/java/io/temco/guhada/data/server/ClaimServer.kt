package io.temco.guhada.data.server

import android.widget.Toast
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.InquiryRequest
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.ClaimService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ClaimServer {
    companion object {
        @JvmStatic
        fun getClaims(listener: OnServerListener, productId: Int, isMyInquiry: Boolean?, pageNo: Int, size: Int, status: String) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).getClaims(productId = productId, isMyInquiry = isMyInquiry,
                    pageNo = pageNo, size = size, status = status).enqueue(object : Callback<BaseModel<ClaimResponse>> {
                override fun onResponse(call: Call<BaseModel<ClaimResponse>>, response: Response<BaseModel<ClaimResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ClaimResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun getClaimsForGuest(listener: OnServerListener, productId: Int, pageNo: Int, size: Int, status: String) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).getClaimsForQuest(productId = productId,
                    pageNo = pageNo, size = size, status = status).enqueue(object : Callback<BaseModel<ClaimResponse>> {
                override fun onResponse(call: Call<BaseModel<ClaimResponse>>, response: Response<BaseModel<ClaimResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ClaimResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun saveClaim(listener: OnServerListener, inquiry: InquiryRequest) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).saveClaim(accessToken = "Bearer $accessToken", productId = inquiry.productId, inquiry = inquiry).enqueue(object : Callback<BaseModel<ClaimResponse.Claim>> {
                    override fun onResponse(call: Call<BaseModel<ClaimResponse.Claim>>, response: Response<BaseModel<ClaimResponse.Claim>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<ClaimResponse.Claim>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }

                })

            }
        }

    }
}