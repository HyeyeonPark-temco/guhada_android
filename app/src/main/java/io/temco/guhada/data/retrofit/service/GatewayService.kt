package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * @author park jungho
 *
 * Gateway API
 * http://dev.gateway.guhada.com/swagger-ui.html#/
 */
interface GatewayService {

    // http://dev.gateway.guhada.com/my-page/bookmark-products?offset=0&page=1&size=20
    @GET("/my-page/bookmark-products")
    fun getBookMarkProduct(@Header("Authorization") accessToken: String, /*@Query("offset") offset: Int = 0,*/  @Query("page") page: Int,@Query("size") size: Int = 20): Call<BaseModel<BookMarkProduct>>


}