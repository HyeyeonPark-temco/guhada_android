package io.temco.guhada.data.server

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.RetryableCallback
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.naver.NaverResponse
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.point.PointPopupInfo
import io.temco.guhada.data.model.review.*
import io.temco.guhada.data.model.seller.*
import io.temco.guhada.data.model.user.*
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.UserService
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * BASE URL: dev.user.guhada.com/
 */
class UserServer {
    companion object {

        @JvmStatic
        fun <C, R> resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>) {
            if (response.code() in 200..400 && response.body() != null) {
                listener.onResult(true, response.body())
            } else {
                try {
                    var msg = Message()
                    var errorBody: String? = response.errorBody()?.string() ?: null
                    if (!errorBody.isNullOrEmpty()) {
                        var gson = Gson()
                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                    }
                    var error = BaseErrorModel(response.code(), response.raw().request().url().toString(), msg)
                    if (CustomLog.flag) CustomLog.L("saveReport", "onResponse body", error.toString())
                    listener.onResult(false, error)
                } catch (e: Exception) {
                    if (CustomLog.flag) CustomLog.E(e)
                    listener.onResult(false, null)
                }
            }
        }


        /**
         * 유저 정보 가져오기
         */
        @JvmStatic
        fun getUserInfo(listener: OnServerListener, userId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getUserInfo(intArrayOf(userId)).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<User>> { successResponse -> listener.onResult(true, successResponse.body()) })


        /**
         * 네이버 유저 프로필 가져오기
         */
        @JvmStatic
        fun getNaverProfile(listener: OnServerListener) {
            CoroutineScope(Dispatchers.Default).launch {
                val string = SnsLoginModule.mNaverLoginModule.requestApi(BaseApplication.getInstance(), SnsLoginModule.mNaverLoginModule.getAccessToken(BaseApplication.getInstance()), "https://openapi.naver.com/v1/nid/me")
                val obj = JSONObject(string)
                val naverResponse = Gson().fromJson<NaverResponse>(obj.toString(), NaverResponse::class.java)
                listener.onResult(string.isNotEmpty(), naverResponse.user)
            }
        }

        /**
         * 이메일 회원가입
         */
        @JvmStatic
        fun signUp(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).signUp(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 회원가입 오류"))

        /**
         * 이메일 로그인
         */
        @JvmStatic
        fun signIn(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).signIn(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일 로그인 오류"))

        /**
         * 이메일로 인증번호 전송하기
         * User 중 email, name 필드만 입력
         * response: {"result":"SUCCESS","data":600000,"resultCode":200,"message":"success"}
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun verifyEmail(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).verifyEmail(user = user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Long>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "이메일로 인증번호 전송하기 오류"))

        /**
         * 핸드폰 번호로 인증번호 전송하기
         */
        @JvmStatic
        fun verifyPhone(listener: OnServerListener, user: User) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).verifyPhone(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "핸드폰 번호로 인증번호 전송하기 오류"))

        /**
         * 인증번호 검증
         */
        @JvmStatic
        fun verifyNumber(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).verifyNumber(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 인증번호로 비밀번호 재설정하기
         */
        @JvmStatic
        fun changePassword(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).changePassword(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "인증번호 검증 오류"))


