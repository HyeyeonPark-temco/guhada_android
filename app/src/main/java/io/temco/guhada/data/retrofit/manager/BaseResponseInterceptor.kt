package io.temco.guhada.data.retrofit.manager

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.temco.guhada.common.util.CustomLog
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
/**
 * @author park jungho
 * 19.07.18
 *
 * 서버에서 내려받은 Response body 를 분해해
 * - jsonObject 는 data
 * - JsonArray  는 list
 * - Request Code 200..400 이외에는 error 로
 *  BaseModel 에 맞춰서 Json Data를 재생성한다
 *
 */
class BaseResponseInterceptor() : Interceptor {
    val JSON = MediaType.parse("application/json; charset=utf-8")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val body : ResponseBody  = response.body()!!

        val parser = JsonParser()
        val bodyString = body.string()
        body.close()
        //if(CustomLog.flag)CustomLog.L("BaseResponseInterceptor",bodyString)
        val json = parser.parse(bodyString)

        var model = JsonObject()
        json?.let {
            val jsonObject: JsonObject = it.asJsonObject
            model.addProperty("result", if (jsonObject.has("result")) jsonObject.get("result").asString else "")
            model.addProperty("message", if (jsonObject.has("message")) jsonObject.get("message").asString else "")
            model.addProperty("resultCode", if (jsonObject.has("resultCode")) jsonObject.get("resultCode").asInt else -1)

            if (model.get("resultCode").asInt in 200..400) {
                if (jsonObject.has("data")) {
                    if(jsonObject.get("data").isJsonArray){
                        model.add("data",JsonObject())
                        model.add("list", if(jsonObject.has("data")) jsonObject.get("data").asJsonArray else JsonArray())
                    }else if(jsonObject.get("data").isJsonObject){
                        model.add("data", if (jsonObject.has("data")) jsonObject.get("data").asJsonObject else JsonObject())
                        model.add("list", JsonArray())
                    }else {
                        var newData = JsonObject()
                        newData.add("data",jsonObject.get("data"))
                        model.add("data",newData)
                        model.add("list", JsonArray())
                    }
                }
            } else {
                if (jsonObject.has("data")) {
                    model.add("data",JsonObject())
                    model.add("list", JsonArray())
                    if(jsonObject.get("data").isJsonObject){
                        model.addProperty("error", jsonObject.getAsJsonObject("data").toString())
                    }else{
                        if(!jsonObject.get("data").isJsonNull){
                            model.addProperty("error",jsonObject.get("data").asString)
                        }else{
                            model.addProperty("error","")
                        }
                    }
                }
            }
        }
        val newResponse = response.newBuilder().body(ResponseBody.create(JSON, model.toString()))
        return newResponse.build()
    }
}