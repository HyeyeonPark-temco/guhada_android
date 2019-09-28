package io.temco.guhada.data.retrofit.service

import com.google.gson.JsonObject
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.Claim
import io.temco.guhada.data.model.claim.ClaimResponse
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.model.order.CancelOrderStatus
import io.temco.guhada.data.model.order.OrderHistoryResponse
import io.temco.guhada.data.model.order.PurchaseOrder
import retrofit2.Call
import retrofit2.http.*

interface ClaimService {
    /**
     * 상품 문의 가져오기 API (로그인 시)
     * @param productId
     * @param isMyInquiry 검색 조건 (내 문의 가져오기)
     * @param status 검색 조건 (미답변, 답변 완료)
     * @param pageNo
     * @param size
     */
    @GET("products/{id}/inquiries")
    fun getClaims(@Header("Authorization") accessToken: String, @Path("id") productId: Long, @Query("isMyInquiry") isMyInquiry: Boolean?, @Query("pageNo") pageNo: Int,
                  @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

    /**
     * 상품 문의 가져오기 API (비로그인 시)
     * @param productId
     * @param status 검색 조건 (미답변, 답변 완료)
     * @param pageNo
     * @param size
     */
    @GET("products/{id}/inquiries")
    fun getClaimsForQuest(@Path("id") productId: Long, @Query("pageNo") pageNo: Int,
                          @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

    /**
     * 상품 문의 작성하기 API
     */
    @POST("products/{id}/inquiries")
    fun saveClaim(@Header("Authorization") accessToken: String, @Path("id") productId: Long, @Body inquiry: Inquiry): Call<BaseModel<Claim>>


    /**
     * 상품 문의 수정하기 API
     */
    @PUT("products/{id}/inquiries")
    fun editClaim(@Header("Authorization") accessToken: String, @Path("id") productId: Long, @Body inquiry: Inquiry): Call<BaseModel<Claim>>

    /**
     * 마이페이지 상품 문의 리스트 API
     */
    @GET("users/my-page/inquiries")
    fun getMyClaimList(@Header("Authorization") accessToken: String, @Query("page") page: Int, @Query("status") status: String, @Query("size") size: Int = 20): Call<BaseModel<MyPageClaim>>


    /**
     * 상품 상세 문의 삭제 API
     */
    @DELETE("products/{productId}/inquiries/{inquiryId}")
    fun deleteClaim(@Header("Authorization") accessToken: String, @Path("productId") productId: Long, @Path("inquiryId") inquiryId: Long): Call<BaseModel<JsonObject>>

    /**
     * 주문 취소/교환/반품 리스트 조회 API
     */
    @GET("/order-claim/my-cancel-order-list")
    fun getCancelOrderList(@Header("Authorization") accessToken: String, @Query("startTimestamp") startTimestamp: Long, @Query("endTimestamp") endTimestamp: Long, @Query("page") page: Int): Call<BaseModel<OrderHistoryResponse>>

    /**
     * 주문 취소/교환/반품 상태 조회 API
     */
    @GET("/order-claim/my-cancel-orders-status-list")
    fun getCancelOrderStatus(@Header("Authorization") accessToken: String, @Query("startTimestamp") startTimestamp: Long, @Query("endTimestamp") endTimestamp: Long): Call<BaseModel<CancelOrderStatus>>

    /**
     * 주문 취소 신청 API
     */
    @POST("/order-claim/order-cancel")
    fun cancelOrder(@Header("Authorization") accessToken: String, @Body cancelRequest: CancelRequest): Call<BaseModel<PurchaseOrder>>

    /**
     * 교환 신청 API
     */
    @POST("/order-claim/order-exchange")
    fun requestExchange(@Header("Authorization") accessToken: String, @Body exchangeRequest: ExchangeRequest): Call<BaseModel<PurchaseOrder>>

    /**
     * 반품 신청 API
     */
    @POST("/order-claim/order-return")
    fun requestRefund(@Header("Authorization") accessToken: String, @Body refundRequest: RefundRequest): Call<BaseModel<PurchaseOrder>>

    /**
     * 주문교환반품 신청 화면 정보 API
     */
    @GET("/order-claim/claim-form/{orderProdGroupId}")
    fun getClaimForm(@Header("Authorization") accessToken: String, @Path("orderProdGroupId") orderProdGroupId: Long): Call<BaseModel<PurchaseOrder>>

    /**
     * 주문교환반품 신청서 수정 화면 정보 API
     */
    @GET("/order-claim/claim-update-form/{orderClaimId}")
    fun getUpdateClaimForm(@Header("Authorization") accessToken: String, @Path("orderClaimId") orderClaimId: Long): Call<BaseModel<PurchaseOrder>>

    /**
     * 반품 철회 API
     */
    @DELETE("/order-claim/order-return-withdraw")
    fun withdrawRefund(@Header("Authorization") accessToken: String, @Query("orderClaimId") orderClaimId: Long): Call<BaseModel<Any>>

    /**
     * 교환 철회 API
     */
    @DELETE("/order-claim/order-exchange-withdraw")
    fun withdrawExchange(@Header("Authorization") accessToken: String, @Query("orderClaimId") orderClaimId: Long): Call<BaseModel<Any>>

    /**
     * 반품 신청서 수정 API
     */
    @POST("/order-claim/update-order-return")
    fun updateRefund(@Header("Authorization") accessToken: String, @Body refundRequest: RefundRequest): Call<BaseModel<Any>>

    /**
     * 교환 신청서 수정 API
     */
    @POST("/order-claim/update-order-exchange")
    fun updateExchange(@Header("Authorization") accessToken: String, @Body exchangeRequest: ExchangeRequest): Call<BaseModel<Any>>


    /**
     * 신고하기 유형 가져오기 API
     */
    @GET("users/report/report-types")
    fun getReportType(): Call<BaseModel<ReportTypeData>>


    /**
     * 신고하기 이미지 업로드 URL 가져오기 API
     */
    @GET("users/report/image-upload-url"/*"/users/report/photo-upload-url"*/)
    fun getReportUserPhotoUrl(@Header("Authorization") accessToken: String): Call<BaseModel<JsonObject>>


    /**
     * 신고하기 작성하기 API
     */
    @POST("users/report")
    fun saveReport(@Header("Authorization") accessToken: String, @Body report: ReportResponse): Call<BaseModel<Any>>


    /**
     * User 문의하기 type 가져오기 - Guhada 문의
     */
    @GET("/users/claims/types")
    fun getUserClaimGuhadaTypeList(): Call<BaseModel<UserClaimGuhadaType>>


    /**
     * User 문의하기 이미지 업로드 URL 가져오기
     */
    @GET("/users/claims/image-upload-url")
    fun gettUserClaimGuhadaImage(@Header("Authorization") accessToken: String): Call<BaseModel<JsonObject>>


    /**
     * USER 구하다 문의하기
     */
    @POST("/users/{userId}/claims")
    fun saveUserClaimGuhada(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Body param: UserClaimGuhadaResponse): Call<BaseModel<Any>>


    /**
     * 판매자 문의하기 type 가져오기
     */
    @GET("/users/seller-claims/types")
    fun getUserClaimSellerTypeList(): Call<BaseModel<UserClaimSellerType>>


    /**
     * 판매자 문의하기 이미지 업로드 URL 가져오기
     */
    @GET("/users/seller-claims/image-upload-url")
    fun getUserClaimSellerImage(@Header("Authorization") accessToken: String): Call<BaseModel<JsonObject>>


    /**
     * 판매자 문의하기
     */
    @POST("/users/{userId}/seller-claims")
    fun saveUserClaimSeller(@Header("Authorization") accessToken: String, @Path("userId") userId: Long, @Body param: UserClaimSellerResponse): Call<BaseModel<Any>>

    /**
     * 환불 예상 금액 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.26
     */
    @GET("/order-claim/claim-complete-form/{orderClaimGroupId}")
    fun getExpectedRefundPrice(@Header("Authorization") accessToken: String, @Path("orderClaimGroupId") orderClaimGroupId: Long): Call<BaseModel<ExpectedRefundPrice.ExpectedRefuncInfo>>

    /**
     * 취소신청, 반품신청 환불 예상 금액 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.26
     */
    @GET("/order-claim/refund-price")
    fun getExpectedRefundPriceForRequest(@Header("Authorization") accessToken: String, @Query("orderProdGroupId") orderProdGroupId : Long, @Query("quantity") quantity: Int): Call<BaseModel<ExpectedRefundPrice>>


}