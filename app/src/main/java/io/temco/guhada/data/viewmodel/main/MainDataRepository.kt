package io.temco.guhada.data.viewmodel.main

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.Keyword
import io.temco.guhada.data.model.main.MainBanner
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.SettleServer
import org.json.JSONArray

class MainDataRepository {
    /**
     *  PREMIUM ITEM
     */
    fun getPremiumItem(unitPerPage : Int, listener : OnCallBackListener) {//getProductByPlusItem
        SearchServer.getProductByPlusItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var list =  (o as BaseModel<*>).data as HomeDeal
                        listener.callBackListener(true, list)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "dataNotFoundTask")
                    },
                    failedTask = {
                        listener.callBackListener(false, it.message)
                    },dataIsNull = {
                listener.callBackListener(false, "dataIsNull")
                    }
            )
        })
    }


    /**
     * Best ITEM
     */
    fun getBestItem(unitPerPage : Int, listener : OnCallBackListener) {
        SearchServer.getProductByBestItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var list =  (o as BaseModel<*>).data as HomeDeal
                        listener.callBackListener(true, list)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "dataNotFoundTask")
                    },
                    failedTask = {
                        listener.callBackListener(false, it.message)
                    },dataIsNull = {
                        listener.callBackListener(false, "dataIsNull")
                    }
            )
        })
    }

    /**
     * NEW IN
     */
    fun getNewIn(unitPerPage : Int, listener : OnCallBackListener) {
        SearchServer.getProductByNewArrivals(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var list =  (o as BaseModel<*>).data as HomeDeal
                        listener.callBackListener(true, list)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "dataNotFoundTask")
                    },
                    failedTask = {
                        listener.callBackListener(false, it.message)
                    },dataIsNull = {
                        listener.callBackListener(false, "dataIsNull")
                    }
            )
        })
    }

    /**
     * HOT KEYWORD
     */
    fun getHotKeyword(unitPerPage : Int, listener : OnCallBackListener) {
        ProductServer.getProductByKeyword(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var keys =  (o as BaseModel<*>).list as List<Keyword>
                        listener.callBackListener(true, keys)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "dataNotFoundTask")
                    },
                    failedTask = {
                        listener.callBackListener(false, it.message)
                    },dataIsNull = {
                        listener.callBackListener(false, "dataIsNull")
                    }
            )
        })
    }


    /**
     *  MainBanner ITEM
     */
    fun getMainBanner(listener : OnCallBackListener) {
        SettleServer.getMainBanner(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        val type = object : TypeToken<List<MainBanner>>() {}.type
                        val list = Gson().fromJson<List<MainBanner>>((o as BaseModel<*>).message, type)
                        listener.callBackListener(true, list)
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "dataNotFoundTask")
                    },
                    failedTask = {
                        listener.callBackListener(false, it.message)
                    },dataIsNull = {
                listener.callBackListener(false, "dataIsNull")
            }
            )
        })
    }


}