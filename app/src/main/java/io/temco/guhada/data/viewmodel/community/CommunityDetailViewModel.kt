package io.temco.guhada.data.viewmodel.community

import android.content.Context
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CommunityDetailViewModel (val context : Context) : BaseObservableViewModel() {

    var bbsId = 0L
    var communityDetail: MutableLiveData<CommunityDetail> = MutableLiveData()
    var info : CommunityInfo = CommunityInfo()


    fun getDetaileData(){
        CommunityServer.getBbsDetail(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as CommunityDetail
                        if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData successTask ",data.toString())
                        communityDetail.postValue(data)
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        }, bbsId,CommonUtil.getUserIp())


    }
}