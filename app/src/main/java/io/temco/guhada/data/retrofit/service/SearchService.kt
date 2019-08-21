package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityCriteria
import io.temco.guhada.data.model.search.AutoComplete
import io.temco.guhada.data.model.search.Popular
import io.temco.guhada.data.model.seller.Criteria
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


    @GET("ps/keyword/popular")
    fun getSearchPopularKeyword(@Query("top") top: Int = 10): Call<BaseModel<Popular>>


    @GET("ps/search/autoComplete")
    fun getSearchAutoComplete(@Query("searchQuery") searchQuery: String): Call<BaseModel<AutoComplete>>

    /**
     * 셀러 연관상품 목록 조회 API
     */
    @POST("/ps/search/seller/related")
    fun getSellerRelatedProductList(@Body criteria: Criteria, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<ProductList>>

    /**
     * 셀러 추천상품(인기상품) 목록 조회 API
     */
    @POST("/ps/search/seller/popular")
    fun getSellerPopularProductList(@Body criteria: Criteria, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<ProductList>>

    /**
     * 커뮤니티 게시판 검색 API
     * @param page default 1
     * @param order default DATE_DESC
     * @param order 페이지 정렬 [default: DATE_DESC]
     * @see io.temco.guhada.common.enum.CommunityOrderType
     *
     * @author Hyeyeon Park
     * @since 2019.08.21
     */
    @POST("/ps/bbs/search")
    fun getCommunityList(@Body criteria: CommunityCriteria, @Query("order") order: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int) : Call<BaseModel<CommunityBoard.CommunityResponse>>
}