package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.body.FilterBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchService {

    // http://dev.search.guhada.com/ps/search/filter?page=1&unitPerPage=20
    @POST("ps/search/filter")
    fun getFilterProductListData(
            @Body filter: FilterBody,
            @Query("page") page: Int,
            @Query("unitPerPage") unit: Int): Call<BaseModel<ProductList>>


    // http://dev.search.guhada.com/ps/search?order=date&page=1&searchQuery=%EC%BD%94%ED%8A%B8&unitPerPage=120
    @GET("ps/search")
    fun getSearchProductListData(@Query("order") order: String,
                                 @Query("page") page: Int,
                                 @Query("searchQuery") searchQuery: String,
                                 @Query("unitPerPage") unit: Int): Call<BaseModel<ProductList>>
}