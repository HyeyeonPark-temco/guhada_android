package io.temco.guhada.data.server

import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerResponseCallback
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginServer {
    companion object {
        /**
         * 네이버 유저 프로필 가져오기 API
         */
        @JvmStatic
        fun getNaverProfile(listener: OnServerListener, accessToken: String) {
            val NAVER_API_SUCCESS = "00"
            RetrofitManager.createService(Type.Server.NAVER_PROFILE, LoginService::class.java).getNaverProfile("Bearer $accessToken").enqueue(object : Callback<NaverResponse> {
                override fun onResponse(call: Call<NaverResponse>, response: Response<NaverResponse>) {
                    if (response.isSuccessful) {
                        val naverResponse = response.body()
                        if (naverResponse != null && naverResponse.resultCode != null) {
                            if (naverResponse.resultCode == NAVER_API_SUCCESS) {
                                listener.onResult(true, naverResponse.user)
                            }
                        } else {
                            listener.onResult(false, response.message())
                        }
                    } else {
                        listener.onResult(false, response.message())
                    }
                }

                override fun onFailure(call: Call<NaverResponse>, t: Throwable) {
                    CommonUtil.debug("[NAVER] FAILED: " + t.message)
                }
            })
        }

        /**
         * 이메일 회원가입 API
         */
        @JvmStatic
        fun signUp(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).signUp(user).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 회원가입 오류"))

        /**
         * 이메일 로그인 API
         */
        @JvmStatic
        fun signIn(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).signIn(user).enqueue(ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 로그인 오류"))

        /**
         * 이메일로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyEmail(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyEmail(user).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일로 인증번호 전송하기 오류"))

        /**
         * 핸드폰 번호로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyPhone(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyPhone(user).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "핸드폰 번호로 인증번호 전송하기 오류"))

        /**
         * 인증번호 검증 API
         */
        @JvmStatic
        fun verifyNumber(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).verifyNumber(verification).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 인증번호로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePassword(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).changePassword(verification).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 본인인증으로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePasswordByIdentifying(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).changePasswordByIdentifying(verification).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "본인인증으로 비밀번호 재설정 오류"))

        /**
         * 나이스 본인인증 호출 토큰 가져오기 API
         * BASE URL: http://13.209.10.68/
         */
        @JvmStatic
        fun getVerifyPhoneToken(listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.LOCAL, LoginService::class.java).getVerifyToken().enqueue(ServerResponseCallback<BaseModel<String>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "나이스 본인인증 토큰 호출 오류"))

        /**
         * 유저 정보 가져오기 API
         */
        @JvmStatic
        fun findUserId(listener: OnServerListener, name: String, phoneNumber: String) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).findUserId(name, phoneNumber)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<User>> {
                override fun onResponse(call: Call<BaseModel<User>>, response: Response<BaseModel<User>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<User>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 구글 로그인 API
         */
        @JvmStatic
        fun googleLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).googleLogin(user).enqueue(ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "구글 로그인 오류"))

        /**
         * 네이버 로그인 API
         */
        @JvmStatic
        fun naverLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).naverLogin(user).enqueue(ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "네이버 로그인 오류"))

        /**
         * 카카오톡 로그인 API
         */
        @JvmStatic
        fun kakaoLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).kakaoLogin(user).enqueue(ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "카카오톡 로그인 오류"))

        /**
         * 페이스북 로그인 API
         */
        @JvmStatic
        fun facebookLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).facebookLogin(user).enqueue(ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "페이스북 로그인 오류"))

        @JvmStatic
        fun checkExistSnsUser(listener: OnServerListener, snsType: String, snsId: String) =
                RetrofitManager.createService(Type.Server.USER, LoginService::class.java).checkExistSnsUser(snsType, snsId).enqueue(ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "중복 SNS 유저 체크 오류"))

        /**
         * 개별 유저 정보 조회 API
         */
        @JvmStatic
        fun getUserById(listener: OnServerListener, userId: Int) {
            val call = RetrofitManager.createService(Type.Server.USER, LoginService::class.java).findUserById(userId)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<User>> {
                override fun onFailure(call: Call<BaseModel<User>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

                override fun onResponse(call: Call<BaseModel<User>>, response: Response<BaseModel<User>>) {
                    listener.onResult(true, response.body())
                }
            })
        }

        /**
         * 중복된 이메일인지 검증 API
         */
        @JvmStatic
        fun checkEmail(listener: OnServerListener, email: String) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java).checkEmail(email).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /***
         * 중복된 핸드폰 번호인지 검증 API
         */
        @JvmStatic
        fun checkPhone(listener: OnServerListener, phoneNumber: String) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java).checkPhone(phoneNumber).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun getUserShippingAddress(listener: OnServerListener, userId: Int) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java).findShippingAddress(userId).enqueue(object : Callback<BaseModel<MutableList<UserShipping>>> {
                override fun onFailure(call: Call<BaseModel<MutableList<UserShipping>>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

                override fun onResponse(call: Call<BaseModel<MutableList<UserShipping>>>, response: Response<BaseModel<MutableList<UserShipping>>>) {
                    listener.onResult(true, response.body())
                }
            })
        }

        @JvmStatic
        fun getSellerById(listener: OnServerListener, sellerId: Long) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java, true).getSellerById(sellerId).enqueue(object : Callback<BaseModel<Seller>> {
                override fun onResponse(call: Call<BaseModel<Seller>>, response: Response<BaseModel<Seller>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Seller>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

            })
        }

        @JvmStatic
        fun getProductReviewSummary(listener: OnServerListener, productId: Long) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java, true).getProductReviewSummary(productId).enqueue(object : Callback<BaseModel<ReviewSummary>> {
                override fun onResponse(call: Call<BaseModel<ReviewSummary>>, response: Response<BaseModel<ReviewSummary>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ReviewSummary>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        @JvmStatic
        fun getProductReview(listener: OnServerListener, productId: Long, page: Int, size: Int) {
            RetrofitManager.createService(Type.Server.USER, LoginService::class.java, true).getProductReview(productId, page, size).enqueue(object : Callback<BaseModel<ReviewResponse>> {
                override fun onResponse(call: Call<BaseModel<ReviewResponse>>, response: Response<BaseModel<ReviewResponse>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ReviewResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }
    }
}