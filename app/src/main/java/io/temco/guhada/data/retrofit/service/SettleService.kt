package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.AppVersionCheck
import io.temco.guhada.data.model.CardInterest
import io.temco.guhada.data.model.MainPopup
import io.temco.guhada.data.model.PlanningDetailData
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventListData
import io.temco.guhada.data.model.main.MainBanner
import io.temco.guhada.data.model.main.PlanningListData
import io.temco.guhada.data.model.search.Popular
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SettleService {

    @GET("app/version")
    fun appVersion(): Call<BaseModel<AppVersionCheck>>

    /**
     * 무이자 할부 카드 리스트 조회 API
     * @author Hyeyeon Park
     * @since 2019..11.06
     */
    @GET("payment/cardInterest")
    fun getCardInterest() : Call<BaseModel<MutableList<CardInterest>>>

    /**
     * http://settle.guhada.com/event/list?eventProgress=ALL  (전부)
    http://settle.guhada.com/event/list?eventProgress=PROGRESS (진행중)
    http://settle.guhada.com/event/list?eventProgress=END (종료)
     */

    @GET("event/list")
    fun getEventList(@Query("eventProgress") eventProgress : String): Call<BaseModel<EventListData>>



    @GET("selectMainBanner")
    fun getMainBanner(): Call<BaseModel<MainBanner>>


    @GET("plan/list")
    fun getPlanningList(@Query("eventProgress") eventProgress : String): Call<BaseModel<PlanningListData>>


    @GET("plan/list/detail")
    fun getPlanningDetail(@Query("eventId") eventId : Int, @Query("page") startIndex : Int, @Query("unitPerPage") unitPerPage : Int = 20): Call<BaseModel<PlanningDetailData>>

    @GET("event/main/popup")
    fun getMainPopup(): Call<BaseModel<MainPopup>>


}