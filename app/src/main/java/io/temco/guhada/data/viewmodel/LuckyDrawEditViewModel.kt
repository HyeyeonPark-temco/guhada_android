package io.temco.guhada.data.viewmodel

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventUser
import io.temco.guhada.data.server.UserServer

/**
 * 럭키드로우 회원 정보 수정 ViewModel
 * @author Hyeyeon Park
 * @author 2019.11.14
 */
class LuckyDrawEditViewModel : LuckyDrawJoinViewModel() {
    var mEventUser: MutableLiveData<EventUser> = MutableLiveData()

    fun getEventUser() {
        ServerCallbackUtil.callWithToken(task = { accessToken ->
            UserServer.getEventUser(OnServerListener { success, o ->
                if (success && o is BaseModel<*>)
                    if (o.resultCode == ResultCode.SUCCESS.flag) mEventUser.postValue(o.data as EventUser)
                    else ToastUtil.showMessage(o.message)
                else
                    ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
            }, accessToken = accessToken)
        })
    }

    fun updateEventUser() {
        if (mEventUser.value != null) {
            mEventUser.value?.email = mEmail
            ServerCallbackUtil.callWithToken(task = { accessToken ->
                UserServer.updateEventUser(OnServerListener { success, o ->
                    if (success && o is BaseModel<*>)
                        if (o.resultCode == ResultCode.SUCCESS.flag) {
                            // [TODO] 럭키드로우 응모
                            ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.luckydraw_message_edit))

                        } else ToastUtil.showMessage(o.message)
                    else
                        ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.common_message_servererror))
                }, accessToken = accessToken, eventUser = mEventUser.value!!)
            })
        }
    }
}