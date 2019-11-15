package io.temco.guhada.data.viewmodel

import android.content.Context
import com.google.gson.JsonObject
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.event.LuckyDrawList
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * @author park jungho
 * 19.07.18
 *
 * @author Hyeyeon Park
 * @since 2019.10.23
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class LuckyEventDialogViewModel(val context: Context) : BaseObservableViewModel() {


    fun getEventUser(listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.getEventUser(OnServerListener { success, o ->
                if (success && o is BaseModel<*>)
                    if (o.resultCode == ResultCode.SUCCESS.flag) listener.callBackListener(true, o.data as EventUser)
                    else ToastUtil.showMessage(o.message)
                else
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
            }, accessToken = accessToken)
        })
    }



    fun getRequestLuckyDraw(dealId:String, listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ProductServer.getRequestLuckyDraw(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            var data = (o as BaseModel<*>).data as LuckyDrawList
                            if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "data",data)
                            /*if(data.has("statusCode")){
                                if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "statusCode",data.getString("statusCode"))
                            }*/
                            listener.callBackListener(true, data)
                        },
                        dataNotFoundTask = {
                            if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataNotFoundTask ")
                            listener.callBackListener(false, "")
                        },
                        failedTask = {if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw failedTask ")
                            listener.callBackListener(false, "") },
                        userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw userLikeNotFoundTask ")
                            listener.callBackListener(false, "") },
                        serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw serverRuntimeErrorTask ")
                            listener.callBackListener(false, "") },
                        dataIsNull = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataIsNull ")
                            listener.callBackListener(false, "")}
                    )
            },accessToken = accessToken, id = dealId)
        })
    }



    fun getRequestLuckyDrawWinner(dealId:Long, listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ProductServer.getRequestLuckyDrawWinner(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            var data = (o as BaseModel<*>).data as JsonObject
                            if (CustomLog.flag) CustomLog.L("getRequestLuckyDrawWinner", "data",data)
                            listener.callBackListener(true, data)
                        },
                        dataNotFoundTask = {
                            if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataNotFoundTask ")
                            listener.callBackListener(false, "")
                        },
                        failedTask = {if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw failedTask ")
                            listener.callBackListener(false, "") },
                        userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw userLikeNotFoundTask ")
                            listener.callBackListener(false, "") },
                        serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw serverRuntimeErrorTask ")
                            listener.callBackListener(false, "") },
                        dataIsNull = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataIsNull ")
                            listener.callBackListener(false, "")}
                )
            },accessToken = accessToken, dealId = dealId)
        })
    }


}
