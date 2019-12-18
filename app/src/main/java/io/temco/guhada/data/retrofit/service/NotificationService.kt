package io.temco.guhada.data.retrofit.service

import com.google.gson.JsonObject
import io.temco.guhada.data.model.base.BaseModel
import retrofit2.Call
import retrofit2.http.*

interface NotificationService {

    @HTTP(method="DELETE", hasBody=true, path="device")
    fun deleteDevice(@Header("Authorization") accessToken: String, @Body body: JsonObject): Call<BaseModel<JsonObject>>

    @DELETE("device/{token}")
    fun deleteDevice(@Header("Authorization") accessToken: String, @Path("token") token: String): Call<BaseModel<JsonObject>>


    @POST("device")
    fun saveDevice(@Header("Authorization") accessToken: String, @Body body: JsonObject): Call<BaseModel<JsonObject>>


    @POST("device")
    fun saveDevice(@Body body: JsonObject): Call<BaseModel<JsonObject>>
}