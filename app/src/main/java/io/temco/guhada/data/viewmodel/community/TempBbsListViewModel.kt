package io.temco.guhada.data.viewmodel.community

import android.content.Context
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityTempInfo
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class TempBbsListViewModel (val context : Context)  : BaseObservableViewModel() {


    fun getBbsTempListData(listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.getBbsTempListData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<CommunityTempInfo>).list
                                        if(CustomLog.flag) CustomLog.L("getBbsTempListData value",it)
                                        listener.callBackListener(true, value)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }

    fun deleteTempData(id : Long, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.deleteBbsTempData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<*>).data
                                        if(CustomLog.flag) CustomLog.L("getBbsTempListData value",value)
                                        listener.callBackListener(true, value)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, id)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }

}