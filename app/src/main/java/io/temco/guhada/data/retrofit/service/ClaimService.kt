package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.InquiryRequest
import io.temco.guhada.data.model.base.BaseModel
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
    fun getClaims(@Header("Authorization") accessToken: String, @Path("id") productId: Int, @Query("isMyInquiry") isMyInquiry: Boolean?, @Query("pageNo") pageNo: Int,
                  @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

    /**
     * 상품 문의 가져오기 API (비로그인 시)
     * @param productId
     * @param status 검색 조건 (미답변, 답변 완료)
     * @param pageNo
     * @param size
     */
    @GET("products/{id}/inquiries")
    fun getClaimsForQuest(@Path("id") productId: Int, @Query("pageNo") pageNo: Int,
                          @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

    /**
     * 상품 문의 작성하기 API
     */
    @POST("products/{id}/inquiries")
    fun saveClaim(@Header("Authorization") accessToken: String, @Path("id") productId: Int, @Body inquiry: InquiryRequest): Call<BaseModel<ClaimResponse.Claim>>

}