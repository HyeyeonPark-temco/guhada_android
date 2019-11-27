package io.temco.guhada.data.server

import com.google.gson.Gson
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.AppVersionCheck
import io.temco.guhada.data.model.CardInterest
import io.temco.guhada.data.model.base.BaseErrorModel
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.base.Message
import io.temco.guhada.data.model.event.EventListData
import io.temco.guhada.data.model.main.MainBanner
import io.temco.guhada.data.retrofit.manager.RetrofitManager
import io.temco.guhada.data.retrofit.service.SettleService
import io.temco.guhada.data.viewmodel.main.EventProgressType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * BASE URL: dev.user.guhada.com/
 */
class SettleServer {
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
         * 중복된 이메일인지 검증
         */
        @JvmStatic
        fun checkAppVersion(listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.SETTLE, SettleService::class.java, true).appVersion().enqueue(object : Callback<BaseModel<AppVersionCheck>> {
                override fun onResponse(call: Call<BaseModel<AppVersionCheck>>, response: Response<BaseModel<AppVersionCheck>>) {
                    resultListener(listener, call, response)
                }

                override fun onFailure(call: Call<BaseModel<AppVersionCheck>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 무이자 할부 카드 리스트 조회
         * @author Hyeyeon Park
         * @since 2019..11.06
         */
        @JvmStatic
        fun getCardInterst(listener: OnServerListener) =
                RetrofitManager.createService(Type.Server.SETTLE, SettleService::class.java, false, false).getCardInterest()
                        .enqueue(ServerCallbackUtil.ServerResponseCallback<BaseModel<MutableList<CardInterest>>> { listener.onResult(it.isSuccessful, it.body()) })



        /**
         * 이벤트 리스트
         */
        @JvmStatic
        fun getEventList(eventProgress : EventProgressType, listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.SETTLE, SettleService::class.java, true).getEventList(eventProgress.code).enqueue(object : Callback<BaseModel<EventListData>> {
                override fun onResponse(call: Call<BaseModel<EventListData>>, response: Response<BaseModel<EventListData>>) {
                    resultListener(listener, call, response)
                }
                override fun onFailure(call: Call<BaseModel<EventListData>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

        /**
         * 메인 베너
         */
        @JvmStatic
        fun getMainBanner(listener: OnServerListener) {
            RetrofitManager.createService(Type.Server.SETTLE, SettleService::class.java, true).getMainBanner().enqueue(object : Callback<BaseModel<MainBanner>> {
                override fun onResponse(call: Call<BaseModel<MainBanner>>, response: Response<BaseModel<MainBanner>>) {
                    resultListener(listener, call, response)
                }
                override fun onFailure(call: Call<BaseModel<MainBanner>>, t: Throwable) {
                    listener.onResult(false, t.message)
                }
            })
        }

    }
}