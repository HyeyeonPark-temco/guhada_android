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
            list.value = ArrayList()
            setInitData(0,null)
        }
        return list
    }


    fun setInitData(status : Int, listener : OnSwipeRefreshResultListener?) {
        getMoreClaimList(status, listener)
    }

    fun getMoreClaimList(status : Int,listener : OnSwipeRefreshResultListener?, nextPage : Int = 1){
        ClaimServer.getMyPageClaimList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        if(nextPage > 1){
                            var idx = list!!.value!!.size-1
                            if(list!!.value!![idx].inquiry.id == 0L){
                                list!!.value!!.removeAt(idx)
                            }
                        }
                        var data =  (o as BaseModel<*>).data as MyPageClaim
                        if(data.totalPages > 0 && (data.totalPages > data.pageable.pageNumber)){
                            var page = MyPageClaim().Content()
                            page.totalPages = data.totalPages
                            page.pageNumber = data.pageable.pageNumber
                            data.content.add(page)
                        }

                        list!!.value!!.addAll(data.content)
                        list!!.value = list!!.value
                        listener?.run { onResultCallback() }
                    },
                    dataNotFoundTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","dataNotFoundTask")
                    },
                    failedTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","failedTask")
                    },
                    serverRuntimeErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverRuntimeErrorTask")
                    },
                    serverLoginErrorTask = {
                        if(CustomLog.flag)CustomLog.L("getMoreClaimList","serverLoginErrorTask")
                    }
            )
        },nextPage)
    }

    fun deleteClaim(productId : Long, inquiryId : Long, listener : OnServerListener){
        ClaimServer.deleteClaim(listener, productId, inquiryId)
    }
}