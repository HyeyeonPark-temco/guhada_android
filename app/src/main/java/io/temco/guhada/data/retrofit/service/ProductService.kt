package io.temco.guhada.data.retrofit.service

import com.google.gson.JsonObject
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.data.model.event.LuckyEvent
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.Keyword
import io.temco.guhada.data.model.product.Product
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ProductService {

    // http://dev.product.guhada.com/categories
    @get:GET("categories")
    val categories: Call<BaseModel<Category>>

    // http://dev.product.guhada.com/brands/simple
    @get:GET("brands/simple")
    val allBrands: Call<BaseModel<Brand>>

    @GET("/deals/{id}")
    fun getProductDetail(@Path("id") id: Long?): Call<BaseModel<Product>>

    // http://dev.product.guhada.com/products/{productId}?viewType=LIST
    @GET("products/{productId}?viewType=LIST")
    fun getProductByList(@Path("productId") id: String): Call<BaseModel<ProductByList>>


    /**
     * @author park jungho
     * 19.07.18
     * 신상품 목록 조회
     * @param unitPerPage 내려받을 페이지 수
     * main-home/deals/new-arrivals - 이전
     */
    @GET("main-home/deals/new-arrivals")
    fun getProductByNewArrivals(@Query("unitPerPage") unitPerPage: Int): Call<BaseModel<HomeDeal>>

    /**
     * @author park jungho
     * 19.07.18
     * 플러스 아이템 목록 조회
     * @param unitPerPage 내려받을 페이지 수
     */
    @GET("main-home/deals/plus-item")
    fun getProductByPlusItem(@Query("unitPerPage") unitPerPage: Int): Call<BaseModel<HomeDeal>>

    /**
     * 상품 목록 조회 API
     * @since 2019.09.18
     * @author Hyeyeon Park
     */
    @GET("deals")
    fun getProductListBySellerId(@Query("sellerId") sellerId: Long, @Query("pageIndex") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<MutableList<Deal>>>

    /**
     * 상품 목록 조회 API
     * @since 2019.08.14
     * @author Hyeyeon Park
     */
    @GET("deals")
    fun getProductListByOnlyPage(@Query("unitPerPage") unitPerPage: Int): Call<BaseModel<MutableList<Deal>>>

    /**
     * 택배사 조회 API
     * @since 2019.08.19
     * @author Hyeyeon Park
     */
    @GET("/common/ship-companies")
    fun getShippingCompanies(@Query("type") type: String): Call<BaseModel<MutableList<ShippingCompany>>>

    /**
     * @author park jungho
     * 19.09.19
     * 핫키워드 목록 조회
     */
    @GET("main-home/hot-keyword")
    fun getProductByKeyword(): Call<BaseModel<Keyword>>

    /**
     * 타임딜 리스트 조회
     * @author Hyeyeon Park
     * @since 2019.10.23
     */
    @GET("time-deals")
    fun getTimeDeal() : Call<BaseModel<MutableList<Deal>>>


    /**
     * @author park jungho
     * 19.11.13
     * 럭키드로우 리스트
     */
    @GET("lucky-draws")
    fun getLuckyDraws(): Call<BaseModel<LuckyEvent>>

    /**
     * @author park jungho
     * 19.11.13
     * 럭키드로우 리스트
     */
    @GET("lucky-draws")
    fun getLuckyDraws(@Header("Authorization") accessToken: String): Call<BaseModel<LuckyEvent>>


    @POST("lucky-draws/request/{dealId}")
    fun getRequestLuckyDraw(@Header("Authorization") accessToken: String, @Path("dealId") id: String): Call<BaseModel<LuckyDrawList>>


    /**
     * @author park jungho
     * 럭키드로우 당첨 정보 확인
     */
    @GET("/lucky-draws/winner/{dealId}")
    fun getRequestLuckyDrawWinner(@Header("Authorization") accessToken: String, @Path("dealId") dealId : Long): Call<BaseModel<JsonObject>>




    /**
     * @author park jungho
     * 19.12.23
     * 리스펀스가 null 일 경우 확인
     */
    @GET("test/ok-null")
    fun getOkNull(): Call<BaseModel<Any>>

}
