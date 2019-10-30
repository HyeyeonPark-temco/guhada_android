package io.temco.guhada.data.retrofit.service

import com.google.gson.JsonObject
import io.temco.guhada.data.model.ProductList
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityCriteria
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.search.AutoComplete
import io.temco.guhada.data.model.search.Popular
import io.temco.guhada.data.model.seller.Criteria
import retrofit2.Call
import retrofit2.http.*

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
    fun getCommunityList(@Body criteria: CommunityCriteria, @Query("order") order: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<CommunityBoard.CommunityResponse>>

    /**
     * 커뮤니티 전체글 검색 API
     * @author Hyeyeon Park
     * @since 2019.09.25
     */
    @POST("/ps/bbs/search")
    fun getCommunityList(@Body criteria: JsonObject, @Query("order") order: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int): Call<BaseModel<CommunityBoard.CommunityResponse>>

    /**
     * 셀러별 상품 목록 조회 API
     * @see io.temco.guhada.common.enum.ProductOrderType
     *
     * @author Hyeyeon Park
     * @since 2019.09.02
     */
    @GET("/ps/search/seller/{id}")
    fun getSellerProductList(@Path("id") sellerId: Long, @Query("order") order: String, @Query("page") page: Int, @Query("unitPerPage") unitPerPage: Int) : Call<BaseModel<ProductList>>


    /**
     * @author park jungho
     * 19.07.18
     * Best 목록 조회
     * @param unitPerPage 내려받을 페이지 수
     */
    @GET("/ps/hits/list")
    fun getProductByBestItem(@Query("unitPerPage") unitPerPage: Int): Call<BaseModel<HomeDeal>>


    /**
     * @author park jungho
     * 19.07.18
     * 플러스 아이템 목록 조회
     * @param unitPerPage 내려받을 페이지 수
     */
    @GET("/ps/main-home/deals/plus-item")
    fun getProductByPlusItem(@Query("unitPerPage") unitPerPage: Int): Call<BaseModel<HomeDeal>>


}