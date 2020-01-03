package io.temco.guhada.data.retrofit.service

import com.google.gson.JsonObject
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.naver.NaverResponse
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.point.PointPopupInfo
import io.temco.guhada.data.model.review.*
import io.temco.guhada.data.model.seller.*
import io.temco.guhada.data.model.user.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*

/**
 * BASE URL: dev.user.guhada.com/
 */
interface UserService {

    /**
     * 회원 정보 가져오기 API
     */
    @GET("/users")
    fun getUserInfo(@Query("userIds") userIds: IntArray): Call<BaseModel<User>>

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
     * 개별 회원 정보 조회 API
     * @param userId
     */
    @GET("/users/{userId}")
    fun findUserById(@Path("userId") id: Int): Call<BaseModel<User>>

    /**
     * 개별 회원 정보 조회 API 2. GET : /users -> 유저 데이터 불러오기
     * @param userId
     */
    @GET("/users")
    fun findUsers(@Query("userIds") id: Int): Call<BaseModel<User>>

    /**
     * 셀러 정보 조회 API
     * @param sellerId
     */
    @GET("/sellers/{sellerId}")
    fun getSellerById(@Path("sellerId") id: Long): Call<BaseModel<Seller>>

    /**
     * 셀러 정보 조회 API (비동기)
     */
    @GET("/sellers/{sellerId}")
    fun getSellerByIdAsync(@Path("sellerId") id: Long): Deferred<BaseModel<Seller>>

    /**
     * 비즈니스 셀러 정보 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.03
     */
    @GET("/sellers/business-user")
    fun getBusinessSeller(@Query("seller-id") sellerId: Long): Call<BaseModel<BusinessSeller>>

    /**
     * 셀러 기본 반품지 가져오기 API
     * @param sellerId
     */
    @GET("/sellers/{sellerId}/default-return")
    fun getSellerDefaultReturnAddress(@Path("sellerId") id: Long): Call<BaseModel<SellerAddress>>

    /**
     * 유저 정보 가져오기 API
     * @param name
     * @param phoneNumber
     */
    @POST("/users/findUserId")
    fun findUserId(@Body body: JsonObject): Call<BaseModel<User>>

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
     * 인증번호로 비밀번호 재설정 API
     * @param verification [field] email, newPassword, verificationNumber
     */
    @POST("/verify/change-password")
    fun changePassword(@Body verification: Verification): Call<BaseModel<Any>>

    /**
     * 본인인증으로 비밀번호 재설정 API
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
     * 존재하는 SNS 계정인지 검증 API (deprecated)
     * @param snsType GOOGLE/FACEBOOK/NAVER/KAKAO
     * @param snsId
     */
//    @GET("/sns/{snsType}/sns-ids/{snsId}")
//    fun checkExistSnsUser(@Path("snsType") snsType: String, @Path("snsId") snsId: String): Call<BaseModel<Any>>

    /**
     * 존재하는 SNS 계정인지 검증 API
     * @param snsType GOOGLE/FACEBOOK/NAVER/KAKAO
     * @param snsId
     * @param email
     */
    @GET("/users/sns")
    fun checkExistSnsUser(@Query("sns-type") snsType: String, @Query("uid") snsId: String, @Query("email") email: String): Call<BaseModel<Any>>

    /**
     * 연동된 sns 종류 불러오기
     * @param userId
     */
    @GET("/sns-users/{userId}/sns-type")
    fun checkSnsUserType(@Path("userId") userId: Long): Call<BaseModel<Any>>

