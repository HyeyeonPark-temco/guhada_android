package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.AvailableBenefitCount
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.coupon.CouponConsumption
import io.temco.guhada.data.model.coupon.CouponResponse
import io.temco.guhada.data.model.coupon.CouponSaveProcess
import io.temco.guhada.data.model.point.*
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.*

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

    /**
     * 쿠폰 조회 API
     */
    @GET("/coupon/wallet")
    fun getCoupons(@Header("Authorization") accessToken: String, @Query("isAvailable") isAvailable: Boolean, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<CouponResponse>>

    /**
     * 쿠폰 삭제 API
     */
    @DELETE("/coupon/wallet/{couponNumber}")
    fun deleteCoupon(@Header("Authorization") accessToken: String, @Path("couponNumber") couponNumber: String): Call<BaseModel<Any?>>

    /**
     * 쿠폰 삭제 API (비동기)
     */
    @DELETE("/coupon/wallet/{couponNumber}")
    fun deleteCouponAsync(@Header("Authorization") accessToken: String, @Path("couponNumber") couponNumber: String): Deferred<BaseModel<Any?>>

    /**
     * 쿠폰 사용 API
     * @author Hyeyeon Park
     * @since 2019.09.10
     */
    @POST("/coupons/process/consumption")
    fun useCoupon(@Header("Authorization") accessToken: String, @Body couponConsumption: CouponConsumption): Call<BaseModel<Any?>>

    /**
     * 쿠폰 발급 API
     * @author Hyeyeon Park
     * @since 2019.09.10
     */
    @POST("/coupons/process/save")
    fun saveCoupon(@Header("Authorization") accessToken: String, @Body couponSaveProcess: CouponSaveProcess): Call<BaseModel<Any?>>

    /**
     * 포인트 기록 삭제 API
     */
    @DELETE("/histories/{id}")
    fun deletePoint(@Header("Authorization") accessToken: String, @Path("id") pointId: Long): Call<BaseModel<Any?>>


    /**
     * 포인트 기록 삭제 API (비동기)
     */
    @DELETE("/histories/{id}")
    fun deletePointAsync(@Header("Authorization") accessToken: String, @Path("id") pointId: Long): Deferred<BaseModel<Any?>>

    /**
     * 적립 예정 포인트 조회 API
     * @author Hyeyeon Park
     * @since 2019.08.29
     */
    @POST("/process/due-save")
    fun getExpectedPoint(@Header("Authorization") accessToken: String, @Body pointRequest: PointRequest): Call<BaseModel<ExpectedPointResponse>>

    /**
     * 발급 가능한 쿠폰 조회 API
     * [required] DCategoryId, LCategoryId, MCategoryId, SCategoryId, dealId, paymentPrice, saveActionType, serviceType
     * @author Hyeyeon Park
     * @since 2019.08.29
     */
    @GET("/coupons/process/due-save")
    fun getExpectedCoupon(@Header("Authorization") accessToken: String, @Query("DCategoryId") DCategoryId: Long, @Query("LCategoryId") LCategoryId: Long,
                          @Query("MCategoryId") MCategoryId: Long, @Query("SCategoryId") SCategoryId: Long, @Query("dealId") dealId: Long,
                          @Query("paymentPrice") paymentPrice: Int, @Query("saveActionType") saveActionType: String, @Query("serviceType") serviceType: String,
                          @Query("sellerId") sellerId: Long): Call<BaseModel<MutableList<Coupon>>>

    /**
     * 사용 가능 포인트, 쿠폰 갯수 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.17
     */
    @GET("/benefits/summary")
    fun getAvailableBenefitCount(@Header("Authorization") accessToken: String): Call<BaseModel<AvailableBenefitCount>>

    /**
     * 적립 예정 포인트 조회 API
     * @author Hyeyeon Park
     * @since 2019.09.18
     */
    @POST("/process/due-save")
    fun getDueSavePoint(@Header("Authorization") accessToken: String, @Body pointProcessParam: PointProcessParam) : Call<BaseModel<ExpectedPointResponse>>

    /**
     * 상품상세-혜택 정보 > 적립 예정 포인트
     * @author Hyeyeon Park
     * @since 2019.10.04
     */
    @GET("/process/total-due-save")
    fun getBenefitDueSavePoint(@Header("Authorization") accessToken: String, @Body pointProcessParam: PointProcessParam) : Call<BaseModel<ExpectedPointResponse>>
}