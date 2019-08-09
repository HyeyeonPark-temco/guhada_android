package io.temco.guhada.data.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.naver.NaverResponse
import io.temco.guhada.data.model.review.MyPageReview
import io.temco.guhada.data.model.review.ReviewResponse
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.model.seller.Seller
import io.temco.guhada.data.model.seller.SellerFollower
import io.temco.guhada.data.model.seller.SellerSatisfaction
import io.temco.guhada.data.model.user.SnsUser
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.UserService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * BASE URL: dev.user.guhada.com/
 */
class UserServer {
    companion object {
        /**
         * 유저 정보 가져오기 API
         */
        @JvmStatic
        fun getUserInfo(listener: OnServerListener, userId: Int) =
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getUserInfo(intArrayOf(userId)).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<User>> { successResponse -> listener.onResult(true, successResponse.body()) })


        /**
         * 네이버 유저 프로필 가져오기 API
         */
        @JvmStatic
        fun getNaverProfile(listener: OnServerListener, accessToken: String) {
            val NAVER_API_SUCCESS = "00"
            RetrofitManager.createService(Type.Server.NAVER_PROFILE, UserService::class.java).getNaverProfile("Bearer $accessToken").enqueue(object : Callback<NaverResponse> {
                override fun onResponse(call: Call<NaverResponse>, response: Response<NaverResponse>) {
                    if (response.isSuccessful) {
                        val naverResponse = response.body()
                        if (naverResponse != null && naverResponse.resultCode != null) {
                            if (naverResponse.resultCode == NAVER_API_SUCCESS || naverResponse.message == "success") {
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
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).signUp(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 회원가입 오류"))

        /**
         * 이메일 로그인 API
         */
        @JvmStatic
        fun signIn(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).signIn(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 로그인 오류"))

        /**
         * 이메일로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyEmail(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).verifyEmail(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일로 인증번호 전송하기 오류"))

        /**
         * 핸드폰 번호로 인증번호 전송하기 API
         */
        @JvmStatic
        fun verifyPhone(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).verifyPhone(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "핸드폰 번호로 인증번호 전송하기 오류"))

        /**
         * 인증번호 검증 API
         */
        @JvmStatic
        fun verifyNumber(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).verifyNumber(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 인증번호로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePassword(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).changePassword(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 본인인증으로 비밀번호 재설정하기 API
         */
        @JvmStatic
        fun changePasswordByIdentifying(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).changePasswordByIdentifying(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "본인인증으로 비밀번호 재설정 오류"))

        /**
         * 나이스 본인인증 호출 토큰 가져오기 API
         * BASE URL: http://13.209.10.68/
         */
        @JvmStatic
        fun getVerifyPhoneToken(listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.LOCAL, UserService::class.java).getVerifyToken().enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<String>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "나이스 본인인증 토큰 호출 오류"))

        /**
         * 유저 정보 가져오기 API
         */
        @JvmStatic
        fun findUserId(listener: OnServerListener, name: String, phoneNumber: String) {
            val call = RetrofitManager.createService(Type.Server.USER, UserService::class.java).findUserId(name, phoneNumber)
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
         * SNS 유저 회원가입 API
         */
        @JvmStatic
        fun joinSnsUser(listener: OnServerListener, user: SnsUser) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).joinSnsUser(user).enqueue(
                    ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>> { successResponse -> listener.onResult(true, successResponse.body()) })
        }

        /**
         * 구글 로그인 API
         */
        @JvmStatic
        fun googleLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).googleLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "구글 로그인 오류"))

        /**
         * 네이버 로그인 API
         */
        @JvmStatic
        fun naverLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).naverLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "네이버 로그인 오류"))

        /**
         * 카카오톡 로그인 API
         */
        @JvmStatic
        fun kakaoLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).kakaoLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "카카오톡 로그인 오류"))

        /**
         * 페이스북 로그인 API
         */
        @JvmStatic
        fun facebookLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).facebookLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "페이스북 로그인 오류"))

        @JvmStatic
        fun checkExistSnsUser(listener: OnServerListener, snsType: String, snsId: String, email: String?) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).checkExistSnsUser(snsType, snsId, email
                        ?: "").enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 개별 유저 정보 조회 API
         */
        @JvmStatic
        fun getUserById(listener: OnServerListener, userId: Int) {
            val call = RetrofitManager.createService(Type.Server.USER, UserService::class.java).findUserById(userId)
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
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).checkEmail(email).enqueue(object : Callback<BaseModel<Any>> {
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
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).checkPhone(phoneNumber).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    listener.onResult(response.isSuccessful, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 회원 배송지 조회 API
         */
        @JvmStatic
        fun getUserShippingAddress(listener: OnServerListener, userId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).findShippingAddress(userId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<MutableList<UserShipping>>>(successTask = { successResponse -> listener.onResult(true, successResponse.body()) }, failedTask = { t -> listener.onResult(false, t) }))

        /**
         * 회원 배송지 삭제 API
         */
        @JvmStatic
        fun deleteUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddressId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).deleteShippingAddress(userId, shippingAddressId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 회원 배송지 수정 API
         */
        @JvmStatic
        fun updateUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddressId: Int, shippingAddress: UserShipping) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).updateShippingAddress(userId, shippingAddressId, shippingAddress).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<UserShipping>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 회원 배송지 등록 API
         */
        @JvmStatic
        fun saveUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddress: UserShipping) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).saveShippingAddress(userId, shippingAddress).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 셀러 정보 가져오기 API
         */
        @JvmStatic
        fun getSellerById(listener: OnServerListener, sellerId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerById(sellerId).enqueue(object : Callback<BaseModel<Seller>> {
                override fun onResponse(call: Call<BaseModel<Seller>>, response: Response<BaseModel<Seller>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<Seller>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

            })
        }

        /**
         * 셀러 만족도 조회 API
         */
        @JvmStatic
        fun getSellerSatisfaction(listener: OnServerListener, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerSatisfaction(sellerId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerSatisfaction>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 셀러 팔로우 여부 조회 API
         * (좋아요 API 사용 -> /followers 미사용)
         */
        @JvmStatic
        fun getSellerFollowers(listener: OnServerListener, accessToken: String, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).getSellerFollowers(accessToken, sellerId).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerFollower>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 회원 좋아요 정보 조회 API
         */
        @JvmStatic
        fun getLike(listener: OnServerListener, accessToken: String, target: String, userId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getLike(accessToken, userId, target).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 상품 리뷰 평점 조회 API
         */
        @JvmStatic
        fun getProductReviewSummary(listener: OnServerListener, productId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReviewSummary(productId).enqueue(object : Callback<BaseModel<ReviewSummary>> {
                override fun onResponse(call: Call<BaseModel<ReviewSummary>>, response: Response<BaseModel<ReviewSummary>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ReviewSummary>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 상품 리뷰 리스트 조회 API
         */
        @JvmStatic
        fun getProductReview(listener: OnServerListener, productId: Long, page: Int, size: Int) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReview(productId, page, size).enqueue(object : Callback<BaseModel<ReviewResponse>> {
                override fun onResponse(call: Call<BaseModel<ReviewResponse>>, response: Response<BaseModel<ReviewResponse>>) {
                    listener.onResult(true, response.body())
                }

                override fun onFailure(call: Call<BaseModel<ReviewResponse>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }


        /**
         * 북마크 확인
         *
         * target : Type.BookMark field
         * userId : 유져 id
         */
        @JvmStatic
        fun getBookMark(listener: OnServerListener, accessToken: String, target: String, targetId: Long, userId: Int) {
            if (CustomLog.flag) CustomLog.L("getBookMark", "userId", userId)
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getBookMark(accessToken, userId, target, targetId).enqueue(object : Callback<BaseModel<BookMark>> {
                        override fun onResponse(call: Call<BaseModel<BookMark>>, response: Response<BaseModel<BookMark>>) {
                            listener.onResult(true, response.body())
                        }

                        override fun onFailure(call: Call<BaseModel<BookMark>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
             )
        }

        /**
         * 북마크 저장
         * */
        @JvmStatic
        fun saveBookMark(listener: OnServerListener, accessToken: String, response: JsonObject) {
            if(CustomLog.flag) CustomLog.L("saveBookMark","response",response.toString())
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .saveBookMark(accessToken, response).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            listener.onResult(true, response.body())
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
             )
        }

        /**
         * 북마크 삭제
         */
        @JvmStatic
        fun deleteBookMark(listener: OnServerListener, accessToken: String, target: String, targetId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .deleteBookMark(accessToken, target, targetId).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            listener.onResult(true, response.body())
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 북마크 전체 삭제
         */
        @JvmStatic
        fun deleteBookMarkAll(listener: OnServerListener, accessToken: String, target: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .deleteBookMarkAll(accessToken, target).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            listener.onResult(true, response.body())
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }



        /**
         * 마이페이지 내가 작성한 리뷰 리스트
         */
        @JvmStatic
        fun getMypageReviewList(listener: OnServerListener, accessToken: String, page: Int, size : Int) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getMypageReviewList(accessToken, page,size).enqueue(object : Callback<BaseModel<MyPageReview>> {
                        override fun onResponse(call: Call<BaseModel<MyPageReview>>, response: Response<BaseModel<MyPageReview>>) {
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
                                    if(CustomLog.flag)CustomLog.L("getMypageReviewList","onResponse body",error.toString())
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag)CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<MyPageReview>>, t: Throwable) {
                            if(CustomLog.flag)CustomLog.L("getMypageReviewList","onFailure",t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }


        /**
         * 마이페이지 내가 작성한 리뷰 리스트
         */
        @JvmStatic
        fun deleteReviewData(listener: OnServerListener, accessToken: String, productId: Long, reviewId : Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .deleteReviewData(accessToken, productId,reviewId).enqueue(object : Callback<BaseModel<Any>> {
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
                                    if(CustomLog.flag)CustomLog.L("deleteReviewData","onResponse body",error.toString())
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag)CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if(CustomLog.flag)CustomLog.L("deleteReviewData","onFailure",t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }

    }

}