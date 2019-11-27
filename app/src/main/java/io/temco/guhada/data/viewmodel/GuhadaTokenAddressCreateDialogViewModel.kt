package io.temco.guhada.data.viewmodel

import android.content.Context
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.BlockChainTokenServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class GuhadaTokenAddressCreateDialogViewModel (val context : Context) : BaseObservableViewModel() {



    fun getTokenList(listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            BlockChainTokenServer.getTokenList(OnServerListener { success, o ->
                if(CustomLog.flag) CustomLog.L("GuhadaTokenAddressCreateDialog","getTokenList",o.toString())
                if (success && o is BaseModel<*>) {
                    listener.callBackListener(true,o)
                }else{
                    listener.callBackListener(false,BaseApplication.getInstance().getString(R.string.common_message_servererror))
                }
            }, accessToken = accessToken)
        },invalidTokenTask = {
            listener.callBackListener(false, "invalidTokenTask")
        })
    }
}