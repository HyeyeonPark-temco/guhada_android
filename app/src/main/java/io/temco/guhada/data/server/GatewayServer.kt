package io.temco.guhada.data.server

import com.google.gson.Gson
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.GatewayService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GatewayServer {

    companion object {

        /**
         * 북마크 상품 목록
         *
         * target : Type.BookMark field
         * userId : 유져 id
         */
        @JvmStatic
        fun getBookMarkProduct(listener: OnServerListener, accessToken : String, page: Int) {
            RetrofitManager.createService(Type.Server.GATEWAY, GatewayService::class.java, true)
                    .getBookMarkProduct(accessToken, page, 4).enqueue(object : Callback<BaseModel<BookMarkProduct>> {
                        override fun onResponse(call: Call<BaseModel<BookMarkProduct>>, response: Response<BaseModel<BookMarkProduct>>) {
                            if(response.code() in 200..400 && response.body() != null){
                                listener.onResult(true, response.body())
                            }else{
                                try{
                                    var msg  = Message()
                                    var errorBody : String? = response.errorBody()?.string() ?: null
                                    if(!errorBody.isNullOrEmpty()){
                                        var gson = Gson()
                                        msg = gson.fromJson<Message>(errorBody, Message::class.java)
                                    }
                                    var error = BaseErrorModel(response.code(),response.raw().request().url().toString(),msg)
                                    if(CustomLog.flag)CustomLog.L("getBookMarkProduct","onResponse body",error.toString())
                                    listener.onResult(false, error)
                                }catch (e : Exception){
                                    if(CustomLog.flag)CustomLog.E(e)
                                    listener.onResult(false, null)
                                }
                            }
                        }
                        override fun onFailure(call: Call<BaseModel<BookMarkProduct>>, t: Throwable) {
                            if(CustomLog.flag)CustomLog.L("getBookMarkProduct","onFailure",t.message.toString())
                            listener.onResult(false, t.message)
                        }
                    }
            )
        }
    }


}