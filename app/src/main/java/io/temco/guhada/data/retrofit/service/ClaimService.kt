package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.ClaimResponse
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ClaimService {
    /**
     * 상세문의 가져오기
     * @param isMyInquiry 검색 조건 (내 문의 가져오기)
     * @param status 검색 조건 (미답변, 답변 완료)
     */
    @GET("products/{id}/inquiries")
    fun getClaims(@Path("id") productId: Int, @Query("isMyInquiry") isMyInquiry: Boolean?, @Query("pageNo") pageNo: Int,
                  @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

    @GET("products/{id}/inquiries")
    fun getClaimsForQuest(@Path("id") productId: Int, @Query("pageNo") pageNo: Int,
                          @Query("size") size: Int, @Query("status") status: String): Call<BaseModel<ClaimResponse>>

}