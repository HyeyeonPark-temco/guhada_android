package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.AppVersionCheck
import io.temco.guhada.data.model.CardInterest
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventListData
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


    @GET("event/list")
    fun getEventList(@Query("eventProgress") eventProgress : String): Call<BaseModel<EventListData>>

}