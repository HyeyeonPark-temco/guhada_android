package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.point.PointSummary
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.point.Point
import io.temco.guhada.data.model.point.PointHistory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface BenefitService {
    /**
     * 포인트 Summary 조회 API
     */
    @GET("/summary")
    fun getPointSummary(@Header("Authorization") accessToken: String, @Query("expireDays") expireDays: Int): Call<BaseModel<PointSummary>>

    /**
     * 포인트 리스트 조회 API
     */
    @GET("/points")
    fun getPoints(@Header("Authorization") accessToken: String, @Query("orderType") orderType: String,
                  @Query("sortType") sortType: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<Point>>

    /**
     * 포인트 리스트 기간별 조회 API
     */
    @GET("/histories")
    fun getPointHistories(@Header("Authorization") accessToken: String, @Query("charge") charge: Boolean,
                          @Query("historyStatus") historyStatus: String, @Query("orderType") orderType: String, @Query("sortType") sortType: String,
                          @Query("fromAt") fromAt: String, @Query("toAt") toAt: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int, @Query("userId") userId: Int): Call<BaseModel<PointHistory>>
}