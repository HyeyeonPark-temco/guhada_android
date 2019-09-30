package io.temco.guhada.data.retrofit.service

import io.temco.guhada.data.model.AppVersionCheck
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.search.Popular
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SettleService {

    @GET("app/version")
    fun appVersion(): Call<BaseModel<AppVersionCheck>>
}