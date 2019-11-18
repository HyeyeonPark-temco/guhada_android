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
                    else {
                        listener.callBackListener(false, o.message)
                        ToastUtil.showMessage(o.message)
                    }
                else{
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                    listener.callBackListener(false, "")
                }
            }, accessToken = accessToken)
        })
    }



    fun getRequestLuckyDraw(dealId:String, listener: OnCallBackListener) {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            ProductServer.getRequestLuckyDraw(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if(success){
                                var data = (o as BaseModel<*>).data as LuckyDrawList
                                if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "data",data)
                                listener.callBackListener(true, data)
                            }else{
                                if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "data o ",(o as BaseModel<*>).message.toString())
                                if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "data o ",(o as BaseModel<*>).errorModel.Message.message.toString())
                                listener.callBackListener(false, (o as BaseModel<*>).message)
                            }
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
                        dataIsNull = {o->
                            if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataIsNull ",o)
                            listener.callBackListener(false, (o as BaseModel<*>).message)}
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
                            listener.callBackListener(false, "데이터 전송중 오류가 발생되었습니다 [LED_DNF]")
                        },
                        failedTask = {if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw failedTask ")
                            listener.callBackListener(false, "데이터 전송중 오류가 발생되었습니다 [LED_FT]") },
                        userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw userLikeNotFoundTask ")
                            listener.callBackListener(false, "데이터 전송중 오류가 발생되었습니다 [LED_ULF]") },
                        serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw serverRuntimeErrorTask ")
                            listener.callBackListener(false, "데이터 전송중 오류가 발생되었습니다 [LED_SRE]") },
                        dataIsNull = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataIsNull ")
                            listener.callBackListener(false, "데이터 전송중 오류가 발생되었습니다 [LED_DN]")}
                )
            },accessToken = accessToken, dealId = dealId)
        })
    }


}
