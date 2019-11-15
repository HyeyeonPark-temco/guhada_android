package io.temco.guhada.data.viewmodel

import android.content.Context
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.model.event.LuckyEvent
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.LuckyDrawAdapter
import io.temco.guhada.view.adapter.main.TimeDealListAdapter
import java.util.*

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
                            var data = (o as BaseModel<*>).data
                            if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "data",data)

                        },
                        dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataNotFoundTask ") },
                        failedTask = {if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw failedTask ") },
                        userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw userLikeNotFoundTask ") },
                        serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw serverRuntimeErrorTask ") },
                        dataIsNull = { if (CustomLog.flag) CustomLog.L("LuckyEventDialogViewModel", "getRequestLuckyDraw dataIsNull ") }
                    )
            },accessToken = accessToken, id = dealId)
        })
    }



}
