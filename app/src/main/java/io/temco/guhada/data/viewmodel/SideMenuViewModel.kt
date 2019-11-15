package io.temco.guhada.data.viewmodel

import android.content.Context
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventListData
import io.temco.guhada.data.server.SettleServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class SideMenuViewModel(val context : Context) : BaseObservableViewModel() {

    fun getEventList(listener : OnCallBackListener){
        SettleServer.getEventList("",OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if (CustomLog.flag) CustomLog.L("SideMenuRepository", "getEventList ", "init ----- o",o)
                        var data = (o as BaseModel<*>).list as List<EventListData>
                        if (CustomLog.flag) CustomLog.L("SideMenuRepository", "getEventList ", "init ----- data",data)
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }



}

