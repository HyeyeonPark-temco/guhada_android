package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.naver.NaverResponse
import io.temco.guhada.data.model.review.ReviewResponse
import io.temco.guhada.data.model.review.ReviewSummary
import io.temco.guhada.data.model.user.SnsUser
import io.temco.guhada.data.model.user.User
import retrofit2.Call
import retrofit2.http.*

/**
 * BASE URL: dev.user.guhada.com/
 */
interface UserService {

    /**
     * 회원가입 API
     */
    @POST("/signUpUser")
    fun signUp(@Body user: User): Call<BaseModel<Any>>

    /**
     * 이메일 로그인 API
     */
    @POST("/loginUser")
    fun signIn(@Body user: User): Call<BaseModel<Token>>

    /**
     * 개별 회원 정보 가져오기 API
     * @param userId
     */
    @GET("/users/{userId}")
    fun findUserById(@Path("userId") id: Int): Call<BaseModel<User>>

    /**
     * 셀러 정보 가져오기 API
     * @param sellerId
     */
    @GET("/sellers/{sellerId}")
    fun getSellerById(@Path("sellerId") id: Long): Call<BaseModel<Seller>>

    /**
     * 유저 정보 가져오기 API
     * @param name
     * @param phoneNumber
     */
    @GET("/findUserId")
    fun findUserId(
            @Query("name") name: String,
            @Query("phoneNumber") phoneNumber: String): Call<BaseModel<User>>

    /**
     * 나이스 본인인증 호출 토큰 발급 API
     */
    @GET("/phoneCertification")
    fun getVerifyToken(): Call<BaseModel<String>>

    /**
     * 인증번호 검증 API
     */
    @POST("/verify")
    fun verifyNumber(@Body verification: Verification): Call<BaseModel<Any>>

    /**
     * 이메일로 인증번호 발송 API
     */
    @POST("/verify/sendEmail")
    fun verifyEmail(@Body user: User): Call<BaseModel<Any>>

    /**
     * 휴대폰 번호로 인증번호 발송 API
     */
    @POST("/verify/sendMobile")
    fun verifyPhone(@Body user: User): Call<BaseModel<Any>>

    /**
     * 인증번호로 비밀번호 바꾸기 API
     * @param verification [field] email, newPassword, verificationNumber
     */
    @POST("/verify/change-password")
    fun changePassword(@Body verification: Verification): Call<BaseModel<Any>>

    /**
     * 본인인증으로 비밀번호 바꾸기 API
     * @param verification [field] diCode, newPassword, mobile
     */
    @POST("/verify/identity/change-password")
    fun changePasswordByIdentifying(@Body verification: Verification): Call<BaseModel<Any>>

    /**
     * 중복된 이메일인지 검증 API
     * @param email
     */
    @GET("/isEmailExist/{email}")
    fun checkEmail(@Path("email") email: String): Call<BaseModel<Any>>

    /**
     * 중복된 핸드폰 번호인지 검증 API
     * @param mobile
     */
    @GET("/isMobileExist/{mobile}")
    fun checkPhone(@Path("mobile") mobile: String): Call<BaseModel<Any>>

    /**
     * 존재하는 SNS 계정인지 검증 API
     * @param snsType GOOGLE/FACEBOOK/NAVER/KAKAO
     * @param snsId
     */
    @GET("/sns/{snsType}/sns-ids/{snsId}")
    fun checkExistSnsUser(@Path("snsType") snsType: String, @Path("snsId") snsId: String): Call<BaseModel<Any>>

    /**
     * 페이스북 로그인 API
     */
    @POST("/facebookLogin")
    fun facebookLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 구글 로그인 API
     */
    @POST("/googleLogin")
    fun googleLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 카카오톡 로그인 API
     */
    @POST("/kakaoLogin")
    fun kakaoLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 네이버 로그인 API
     */
    @POST("/naverLogin")
    fun naverLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 네이버 유저 프로필 가져오기 API
     */
    @GET("v1/nid//me")
    fun getNaverProfile(@Header("Authorization") auth: String): Call<NaverResponse>

    /**
     * 회원 배송지 정보 조회 API
     * @param userId
     * @return success: List<UserShipping>  failed: Object(result, data, resultCode)
     */
    @GET("/users/{userId}/shipping-addresses")
    fun findShippingAddress(@Path("userId") userId: Int): Call<BaseModel<MutableList<UserShipping>>>

    /**
     * 회원 배송지 삭제 API
     * @param userId
     */
    @DELETE("/users/{userId}/shipping-addresses")
    fun deleteShippingAddress(@Path("userId") userId: Int, @Query("shippingAddressId") shippingAddressId: Int): Call<BaseModel<Any>>

    /**
     * 회원 배송지 수정 API
     * @param userId
     */
    @PUT("/users/{userId}/shipping-addresses")
    fun updateShippingAddress(@Path("userId") userId: Int, @Query("shippingAddressId") shippingAddressId: Int, @Body shippingAddress: UserShipping): Call<BaseModel<UserShipping>>


    /**
     * 회원 배송지 추가 API
     * @param userId
     */
    @POST("/users/{userId}/shipping-addresses")
    fun saveShippingAddress(@Path("userId") userId: Int, @Body shippingAddress: UserShipping): Call<BaseModel<Any>>

    /**
     * 상품 리뷰 평점 및 그래프 정보 조회 API
     */
    @GET("/products/{productId}/reviews/summary")
    fun getProductReviewSummary(@Path("productId") productId: Long): Call<BaseModel<ReviewSummary>>

    /**
     * 상품 리뷰 조회 API
     */
    @GET("/products/{productId}/reviews")
    fun getProductReview(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int): Call<BaseModel<ReviewResponse>>

    /**
     * 셀러 만족도 조회 API
     */
    @GET("/sellers/{sellerId}/purchase-satisfaction")
    fun getSellerSatisfaction(@Path("sellerId") sellerId: Long): Call<BaseModel<SellerSatisfaction>>
}