    /**
     * SNS 회원가입 API
     */
    @POST("/sns-users")
    fun joinSnsUser(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 페이스북 로그인 API
     */
    @POST("/sns-users/facebookLogin")
    fun facebookLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 구글 로그인 API
     */
    @POST("/sns-users/googleLogin")
    fun googleLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 카카오톡 로그인 API
     */
    @POST("/sns-users/kakaoLogin")
    fun kakaoLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 네이버 로그인 API
     */
    @POST("/sns-users/naverLogin")
    fun naverLogin(@Body user: SnsUser): Call<BaseModel<Token>>

    /**
     * 네이버 유저 프로필 가져오기 API
     */
    @GET("/v1/nid/me")
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

    @GET("/products/{productId}/reviews")
    fun getProductReviewWithRating(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int, @Query("rating") rating: String): Call<BaseModel<ReviewResponse>>

    @GET("/products/{productId}/reviews")
    fun getProductReviewWithSorting(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int, @Query("sort") sorting: String): Call<BaseModel<ReviewResponse>>

    @GET("/products/{productId}/reviews")
    fun getProductReviewWithSortingAndRating(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int, @Query("sort") sorting: String, @Query("rating") rating: String): Call<BaseModel<ReviewResponse>>

    /**
     * 상품 리뷰 리스트(포토 리뷰) 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.25
     */
    @GET("/products/{productId}/reviews/photo")
    fun getProductPhotoReview(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int): Call<BaseModel<ReviewResponse>>

    @GET("/products/{productId}/reviews/photo")
    fun getProductPhotoReviewWithSorting(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int, @Query("sort") sorting: String): Call<BaseModel<ReviewResponse>>

    /**
     * 상품 리뷰 리스트(개인 수치 포함) 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.25
     */
    @GET("/products/{productId}/reviews/user-size")
    fun getProductSizeReview(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int): Call<BaseModel<ReviewResponse>>

    @GET("/products/{productId}/reviews/user-size")
    fun getProductSizeReviewWithSorting(@Path("productId") productId: Long, @Query("page") page: Int, @Query("size") size: Int, @Query("sort") sorting: String): Call<BaseModel<ReviewResponse>>

    /**
     * 셀러 만족도 조회 API
     */
    @GET("/sellers/{sellerId}/purchase-satisfaction")
    fun getSellerSatisfaction(@Path("sellerId") sellerId: Long): Call<BaseModel<SellerSatisfaction>>

    /**
     * 셀러 팔로우 조회 API
     */
    @GET("/sellers/{sellerId}/followers")
    fun getSellerFollowers(@Header("Authorization") accessToken: String, @Path("sellerId") sellerId: Long): Call<BaseModel<SellerFollower>>

    /**
     * BookMark 정보 조회 API
     * target: PRODUCT, DEAL, BBS, COMMENT, STORE, REVIEW, SELLER
     *
     * park jungho
     * 19.07.31
     * like -> bookmarks 로 변경
     */
    @GET("/users/{userId}/bookmarks")
    fun getLike(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Query("targetId") targetId: Long, @Query("target") target: String): Call<BaseModel<BookMark>>

    /**
     * BookMark 정보 조회 API
     * target: PRODUCT, DEAL, BBS, COMMENT, STORE, REVIEW, SELLER
     * @see io.temco.guhada.common.enum.BookMarkTarget
     * @author Hyeyeon Park
     *
     * park jungho
     * 19.07.31
     * like -> bookmarks 로 변경
     * /users/{userId}/bookmarks   회원 북마크 정보 가져오기 특정 타겟의 targetId의 정보가 있는지 확인
     */
    @GET("/users/{userId}/bookmarks")
    fun getBookMark(@Header("Authorization") accessToken: String, @Path("userId") userId: Int, @Query("target") target: String, @Query("targetId") targetId: Long): Call<BaseModel<BookMark>>

    @GET("/users/{userId}/bookmarks")
    fun getBookMarkWithoutTargetId(@Header("Authorization") accessToken: String, @Path("userId") userId: Int, @Query("target") target: String): Call<BaseModel<BookMark>>

    @GET("/users/{userId}/bookmarks")
    fun getBookMarkWithoutTargetIdAndTarget(@Header("Authorization") accessToken: String, @Path("userId") userId: Int): Call<BaseModel<BookMark>>

    /**
     * BookMark 정보 추가
     * target: PRODUCT, DEAL, BBS, COMMENT, STORE, REVIEW, SELLER
     *
     * park jungho
     * 19.08.01
     * /users/bookmarks
     */
    @POST("/users/bookmarks")
    fun saveBookMark(@Header("Authorization") accessToken: String, @Body response: JsonObject): Call<BaseModel<Any>>

    /**
     * BookMark 정보 추가 (비동기)
     * @author Hyeyeon Park
     * @since 2019.08.27
     */
    @POST("/users/bookmarks")
    fun saveBookMarkAsync(@Header("Authorization") accessToken: String, @Body response: JsonObject): Deferred<BaseModel<Any>>

    /**
     * BookMark 정보 삭제
     * target: PRODUCT, DEAL, BBS, COMMENT, STORE, REVIEW, SELLER
     *
     * park jungho
     * 19.08.01
     * /users/bookmarks
     */
    @DELETE("/users/bookmarks")
    fun deleteBookMark(@Header("Authorization") accessToken: String, @Query("target") target: String, @Query("targetId") targetId: Long): Call<BaseModel<Any>>

    /**
     * BookMark 정보 삭제 (비동기)
     * @author Hyeyeon Park
     * @since 2019.08.27
     */
    @DELETE("/users/bookmarks")
    fun deleteBookMarkAsync(@Header("Authorization") accessToken: String, @Query("target") target: String, @Query("targetId") targetId: Long): Deferred<BaseModel<Any>>

    /**
     * BookMark 정보 추가
     * target: PRODUCT, DEAL, BBS, COMMENT, STORE, REVIEW, SELLER
     *
     * park jungho
     * 19.08.01
     * /users/bookmarks
     */
    @DELETE("/users/bookmarks")
    fun deleteBookMarkAll(@Header("Authorization") accessToken: String, @Query("target") target: String): Call<BaseModel<Any>>

    /**
     * BookMark Count 조회 API
     * @see io.temco.guhada.common.enum.BookMarkTarget
     * @since 2019.08.27
     * @author Hyeyeon Park
     */
    @GET("/users/bookmarks/{target}/{targetId}/count")
    fun getBookMarkCount(@Path("target") target: String, @Path("targetId") targetId: Long): Call<BaseModel<BookMarkCountResponse>>

    /**
     * BookMark Count 조회 API (비동기)
     * @see io.temco.guhada.common.enum.BookMarkTarget
     * @since 2019.08.27
     * @author Hyeyeon Park
     */
    @GET("/users/bookmarks/{target}/{targetId}/count")
    fun getBookMarkCountAsync(@Path("target") target: String, @Path("targetId") targetId: Long): Deferred<BaseModel<BookMarkCountResponse>>


    /**
     * 마이페이지 내가 작성한 리뷰 목록
     *
     * park jungho
     * 19.08.08
     */
    @GET("/users/my-page/reviews")
    fun getMypageReviewList(@Header("Authorization") accessToken: String, @Query("page") page: Int, @Query("size") size: Int,
                            @Query("sort")sort:String = "created_at,desc"): Call<BaseModel<MyPageReview>>


    /**
     * 마이페이지 내가 작성한 리뷰 삭제
     *
     * park jungho
     * 19.08.09
     */
    @DELETE("/products/{productId}/reviews/{reviewId}")
    fun deleteReviewData(@Header("Authorization") accessToken: String, @Path("productId") productId: Long, @Path("reviewId") reviewId: Long): Call<BaseModel<Any>>


    /**
     * 리뷰 작성하기
     *
     * park jungho
     * 19.08.12
    @FormUrlEncoded
     */
    @POST("/products/{productId}/reviews")
    fun writeReview(@Header("Authorization") accessToken: String, @Path("productId") productId: Long, @Body param: ReviewWrMdResponse): Call<BaseModel<ReviewData>>


    /**
     * 리뷰 수정하기
     *
     * park jungho
     * 19.08.12
    @FormUrlEncoded
     */
    @PUT("/products/{productId}/reviews/{reviewId}")
    fun modifyReview(@Header("Authorization") accessToken: String, @Path("productId") productId: Long, @Path("reviewId") reviewId: Int,
                     @Body param: ReviewWrMdResponse): Call<BaseModel<Any>>
    /*
    fun modifyReview(@Header("Authorization") accessToken: String, @Path("productId") productId: Long, @Path("reviewId") reviewId: Int,
                     @FieldMap param: HashMap<String, Any>): Call<BaseModel<Any>>
     */


    /**
     * 특정 리뷰 조회 API
     * @author Hyeyeon Park
     * @since 2019.08.28
     */
    @GET("/products/{productId}/reviews/{reviewId}")
    fun getReviewAsync(@Path("productId") productId: Long, @Path("reviewId") reviewId: Long): Deferred<BaseModel<MyPageReviewContent>>

    /**
     * 회원 신체 사이즈 정보 가져오기
     *
     * park jungho
     * 19.08.16
     */
    @GET("/users/user-size")
    fun getUserSize(@Header("Authorization") accessToken: String): Call<BaseModel<UserSize>>


    /**
     * 회원 신체 사이즈 정보 저장하기
     *
     * park jungho
     * 19.08.16
     */
    @POST("/users/user-size")
    fun saveUserSize(@Header("Authorization") accessToken: String, @Body response: UserSize): Call<BaseModel<PointPopupInfo>>


    /**
     * 회원 신체 사이즈 정보 수정하기
     *
     * park jungho
     * 19.08.16
     */
    @PUT("/users/user-size")
    fun modifyUserSize(@Header("Authorization") accessToken: String, @Body response: UserSize): Call<BaseModel<Any>>

    /**
     * 본인인증 정보 업데이트 API
     * @author Hyeyeon Park
     * @since 2019.09.11
     */
    @PUT("/users/identity-verify")
    fun updateIdentityVerify(@Header("Authorization") accessToken: String, @Body verification: Verification): Call<BaseModel<Any>>

    /**
     * 본인인증 데이터 여부 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.23
     */
    @POST("/users/identity-verify")
    fun getIdentityVerify(@Body jsonObject: JsonObject): Call<BaseModel<Any>>

    /**
     * 셀러 스토어 정보 조회 API (비로그인)
     * @param sellerId seller id
     * @author Hyeyeon Park
     * @since 2019.09.19
     */
    @GET("/sellers/{sellerId}/store")
    fun getSellerStoreInfo(@Path("sellerId") sellerId: Long): Call<BaseModel<SellerStore>>

    /**
     * 셀러 스토어 정보 조회 API (로그인)
     * @param accessToken access token
     * @param sellerId seller id
     * @author Hyeyeon Park
     * @since 2019.09.19
     */
    @GET("/sellers/{sellerId}/store")
    fun getSellerStoreInfo(@Header("Authorization") accessToken: String, @Path("sellerId") sellerId: Long): Call<BaseModel<SellerStore>>

    /**
     * 이메일 본인인증 업데이트 API
     * @author Hyeyeon Park
     * @since 2019.09.23
     */
    @PUT("/users/email-verify")
    fun updateEmailVerify(@Header("Authorization") accessToken: String, @Body jsonObject: JsonObject): Call<BaseModel<Any>>

    /**
     * review image 업로드 url
     *
     * park jungho
     * 19.08.16
     */
    @GET("/products/reviews/image-upload-url")
    fun getUserReviewUrl(@Header("Authorization") accessToken: String): Call<BaseModel<JsonObject>>


    /**
     * 회원 좋아요 가져오기
     * @author park jungho
     * @since 2019.12.02
     */
    @GET("/users/{userId}/likes")
    fun getLikes(@Header("Authorization") accessToken: String, @Query("target") target: String, @Query("targetId") targetId: Long, @Query("userId") userId: Long): Call<BaseModel<Any>>


    /**
     * 회원 좋아요 정보 추가
     * @author park jungho
     * @since 2019.12.02
     */
    @POST("/users/{userId}/likes")
    fun saveLikes(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Body response: LikesModel): Call<BaseModel<Any>>


    /**
     * 회원 좋아요 정보 삭제
     * @author park jungho
     * @since 2019.12.02
     */
    @DELETE("/users/{userId}/likes")
    fun deleteLikes(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Query("target") target: String, @Query("targetId") targetId: Long): Call<BaseModel<Any>>


    /**
     * 회원 좋아요 가져오기
     * /users/{userId}/likes/{id}
     * @author park jungho
     * @since 2019.12.02
     */
    @GET("/users/{userId}/likes/{id}")
    fun getUserLike(@Header("Authorization") accessToken: String, @Path("userId ") userId : Long, @Path("id ") id : Long): Call<BaseModel<Any>>


    /**
     * 회원 좋아요 COUNT 가져오기
     * /users/likes/{target}/count
     * @author park jungho
     * @since 2019.12.02
     */
    @GET("/users/{userId}/likes/{id}")
    fun getUserLikeCount(@Header("Authorization") accessToken: String, @Path("userId ") userId : Long, @Path("id ") id : Long): Call<BaseModel<Any>>


    /**
     * 닉네임으로 유저 정보 가져오기 API
     * @since 2019.09.28
     * @author Hyeyeon Park
     */
    @GET("/users/nickname/{nickname}")
    fun getUserByNickName(@Path("nickname") nickName: String): Call<BaseModel<Any>>


    /**
     * 은행 정보 가져오기 API
     * @since 2019.09.28
     * @author Hyeyeon Park
     */
    @GET("/banks")
    fun getBanks(): Call<BaseModel<MutableList<PurchaseOrder.Bank>>>


    /**
     * 아이디 발송하기
     * @since 2019.09.29
     * @author Hyeyeon Park
     */
    @POST("/notification/mobile/id")
    fun sendEmailToPhone(@Body jsonObject: JsonObject): Call<BaseModel<Any>>


    /**
     * 회원 정보 수정하기
     * @param userId
     */
    @PUT("/users/{userId}")
    fun updateUserInfo(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Body userinfo: UserUpdateInfo): Call<BaseModel<JsonObject>>

    /**
     * 팔로우한 스토어 조회
     * @since 2019.10.14
     * @author Hyeyeon Park
     */
    @GET("/users/{userId}/followings")
    fun getFollowingStores(@Path("userId") userId: Long): Call<BaseModel<MutableList<SellerStore>>>

    /**
     * 럭키드로우 이메일 회원가입 API
     * response: email (String)
     * @author Hyeyeon Park
     * @since 2019.11.13
     */
    @POST("/event/users")
    fun signUpEventUser(@Body eventUser: EventUser): Call<BaseModel<Any>>

    /**
     * 럭키드로우 SNS 회원가입 API
     * response: email (String)
     * @author Hyeyeon Park
     * @since 2019.11.14
     */
    @POST("/event/sns-users")
    fun signUpEventSnsUser(@Body eventUser: EventUser): Call<BaseModel<Any>>

    /**
     * 럭키드로우 유저 정보 조회 API
     * @author Hyeyeon Park
     * @since 2019.11.14
     */
    @GET("/event/users")
    fun getEventUser(@Header("Authorization") accessToken: String): Call<BaseModel<EventUser>>

    /**
     * 럭키드로우 유저 정보 수정 API
     * response: email (String)
     * @author Hyeyeon Park
     * @since 2019.11.14
     */
    @PUT("/event/users")
    fun updateEventUser(@Header("Authorization") accessToken: String, @Body eventUser: EventUser): Call<BaseModel<Any>>

    /**
     * 회원탈퇴
     * @author Hyeyeon Park
     * @since 2019.11.21
     */
    @DELETE("/users/withdraw")
    fun withdraw(@Header("Authorization") accessToken: String) : Call<BaseModel<Any>>



    /**
     * 비밀번호 확인 api
     * @author park jungho
     * @since 2019.12.10
     */
    @POST("/users/{userId}/password")
    fun passwordCheck(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Body body: JsonObject) : Call<BaseModel<Any>>

    /**
     * 토큰 갱신 api
     * @author Hyeyeon Park
     */
    @FormUrlEncoded
    @POST("/oauth/token")
    fun refreshTokenAsync(@Header("Authorization") authorization : String, @Field("refresh_token") refresh_token: String, @Field("grant_type") grant_type : String) : Deferred<Token>
}