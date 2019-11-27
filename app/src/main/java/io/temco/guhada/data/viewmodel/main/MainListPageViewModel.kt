package io.temco.guhada.data.viewmodel.main

import android.content.Context
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.SubTitleItemList
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 Fragment ViewModel
 */
class MainListPageViewModel(val context : Context) : BaseObservableViewModel() {

    /**
     *  PREMIUM ITEM
     */
    fun getPlusItem() {//getProductByPlusItem
        if(CustomLog.flag)CustomLog.L("MainListPageViewModel","getPlusItem start-----")
        SearchServer.getProductByBestItem(100,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var list =  (o as BaseModel<*>).data as HomeDeal
                        if(CustomLog.flag)CustomLog.L("MainListPageViewModel","getPlusItem kidsList size",list.kidsList?.size?:"")
                        if(CustomLog.flag)CustomLog.L("MainListPageViewModel","getPlusItem menList size",list.menList?.size?:"")
                        if(CustomLog.flag)CustomLog.L("MainListPageViewModel","getPlusItem womenList size",list.womenList?.size?:"")
                        if(CustomLog.flag)CustomLog.L("MainListPageViewModel","getPlusItem allList size",list.allList?.size?:"")
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


}