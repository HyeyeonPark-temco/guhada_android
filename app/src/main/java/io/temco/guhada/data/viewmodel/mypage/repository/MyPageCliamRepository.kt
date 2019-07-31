package io.temco.guhada.data.viewmodel.mypage.repository

import android.content.Context
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.claim.MyPageClaim
import io.temco.guhada.data.server.ClaimServer

class MyPageCliamRepository (val context : Context) {
    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<MyPageClaim.Content>>()

    fun getList(): SingleLiveEvent<ArrayList<MyPageClaim.Content>> {
        if (list.value.isNullOrEmpty()) {
            setInitData(null)
        }
        return list
    }


    fun setInitData(listener : OnSwipeRefreshResultListener?) {
        list.value = ArrayList()
        ClaimServer.getMyPageClaimList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data =  (o as BaseModel<*>).data as MyPageClaim
                        if(CustomLog.flag)CustomLog.L("MyPageCliamRepository",data.toString())
                        list!!.value!!.addAll(data.content)
                        list!!.value = list!!.value
                        listener?.run { onResultCallback() }
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        },1)
    }

}