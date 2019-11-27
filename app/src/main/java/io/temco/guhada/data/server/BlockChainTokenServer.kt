package io.temco.guhada.data.server


import android.widget.Toast
import com.google.gson.Gson
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.blockchain.TokenList
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.BlockChainTokenService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockChainTokenServer {
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



        /**
         * 마이페이지 상품문의 리스트 조회
         */
        @JvmStatic
        fun getTokenList(listener: OnServerListener, accessToken : String) {
            RetrofitManager.createService(Type.Server.BLOCKCHAIN_TOKEN, BlockChainTokenService::class.java, true)
                    .getTokenList(accessToken = accessToken).enqueue(object : Callback<BaseModel<TokenList>> {
                        override fun onResponse(call: Call<BaseModel<TokenList>>, response: Response<BaseModel<TokenList>>) {
                            resultListener(listener, call, response)
                        }
                        override fun onFailure(call: Call<BaseModel<TokenList>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    })
        }


        /**
         * 마이페이지 상품문의 리스트 조회
         */
        @JvmStatic
        fun getTokenAddress(listener: OnServerListener, accessToken : String, tokenName: String) {
            RetrofitManager.createService(Type.Server.BLOCKCHAIN_TOKEN, BlockChainTokenService::class.java, true)
                    .getTokenAddress(accessToken = accessToken, tokenName = tokenName).enqueue(object : Callback<BaseModel<JSONObject>> {
                        override fun onResponse(call: Call<BaseModel<JSONObject>>, response: Response<BaseModel<JSONObject>>) {
                            resultListener(listener, call, response)
                        }
                        override fun onFailure(call: Call<BaseModel<JSONObject>>, t: Throwable) {
                            listener.onResult(false, t.message)
                        }
                    })
        }



    }


}
