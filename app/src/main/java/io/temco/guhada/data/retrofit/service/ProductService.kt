package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.Brand
import io.temco.guhada.data.model.Category
import io.temco.guhada.data.model.Product
import io.temco.guhada.data.model.ProductByList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
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
    fun getProductByNewArrivals(@Query("unitPerPage") unitPerPage : Int): Call<BaseModel<HomeDeal>>

    /**
     * @author park jungho
     * 19.07.18
     * 플러스 아이템 목록 조회
     * @param unitPerPage 내려받을 페이지 수
     */
    @GET("main-home/deals/plus-item")
    fun getProductByPlusItem(@Query("unitPerPage") unitPerPage : Int): Call<BaseModel<HomeDeal>>

}
