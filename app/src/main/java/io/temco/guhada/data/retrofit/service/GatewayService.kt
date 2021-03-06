package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.coupon.CouponInfo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * @author park jungho
 *
 * Gateway API
 * http://dev.gateway.guhada.com/swagger-ui.html#/
 */
interface GatewayService {

    // http://dev.gateway.guhada.com/my-page/bookmark-products?offset=0&page=1&size=20
    @GET("/my-page/bookmark-products")
    fun getBookMarkProduct(@Header("Authorization") accessToken: String, @Query("page") page: Int,@Query("size") size: Int = 20): Call<BaseModel<BookMarkProduct>>

    @Multipart
    @POST("/upload/image")
    fun uploadImage(@Part("cloudResourceList") cloudResourceList: RequestBody, @Part file : MultipartBody.Part): Call<BaseModel<ImageResponse>>


    @Multipart
    @POST("/upload/image/path")
    fun uploadImagePath(@Part("uploadPath") cloudResourceList: RequestBody, @Part file : MultipartBody.Part): Call<BaseModel<ImageResponse>>


    @Multipart
    @POST("/upload/image/path")
    fun uploadImagePathQuery(@Query("uploadPath") uploadPath: String, @Part file : MultipartBody.Part): Call<BaseModel<ImageResponse>>


    @Multipart
    @POST("/upload/image/path/{uploadPath}")
    fun uploadImagePath2(@Path("uploadPath") uploadPath: String, @Part file : MultipartBody.Part): Call<BaseModel<ImageResponse>>

    /**
     * 주문서 쿠폰 정보
     * @author Hyeyeon Park
     */
    @GET("/benefits/order/coupon")
    fun getCouponInfo(@Header("Authorization") accessToken: String, @Query("cartItemIdSet") cartItemIdSet : IntArray) : Call<BaseModel<CouponInfo>>

}