        /**
         * 본인인증으로 비밀번호 재설정하기
         */
        @JvmStatic
        fun changePasswordByIdentifying(listener: OnServerListener, verification: Verification) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).changePasswordByIdentifying(verification).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "본인인증으로 비밀번호 재설정 오류"))

        /**
         * 나이스 본인인증 호출 토큰 가져오기
         * BASE URL: https://stg.order.guhada.com
         */
        @JvmStatic
        fun getVerifyPhoneToken(listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.ORDER, UserService::class.java, true, false).getVerifyToken().enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<String>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "나이스 본인인증 토큰 호출 오류"))

        /**
         * 유저 정보 가져오기
         */
        @JvmStatic
        fun findUserId(listener: OnServerListener, body: JsonObject) {
            val call = RetrofitManager.createService(Type.Server.USER, UserService::class.java).findUserId(body)
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
         * SNS 유저 회원가입
         */
        @JvmStatic
        fun joinSnsUser(listener: OnServerListener, user: SnsUser) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).joinSnsUser(user).enqueue(
                    ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>> { successResponse -> listener.onResult(true, successResponse.body()) })
        }

        /**
         * 구글 로그인
         */
        @JvmStatic
        fun googleLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).googleLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "구글 로그인 오류"))

        /**
         * 네이버 로그인
         */
        @JvmStatic
        fun naverLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).naverLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "네이버 로그인 오류"))

        /**
         * 카카오톡 로그인
         */
        @JvmStatic
        fun kakaoLogin(user: SnsUser, listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).kakaoLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "카카오톡 로그인 오류"))

        /**
         * 페이스북 로그인
         */
        @JvmStatic
        fun facebookLogin(listener: OnServerListener, user: SnsUser) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).facebookLogin(user).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Token>>({ successResponse -> listener.onResult(true, successResponse.body()) }, "페이스북 로그인 오류"))

        @JvmStatic
        fun checkExistSnsUser(listener: OnServerListener, snsType: String, snsId: String, email: String?) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).checkExistSnsUser(snsType, snsId, email
                        ?: "").enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        @JvmStatic
        suspend fun checkExistSnsUserAsync(snsType: String, snsId: String, email: String?): Deferred<BaseModel<Any>> = GlobalScope.async {
            val mail = email ?: ""
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).checkExistSnsUserAsync(snsType, snsId, mail).await()
        }


        /**
         * 개별 유저 정보 조회
         */
        @JvmStatic
        fun getUserById(listener: OnServerListener, userId: Int) {
            val call = RetrofitManager.createService(Type.Server.USER, UserService::class.java).findUserById(userId)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<User>> {
                override fun onFailure(call: Call<BaseModel<User>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

                override fun onResponse(call: Call<BaseModel<User>>, response: Response<BaseModel<User>>) {
                    resultListener(listener, call, response)
                }
            })
        }

        /**
         * 개별 회원 정보 조회 API 2. GET : /users -> 유저 데이터 불러오기
         */
        @JvmStatic
        fun findUsers(listener: OnServerListener, userId: Int) {
            val call = RetrofitManager.createService(Type.Server.USER, UserService::class.java).findUsers(userId)
            RetryableCallback.APIHelper.enqueueWithRetry(call, object : Callback<BaseModel<User>> {
                override fun onFailure(call: Call<BaseModel<User>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }

                override fun onResponse(call: Call<BaseModel<User>>, response: Response<BaseModel<User>>) {
                    resultListener(listener, call, response)
                }
            })
        }


        /**
         * 중복된 이메일인지 검증
         */
        @JvmStatic
        fun checkEmail(listener: OnServerListener, email: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).checkEmail(email).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    resultListener(listener, call, response)
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /***
         * 중복된 핸드폰 번호인지 검증
         */
        @JvmStatic
        fun checkPhone(listener: OnServerListener, phoneNumber: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java).checkPhone(phoneNumber).enqueue(object : Callback<BaseModel<Any>> {
                override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                    resultListener(listener, call, response)
                }

                override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 회원 배송지 조회
         */
        @JvmStatic
        fun getUserShippingAddress(listener: OnServerListener, userId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).findShippingAddress(userId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<MutableList<UserShipping>>>(successTask = { successResponse -> listener.onResult(true, successResponse.body()) }, failedTask = { t -> listener.onResult(false, t) }))

        /**
         * 회원 배송지 삭제
         */
        @JvmStatic
        fun deleteUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddressId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).deleteShippingAddress(userId, shippingAddressId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 회원 배송지 수정
         */
        @JvmStatic
        fun updateUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddressId: Int, shippingAddress: UserShipping) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).updateShippingAddress(userId, shippingAddressId, shippingAddress).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<UserShipping>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 회원 배송지 등록
         */
        @JvmStatic
        fun saveUserShippingAddress(listener: OnServerListener, userId: Int, shippingAddress: UserShipping) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).saveShippingAddress(userId, shippingAddress).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 셀러 정보 가져오기
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
         * 셀러 정보 가져오기 (비동기)
         */
        @JvmStatic
        suspend fun getSellerByIdAsync(sellerId: Long): Deferred<BaseModel<Seller>> = GlobalScope.async {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).getSellerByIdAsync(sellerId).await()
        }

        /**
         * 셀러 반품지 가져오기
         */
        @JvmStatic
        fun getSellerDefaultReturnAddress(listener: OnServerListener, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerDefaultReturnAddress(sellerId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerAddress>> { response -> listener.onResult(true, response.body()) })

        /**
         * 셀러 만족도 조회
         */
        @JvmStatic
        fun getSellerSatisfaction(listener: OnServerListener, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerSatisfaction(sellerId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerSatisfaction>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 셀러 팔로우 여부 조회
         * (좋아요 API 사용 -> /followers 미사용)
         */
        @JvmStatic
        fun getSellerFollowers(listener: OnServerListener, accessToken: String, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).getSellerFollowers(accessToken, sellerId).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerFollower>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 북마크 카운트 조회
         */
        @JvmStatic
        fun getBookMarkCount(listener: OnServerListener, target: String, targetId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java).getBookMarkCount(target = target, targetId = targetId).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<BookMarkCountResponse>>
                { response -> listener.onResult(true, response.body()) })

        /**
         * 북마크 카운트 조회 (비동기)
         */
        @JvmStatic
        fun getBookMarkCountAsync(target: String, targetId: Long): Deferred<BaseModel<BookMarkCountResponse>> =
                GlobalScope.async {
                    RetrofitManager.createService(Type.Server.USER, UserService::class.java).getBookMarkCountAsync(target = target, targetId = targetId).await()
                }

        /**
         * 회원 좋아요 정보 조회
         */
        @JvmStatic
        fun getLike(listener: OnServerListener, accessToken: String, target: String, userId: Long, targetId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getLike(accessToken, userId, targetId, target).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<BookMark>> { successResponse -> listener.onResult(true, successResponse.body()) })

        /**
         * 상품 리뷰 평점 조회
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
         * 상품 리뷰 리스트 조회
         */
        @JvmStatic
        fun getProductReview(listener: OnServerListener, productId: Long, page: Int, size: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReview(productId, page, size).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        @JvmStatic
        fun getProductReviewWithRating(listener: OnServerListener, productId: Long, page: Int, size: Int, rating: String) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReviewWithRating(productId, page, size, rating).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        @JvmStatic
        fun getProductReviewWithSorting(listener: OnServerListener, productId: Long, page: Int, size: Int, sorting: String) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReviewWithSorting(productId, page, size, sorting).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        @JvmStatic
        fun getProductReviewWithSortingAndRating(listener: OnServerListener, productId: Long, page: Int, size: Int, sorting: String, rating: String) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductReviewWithSortingAndRating(productId = productId, page = page, size = size, sorting = sorting, rating = rating).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        /**
         * 상품 리뷰 리스트(포토 리뷰) 조회
         * @author Hyeyeon Park
         * @since 2019.09.25
         */
        @JvmStatic
        fun getProductPhotoReview(listener: OnServerListener, productId: Long, page: Int, size: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductPhotoReview(productId, page, size).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        @JvmStatic
        fun getProductPhotoReviewWithSorting(listener: OnServerListener, productId: Long, page: Int, size: Int, sorting: String) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductPhotoReviewWithSorting(productId, page, size, sorting).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        /**
         * 상품 리뷰 리스트(개인 수치 포함) 조회
         * @author Hyeyeon Park
         * @since 2019.09.25
         */
        @JvmStatic
        fun getProductSizeReview(listener: OnServerListener, productId: Long, page: Int, size: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductSizeReview(productId, page, size).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

        @JvmStatic
        fun getProductSizeReviewWithSorting(listener: OnServerListener, productId: Long, page: Int, size: Int, sorting: String) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getProductSizeReviewWithSorting(productId, page, size, sorting).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<ReviewResponse>> { listener.onResult(true, it.body()) })

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
                    })
        }

        @JvmStatic
        fun getBookMarkWithoutTargetId(listener: OnServerListener, accessToken: String, target: String, userId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                        .getBookMarkWithoutTargetId(accessToken = accessToken, userId = userId, target = target).enqueue(
                                ServerCallbackUtil.ServerResponseCallback<BaseModel<BookMark>>(successTask = { response -> listener.onResult(true, response.body()) }))

        @JvmStatic
        fun getBookMarkWithoutTargetIdAndTarget(listener: OnServerListener, accessToken: String, userId: Int) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                        .getBookMarkWithoutTargetIdAndTarget(accessToken = accessToken, userId = userId).enqueue(
                                ServerCallbackUtil.ServerResponseCallback<BaseModel<BookMark>>(successTask = { response -> listener.onResult(true, response.body()) }))

        /**
         * 북마크 저장
         * */
        @JvmStatic
        fun saveBookMark(listener: OnServerListener, accessToken: String, response: JsonObject) {
            if (CustomLog.flag) CustomLog.L("saveBookMark", "response", response.toString())
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .saveBookMark(accessToken, response).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            listener.onResult(true, response.body())
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    })
        }

        /**
         * 북마크 저장 (비동기)
         * @since 2019.08.27
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun saveBookMarkAsync(accessToken: String, response: JsonObject): Deferred<BaseModel<Any>> = GlobalScope.async {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).saveBookMarkAsync(accessToken = accessToken, response = response).await()
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
         * 북마크 삭제 (비동기)
         * @since 2019.08.27
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun deleteBookMarkAsync(accessToken: String, target: String, targetId: Long): Deferred<BaseModel<Any>> = GlobalScope.async {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).deleteBookMarkAsync(accessToken = accessToken, target = target, targetId = targetId).await()
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
        fun getMypageReviewList(listener: OnServerListener, accessToken: String, page: Int, size: Int) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getMypageReviewList(accessToken, page, size).enqueue(object : Callback<BaseModel<MyPageReview>> {
                        override fun onResponse(call: Call<BaseModel<MyPageReview>>, response: Response<BaseModel<MyPageReview>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<MyPageReview>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 마이페이지 내가 작성한 리뷰 삭제
         */
        @JvmStatic
        fun deleteReviewData(listener: OnServerListener, accessToken: String, productId: Long, reviewId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .deleteReviewData(accessToken, productId, reviewId).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("deleteReviewData", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 마이페이지 리뷰 작성하기
         */
        @JvmStatic
        fun writeReview(listener: OnServerListener, accessToken: String, productId: Long, data: ReviewWrMdResponse) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .writeReview(accessToken, productId, data).enqueue(object : Callback<BaseModel<ReviewData>> {
                        override fun onResponse(call: Call<BaseModel<ReviewData>>, response: Response<BaseModel<ReviewData>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<ReviewData>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("writeReview", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 마이페이지 리뷰 수정하기
         */
        @JvmStatic
        fun modifyReview(listener: OnServerListener, accessToken: String, productId: Long, reviewId: Int, data: ReviewWrMdResponse) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .modifyReview(accessToken, productId, reviewId, data).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("modifyReview", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 회원 신체 사이즈 정보 가져오기
         */
        @JvmStatic
        fun getUserSize(listener: OnServerListener, accessToken: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getUserSize(accessToken).enqueue(object : Callback<BaseModel<UserSize>> {
                        override fun onResponse(call: Call<BaseModel<UserSize>>, response: Response<BaseModel<UserSize>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<UserSize>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getUserSize", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }


        /**
         * 회원 신체 사이즈 정보 저장하기
         */
        @JvmStatic
        fun saveUserSize(listener: OnServerListener, accessToken: String, data: UserSize) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .saveUserSize(accessToken, data).enqueue(object : Callback<BaseModel<PointPopupInfo>> {
                        override fun onResponse(call: Call<BaseModel<PointPopupInfo>>, response: Response<BaseModel<PointPopupInfo>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<PointPopupInfo>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("saveUserSize", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }


        /**
         * 회원 신체 사이즈 정보 수정하기
         */
        @JvmStatic
        fun modifyUserSize(listener: OnServerListener, accessToken: String, data: UserSize) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .modifyUserSize(accessToken, data).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("modifyUserSize", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }

        /**
         * 특정 리뷰 조회 (비동기)
         * @author Hyeyeon Park
         * @since 2019.08.28
         */
        @JvmStatic
        fun getReviewAsync(productId: Long, reviewId: Long): Deferred<BaseModel<MyPageReviewContent>> =
                GlobalScope.async {
                    RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                            .getReviewAsync(productId = productId, reviewId = reviewId).await()
                }

        /**
         * 비즈니스 셀러 조회
         * @author Hyeyeon Park
         * @since 2019.09.03
         */
        @JvmStatic
        fun getBusinessSeller(listener: OnServerListener, sellerId: Long) =
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getBusinessSeller(sellerId = sellerId).enqueue(
                        ServerCallbackUtil.ServerResponseCallback<BaseModel<BusinessSeller>> { successResponse -> listener.onResult(true, successResponse.body()) })


        /**
         * 마이페이지 내가 작성한 리뷰 리스트
         */
        @JvmStatic
        fun checkExistSnsUser2(listener: OnServerListener, snsType: String, snsId: String, email: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .checkExistSnsUser(snsType, snsId, email).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 본인인증 정보 업데이트
         * @param verification birth, diCode, gender, identityVerifyMethod, mobile, name 만 사용
         * @author Hyeyeon Park
         * @since 2019.09.11
         */
        @JvmStatic
        fun updateIdentityVerify(listener: OnServerListener, accessToken: String, verification: Verification) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).updateIdentityVerify(accessToken, verification)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 셀러 스토어 정보 조회 API
         * @author Hyeyeon Park
         * @since 2019.09.19
         */
        @JvmStatic
        fun getSellerStoreInfo(listener: OnServerListener, sellerId: Long, accessToken: String?) {
            if (accessToken.isNullOrEmpty())
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerStoreInfo(sellerId = sellerId)
                        .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerStore>>(successTask = { listener.onResult(true, it.body()) }))
            else
                RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getSellerStoreInfo(accessToken = accessToken, sellerId = sellerId)
                        .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<SellerStore>>(successTask = { listener.onResult(true, it.body()) }))
        }

        /**
         * 본인인증 데이터 여부 조회
         * resultCode 200: 본인인증 완료; 5004: 본인인증 미완료
         * @author Hyeyeon Park
         * @since 2019.09.23
         */
        @JvmStatic
        fun getIdentityVerify(listener: OnServerListener, jsonObject: JsonObject) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).getIdentityVerify(jsonObject = jsonObject)
                .enqueue(object : Callback<BaseModel<Any>> {
                    override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                        listener.onResult(false, t.message)
                    }

                    override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                        listener.onResult(true, response.body())
                    }
                })


        /**
         * 이메일 본인인증 업데이트
         * @author Hyeyeon Park
         * @since 2019.09.23
         */
        @JvmStatic
        fun updateEmailVerify(listener: OnServerListener, accessToken: String, jsonObject: JsonObject) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).updateEmailVerify(accessToken = accessToken, jsonObject = jsonObject)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 마이페이지 내가 작성한 리뷰 리스트
         */
        @JvmStatic
        fun getUserReviewUrl(listener: OnServerListener, accessToken: String) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getUserReviewUrl(accessToken).enqueue(object : Callback<BaseModel<JsonObject>> {
                        override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 커뮤니티 좋아요 가져오기
         * @author park jungho
         * @since 2019.12.02
         */
        @JvmStatic
        fun getLikes(listener: OnServerListener, accessToken: String, target: String, targetId: Long, userId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .getLikes(accessToken, target, targetId, userId).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 커뮤니티 좋아요 저장하기
         * @since 2019.12.02
         * @author park jungho
         */
        @JvmStatic
        fun saveLikes(listener: OnServerListener, accessToken: String, userId: Long, response: LikesModel) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .saveLikes(accessToken, userId, response).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }

        /**
         * 커뮤니티 좋아요 삭제
         * @since 2019.12.02
         * @author park jungho
         */
        @JvmStatic
        fun deleteLikes(listener: OnServerListener, accessToken: String, target: String, targetId: Long, userId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .deleteLikes(accessToken, userId, target, targetId).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        /**
         * 닉네임으로 유저 정보 가져오기
         * @since 2019.09.28
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun getUserByNickName(listener: OnServerListener, nickName: String) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true).getUserByNickName(nickName = nickName)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 은행 정보 가져오기
         * @since 2019.09.28
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun getBanks(listener: OnServerListener) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).getBanks()
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<MutableList<PurchaseOrder.Bank>>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 연동된 sns 종류 불러오기
         */
        @JvmStatic
        fun checkSnsUserType(listener: OnServerListener, userId: Long) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .checkSnsUserType(userId).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
                    )
        }


        @JvmStatic
        fun sendEmailToPhone(listener: OnServerListener, jsonObject: JsonObject) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .sendEmailToPhone(jsonObject).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }


        @JvmStatic
        fun updateUserInfo(listener: OnServerListener, accessToken: String, userId: Long, userInfo: UserUpdateInfo) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .updateUserInfo(accessToken, userId, userInfo).enqueue(object : Callback<BaseModel<JsonObject>> {
                        override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("getMypageReviewList", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }

        /**
         * 팔로우한 스토어 조회
         * @since 2019.10.14
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun getFollowingStores(listener: OnServerListener, userId: Long) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).getFollowingStores(userId = userId)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<MutableList<SellerStore>>>(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 럭키드로우 이메일 회원가입
         * @author Hyeyeon Park
         * @since 2019.11.13
         */
        @JvmStatic
        fun signUpEventUser(listener: OnServerListener, eventUser: EventUser) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).signUpEventUser(eventUser = eventUser)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))

        /**
         * 럭키드로우 이메일 회원가입
         * @author Hyeyeon Park
         * @since 2019.11.13
         */
        @JvmStatic
        fun signUpEventSnsUser(listener: OnServerListener, eventUser: EventUser) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).signUpEventSnsUser(eventUser = eventUser)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 럭키드로우 유저 정보 조회
         * @author Hyeyeon Park
         * @since 2019.11.14
         */
        @JvmStatic
        fun getEventUser(listener: OnServerListener, accessToken: String) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).getEventUser(accessToken = accessToken)
                .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<EventUser>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 럭키드로우 유저 정보 수정
         * @author Hyeyeon Park
         * @since 2019.11.14
         */
        fun updateEventUser(listener: OnServerListener, accessToken: String, eventUser: EventUser) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false)
                .updateEventUser(accessToken = accessToken, eventUser = eventUser).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 회원탈퇴
         * @author Hyeyeon Park
         * @since 2019.11.21
         */
        fun withdraw(listener: OnServerListener, accessToken: String) = RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false)
                .withdraw(accessToken = accessToken).enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<Any>>(successTask = { listener.onResult(true, it.body()) }))


        /**
         * 비밀번호 확인 api
         * @author park jungho
         * @since 2019.12.10
         */
        @JvmStatic
        fun passwordCheck(listener: OnServerListener, accessToken: String, userId: Long, body: JsonObject) {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true)
                    .passwordCheck(accessToken, userId, body).enqueue(object : Callback<BaseModel<Any>> {
                        override fun onResponse(call: Call<BaseModel<Any>>, response: Response<BaseModel<Any>>) {
                            resultListener(listener, call, response)
                        }

                        override fun onFailure(call: Call<BaseModel<Any>>, t: Throwable) {
                            if (CustomLog.flag) CustomLog.L("passwordCheck", "onFailure", t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    })
        }

        /**
         * 토큰 갱신
         * @author Hyeyeon Park
         */
        @JvmStatic
        fun refreshTokenAsync(authorization: String, refresh_token: String): Deferred<Token> = GlobalScope.async {
            RetrofitManager.createService(Type.Server.USER, UserService::class.java, true, false).refreshTokenAsync(authorization = authorization, refresh_token = refresh_token, grant_type = "refresh_token").await()
        }


    }
}