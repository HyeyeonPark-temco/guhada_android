package io.temco.guhada.data.server

import android.widget.Toast
import com.google.gson.JsonObject
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.Inquiry
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.ClaimService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class ClaimServer {
    companion object {
        @JvmStatic
        fun getClaims(listener: OnServerListener, productId: Long, isMyInquiry: Boolean?, pageNo: Int, size: Int, status: String) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true).getClaims(productId = productId, isMyInquiry = isMyInquiry,
                    pageNo = pageNo, size = size, status = status, accessToken = "Bearer ${Preferences.getToken().accessToken}").enqueue(object : Callback<BaseModel<ClaimResponse>> {
                override fun onResponse(call: Call<BaseModel<ClaimResponse>>, response: Response<BaseModel<ClaimResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ClaimResponse>>, t: Throwable) {
//                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun getClaimsForGuest(listener: OnServerListener, productId: Long, pageNo: Int, size: Int, status: String) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true).getClaimsForQuest(productId = productId,
                    pageNo = pageNo, size = size, status = status).enqueue(object : Callback<BaseModel<ClaimResponse>> {
                override fun onResponse(call: Call<BaseModel<ClaimResponse>>, response: Response<BaseModel<ClaimResponse>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ClaimResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 상품문의 작성 API
         */
        @JvmStatic
        fun saveClaim(listener: OnServerListener, inquiry: Inquiry) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).saveClaim(accessToken = "Bearer $accessToken", productId = inquiry.productId, inquiry = inquiry).enqueue(object : Callback<BaseModel<Claim>> {
                    override fun onResponse(call: Call<BaseModel<Claim>>, response: Response<BaseModel<Claim>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<Claim>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }


        /**
         * 마이페이지 상품문의 리스트
         */
        @JvmStatic
        fun getMyPageClaimList(listener: OnServerListener, page : Int) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java,true)
                        .getMyClaimList(accessToken = "Bearer $accessToken", page = page).enqueue(object : Callback<BaseModel<MyPageClaim>> {
                    override fun onResponse(call: Call<BaseModel<MyPageClaim>>, response: Response<BaseModel<MyPageClaim>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }
                    override fun onFailure(call: Call<BaseModel<MyPageClaim>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }

        /**
         * 상품문의 수정 API
         */
        @JvmStatic
        fun editClaim(listener: OnServerListener, inquiry: Inquiry) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).editClaim(accessToken = "Bearer $accessToken", productId = inquiry.productId, inquiry = inquiry).enqueue(object : Callback<BaseModel<Claim>> {
                    override fun onResponse(call: Call<BaseModel<Claim>>, response: Response<BaseModel<Claim>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }

                    override fun onFailure(call: Call<BaseModel<Claim>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }


        /**
         * 마이페이지 상품문의 리스트
         */
        @JvmStatic
        fun deleteClaim(listener: OnServerListener, productId : Long, inquiryId : Long) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java,true)
                        .deleteClaim(accessToken = "Bearer $accessToken", productId = productId, inquiryId = inquiryId).enqueue(object : Callback<BaseModel<JsonObject>> {
                    override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                        listener.onResult(response.isSuccessful, response.body())
                    }
                    override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }
                })
            }
        }
    }
}