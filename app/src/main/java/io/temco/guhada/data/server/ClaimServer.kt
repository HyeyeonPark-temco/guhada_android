package io.temco.guhada.data.server

import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
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
        /**
         * 회원 상품 문의 조회
         */
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

        /**
         * 비회원 상품 문의 조회
         */
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
         * 상품문의 작성
         */
        @JvmStatic
        fun saveClaim(listener: OnServerListener, inquiry: Inquiry) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java).saveClaim(
                        accessToken = "Bearer $accessToken", productId = inquiry.productId, inquiry = inquiry).enqueue(object : Callback<BaseModel<Claim>> {
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
         * 마이페이지 상품문의 리스트 조회
         */
        @JvmStatic
        fun getMyPageClaimList(listener: OnServerListener, page: Int, status: String) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
                        .getMyClaimList(accessToken = "Bearer $accessToken", page = page, status = status).enqueue(object : Callback<BaseModel<MyPageClaim>> {
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
         * 상품문의 수정
         */
        @JvmStatic
        fun editClaim(listener: OnServerListener, inquiry: Inquiry) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true).editClaim(accessToken = "Bearer $accessToken", productId = inquiry.productId, inquiry = inquiry).enqueue(object : Callback<BaseModel<Claim>> {
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
         * 마이페이지 상품문의 리스트 조회
         */
        @JvmStatic
        fun deleteClaim(listener: OnServerListener, productId: Long, inquiryId: Long) {
            val accessToken = Preferences.getToken()?.accessToken
            if (accessToken.isNullOrBlank()) {
                // 로그인 팝업 노출
                Toast.makeText(BaseApplication.getInstance().applicationContext, BaseApplication.getInstance().getString(R.string.login_message_requiredlogin), Toast.LENGTH_SHORT).show()
            } else {
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
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

        /**
         * 마이페이지 취소교환반품 리스트 조회
         */
        @JvmStatic
        fun getCancelOrders(listener: OnServerListener, accessToken: String, startTimeStamp: Long, endTimeStamp: Long, page: Int) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
                        .getCancelOrderList(accessToken = accessToken, startTimestamp = startTimeStamp, endTimestamp = endTimeStamp, page = page).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 마이페이지 취소교환반품 상태 조회
         */
        @JvmStatic
        fun getCancelOrderStatus(listener: OnServerListener, accessToken: String, startTimeStamp: Long, endTimeStamp: Long) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
                        .getCancelOrderStatus(accessToken = accessToken, startTimestamp = startTimeStamp, endTimestamp = endTimeStamp).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 주문 취소 신청
         */
        @JvmStatic
        fun cancelOrder(listener: OnServerListener, accessToken: String, cancelRequest: CancelRequest) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .cancelOrder(accessToken = accessToken, cancelRequest = cancelRequest).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 교환 신청
         */
        @JvmStatic
        fun requestExchange(listener: OnServerListener, accessToken: String, exchangeRequest: ExchangeRequest) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .requestExchange(accessToken = accessToken, exchangeRequest = exchangeRequest).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 반품 신청
         */
        @JvmStatic
        fun requestRefund(listener: OnServerListener, accessToken: String, refundRequest: RefundRequest) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .requestRefund(accessToken = accessToken, refundRequest = refundRequest).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 주문교환반품 신청 화면 정보
         */
        @JvmStatic
        fun getClaimForm(listener: OnServerListener, accessToken: String, orderProdGroupId: Long) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .getClaimForm(accessToken = accessToken, orderProdGroupId = orderProdGroupId).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 반품 철회
         * response type: Boolean
         */
        @JvmStatic
        fun withdrawRefund(listener: OnServerListener, accessToken: String, orderProdGroupId: Long) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .withdrawRefund(accessToken = accessToken, orderProdGroupId = orderProdGroupId).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 교환 철회
         * response type: Boolean
         */
        @JvmStatic
        fun withdrawExchange(listener: OnServerListener, accessToken: String, orderProdGroupId: Long) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .withdrawExchange(accessToken = accessToken, orderProdGroupId = orderProdGroupId).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 신청서 수정 화면 정보
         */
        @JvmStatic
        fun getUpdateClaimForm(listener: OnServerListener, accessToken: String, orderProdGroupId: Long) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .getUpdateClaimForm(accessToken = accessToken, orderProdGroupId = orderProdGroupId).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 반품 신청서 수정
         */
        @JvmStatic
        fun updateRefund(listener: OnServerListener, accessToken: String, refundRequest: RefundRequest) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .updateRefund(accessToken = accessToken, refundRequest = refundRequest).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 교환 신청서 수정
         */
        @JvmStatic
        fun updateExchange(listener: OnServerListener, accessToken: String, exchangeRequest: ExchangeRequest) =
                RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true, false)
                        .updateExchange(accessToken = accessToken, exchangeRequest = exchangeRequest).enqueue(
                                ServerCallbackUtil.ServerResponseCallback(successTask = { listener.onResult(true, it.body()) }))



        /**
         * 신고하기 유형 가져오기
         */
        @JvmStatic
        fun getReportTypeList(listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
                    .getReportType().enqueue(object : Callback<BaseModel<ReportTypeData>> {
                        override fun onResponse(call: Call<BaseModel<ReportTypeData>>, response: Response<BaseModel<ReportTypeData>>) {
                            listener.onResult(response.isSuccessful, response.body())
                        }
                        override fun onFailure(call: Call<BaseModel<ReportTypeData>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 신고하기 유저 이미지 업로드 URL 가져오기
         */
        @JvmStatic
        fun getReportUserPhotoUrl(listener: OnServerListener, accessToken: String) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java, true)
                    .getReportUserPhotoUrl(accessToken).enqueue(object : Callback<BaseModel<JsonObject>> {
                        override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                            listener.onResult(response.isSuccessful, response.body())
                        }
                        override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }



        /**
         * 신고하기 작성
         */
        @JvmStatic
        fun saveReport(listener: OnServerListener, accessToken : String, report: ReportResponse) {
            RetrofitManager.createService(Type.Server.CLAIM, ClaimService::class.java,true).saveReport(
                    accessToken = accessToken, report = report).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    if(response.code() in 200..400 && response.body() != null){
                        listener.onResult(true, response.body())
                    }else{
                        try{
                            var msg  = Message()
                            var errorBody : String? = response.errorBody()?.string() ?: null
                            if(!errorBody.isNullOrEmpty()){
                                var gson = Gson()
                                msg = gson.fromJson<Message>(errorBody, Message::class.java)
                            }
                            var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                            if(CustomLog.flag) CustomLog.L("saveReport","onResponse body",error.toString())
                            listener.onResult(false, error)
                        }catch (e : Exception){
                            if(CustomLog.flag) CustomLog.E(e)
                            listener.onResult(false, null)
                        }
                    }
                }
                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    if(CustomLog.flag) CustomLog.L("saveReport","onFailure",t.message.toString())
                    listener.onResult(false, t.message)
                }
            })
        }



    }
}