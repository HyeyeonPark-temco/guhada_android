package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityCategory
import io.temco.guhada.data.model.community.CommunityCategorySub
import io.temco.guhada.data.model.community.CommunityCategoryfilter
import io.temco.guhada.data.model.community.CommunityDetail
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


}