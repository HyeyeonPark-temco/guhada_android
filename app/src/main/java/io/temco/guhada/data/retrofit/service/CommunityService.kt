package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.CommentContent
import io.temco.guhada.data.model.CommentResponse
import io.temco.guhada.data.model.Comments
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityCategory
import io.temco.guhada.data.model.community.CommunityCategorySub
import io.temco.guhada.data.model.community.CommunityCategoryfilter
import io.temco.guhada.data.model.community.CommunityDetail
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

    // 커뮤니티 전체 조회 API
    @GET("/bbses/{id}")
    fun getBbsDetail(@Path("id") id: Long, @Query("userIp") userIp: String): Call<BaseModel<CommunityDetail>>

    // 댓글 목록 조회 API
    @GET("/comments")
    fun getCommentList(@Query("communityBbsId") communityBbsId : Long, @Query("page") page : Int,
                       @Query("orderType") orderType: String = "DESC", @Query("unitPerPage") unitPerPage : Int=10): Call<BaseModel<CommentContent>>


    // 댓글 ID 조회 API
    @GET("/comments/{id}")
    fun getCommentId(@Path("id") id : Long): Call<BaseModel<Comments>>


    // 커뮤니티 전체 조회 API
    @POST("/comments")
    fun postCommentData(@Header("Authorization") accessToken: String, @Body response: CommentResponse): Call<BaseModel<Any>>

}