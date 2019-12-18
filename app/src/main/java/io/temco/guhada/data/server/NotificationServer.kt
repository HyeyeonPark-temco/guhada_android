package io.temco.guhada.data.server

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationServer {

    companion object {

        @JvmStatic
        fun <C, R> resultListener(listener: OnServerListener, call: Call<C>, response: Response<R>) {
            if (response.code() in 200..400 && response.body() != null) {
                listener.onResult(true, response.body())
            } else {
                try {
                    var msg = Message()
                    var errorBody: String? = response.errorBody()?.string() ?: null
                    if (!errorBody.isNullOrEmpty()) {
                        var gson = Gson()
                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                    }
                    var error = BaseErrorModel(response.code(), response.raw().request().url().toString(), msg)
                    if (CustomLog.flag) CustomLog.L("saveReport", "onResponse body", error.toString())
                    listener.onResult(false, error)
                } catch (e: Exception) {
                    if (CustomLog.flag) CustomLog.E(e)
                    listener.onResult(false, null)
                }
            }
        }

        @JvmStatic
        fun deleteDevice(accessToken: String, token: String, listener: OnServerListener) {
            var body = JsonObject()
            body.addProperty("token",token)
            RetrofitManager.createService(Type.Server.NOTIFICATION, NotificationService::class.java, true).deleteDevice(("Bearer "+accessToken), body).enqueue(object : Callback<BaseModel<JsonObject>> {
                override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                    resultListener(listener, call, response)
                }
                override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }



        @JvmStatic
        fun saveDeviceToken(listener: OnServerListener, accessToken: String?, token : String){
            var body = JsonObject()
            body.addProperty("token",token)
            if(TextUtils.isEmpty(accessToken)){
                saveDevice(body, listener)
            }else{
                saveDevice(accessToken!!, body, listener)
            }
        }


        @JvmStatic
        fun saveDevice(accessToken: String, body: JsonObject, listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.NOTIFICATION, NotificationService::class.java, true).saveDevice(("Bearer "+accessToken), body).enqueue(object : Callback<BaseModel<JsonObject>> {
                override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                    resultListener(listener, call, response)
                }
                override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }


        @JvmStatic
        fun saveDevice(body: JsonObject, listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.NOTIFICATION, NotificationService::class.java, true).saveDevice(body).enqueue(object : Callback<BaseModel<JsonObject>> {
                override fun onResponse(call: Call<BaseModel<JsonObject>>, response: Response<BaseModel<JsonObject>>) {
                    resultListener(listener, call, response)
                }
                override fun onFailure(call: Call<BaseModel<JsonObject>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }


    }


}