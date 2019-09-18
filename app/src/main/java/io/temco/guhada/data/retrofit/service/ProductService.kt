package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.product.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

}
