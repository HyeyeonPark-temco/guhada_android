package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.*
import retrofit2.Call
import retrofit2.http.*

/**
 * @author park jungho
 *
 * Gateway API
 * http://dev.gateway.guhada.com/swagger-ui.html#/
 */
interface CommunityService {

    // 커뮤니티 전체 조회 API
    @GET("/community/all")
    fun getCommunityAll(): Call<BaseModel<CommunityCategory>>

    // 커뮤니티 카테고리 전체 조회 API
    @GET("/category/all")
    fun getCommunityCategoryAll(@Query("communityId") communityId: Int): Call<BaseModel<CommunityCategorySub>>

    // community-category-filter-controller
    @GET("/category/filter")
    fun getCategoryFilter(@Query("communityCategoryId") communityCategoryId: Int): Call<BaseModel<CommunityCategoryfilter>>

    // 게시글 조회
    @GET("/bbses/{id}")
    fun getBbsDetail(@Path("id") id: Long, @Query("userIp") userIp: String): Call<BaseModel<CommunityDetail>>

    // 댓글 목록 조회 API
    @GET("/comments")
    fun getCommentList(@Query("communityBbsId") communityBbsId : Long, @Query("page") page : Int,
                       @Query("orderType") orderType: String = "DESC", @Query("unitPerPage") unitPerPage : Int=10): Call<BaseModel<CommentContent>>


    // 댓글 ID 조회 API
    @GET("/comments/{id}")
    fun getCommentId(@Path("id") id : Long): Call<BaseModel<Comments>>

    // 댓글 수정
    @PUT("/comments/{id}")
    fun modifyCommentId(@Header("Authorization") accessToken: String, @Path("id") id : Long, @Body response: CommentResponse): Call<BaseModel<Any>>

    // 댓글 삭제
    @DELETE("/comments/{id}")
    fun deleteCommentId(@Header("Authorization") accessToken: String, @Path("id") id : Long): Call<BaseModel<Any>>

    // 댓글 전송
    @POST("/comments")
    fun postCommentData(@Header("Authorization") accessToken: String, @Body response: CommentResponse): Call<BaseModel<Any>>


    // 게시글 작성
    @POST("/bbses")
    fun postBbsData(@Header("Authorization") accessToken: String, @Body response: CreateBbsResponse): Call<BaseModel<Any>>


    // 게시글 수정
    @PUT("/bbses/{id}")
    fun modifyBbsData(@Header("Authorization") accessToken: String, @Path("id") id : Long, @Body response: CreateBbsResponse): Call<BaseModel<Any>>


    // 게시글 삭제
    @DELETE("/bbses/{id}")
    fun deleteBbsData(@Header("Authorization") accessToken: String,  @Path("id") id : Long): Call<BaseModel<Any>>


    // 게시글 임시 작성
    @POST("/bbses/temp")
    fun postBbsTempData(@Header("Authorization") accessToken: String, @Body response: CreateBbsTempResponse): Call<BaseModel<Any>>


    // 게시글 임시 수정
    @PUT("/bbses/temp/{id}")
    fun modifyBbsTempData(@Header("Authorization") accessToken: String, @Path("id") id : Long, @Body response: CreateBbsResponse): Call<BaseModel<Any>>


    // 게시글 임시 삭제
    @DELETE("/bbses/temp/{id}")
    fun deleteBbsTempData(@Header("Authorization") accessToken: String,  @Path("id") id : Long): Call<BaseModel<Any>>


    // 게시글 임시 삭제
    @GET("/bbses/temp/all")
    fun getBbsTempListData(@Header("Authorization") accessToken: String): Call<BaseModel<CommunityTempInfo>>

}