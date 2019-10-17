package io.temco.guhada.data.viewmodel.mypage.repository

import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.BookMarkProduct
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.mypage.MyPageBookMarkViewModel

class MyPageProductBookMarkRepository (val model : MyPageBookMarkViewModel) {

    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<Deal>>()

    fun getList(): SingleLiveEvent<ArrayList<Deal>> {
        if (list.value.isNullOrEmpty()) {
            list.value = ArrayList()
            setInitData(null)
        }
        return list
    }


    fun setInitData(listener: OnSwipeRefreshResultListener?) {
        getMyBookMarkProductList(1,listener)
    }


    fun getMyBookMarkProductList(page : Int, listener: OnSwipeRefreshResultListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    if (it != null){
                        GatewayServer.getBookMarkProduct(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        if(!list.value.isNullOrEmpty() && list.value!!.get(list.value!!.size-1).dealId < 0){
                                            list.value!!.removeAt(list.value!!.size-1)
                                        }
                                        var startRange = model.getListAdapter().items.size
                                        var data = (o as BaseModel<*>).data as BookMarkProduct
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData ", "init ----- list",data)
                                        model.totalElement.set(data.totalElements.toString())
                                        list.value!!.addAll(data.deals)
                                        if(data.totalPage > page){
                                            var moreDeal = Deal()
                                            moreDeal.dealId = -999
                                            list.value!!.add(moreDeal)
                                        }
                                        list!!.value = list!!.value
                                        if(startRange == 0){
                                            model.getListAdapter().notifyDataSetChanged()
                                        }else{
                                            model.getListAdapter().notifyItemRangeChanged(startRange, model.getListAdapter().items.size)
                                        }
                                        listener?.onResultCallback()

                                    },
                                    dataNotFoundTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData dataNotFoundTask ")
                                        listener?.onResultCallback()
                                    },
                                    failedTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData failedTask ",o.toString())
                                        listener?.onResultCallback()
                                    },
                                    userLikeNotFoundTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData userLikeNotFoundTask ")
                                        listener?.onResultCallback()
                                    },
                                    serverRuntimeErrorTask = {
                                        if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData serverRuntimeErrorTask ")
                                        listener?.onResultCallback()
                                    },
                                    dataIsNull = {listener?.onResultCallback()}
                            )
                        }, accessToken = it, page = page)
                    }
                }, invalidTokenTask = {  if (CustomLog.flag) CustomLog.L("MyPageProductBookMarkRepository", "setInitData invalidTokenTask ") })
    }



    fun deleteBookMark(target : String, targetId: Long, callBackListener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMark(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("deleteBookMark","successTask")
                                    callBackListener?.callBackListener(true,0)
                                },
                                dataNotFoundTask = { callBackListener?.callBackListener(false,"dataNotFoundTask") },
                                failedTask = { callBackListener?.callBackListener(false,"failedTask") },
                                userLikeNotFoundTask = { callBackListener?.callBackListener(false,"userLikeNotFoundTask") },
                                serverRuntimeErrorTask = { callBackListener?.callBackListener(false,"serverRuntimeErrorTask") }
                        )
                    }, accessToken = it, target = target,targetId = targetId)
                }, invalidTokenTask = { })
    }


    fun deleteBookMarkAll(target : String, callBackListener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = {
                    UserServer.deleteBookMarkAll(OnServerListener { success, o ->
                        ServerCallbackUtil.executeByResultCode(success, o,
                                successTask = {
                                    if(CustomLog.flag)CustomLog.L("deleteBookMark","successTask",o.toString())
                                    callBackListener?.callBackListener(true,0)
                                },
                                dataNotFoundTask = { callBackListener?.callBackListener(false,"dataNotFoundTask") },
                                failedTask = {callBackListener?.callBackListener(false,"failedTask") },
                                userLikeNotFoundTask = {callBackListener?.callBackListener(false,"userLikeNotFoundTask") },
                                serverRuntimeErrorTask = { callBackListener?.callBackListener(false,"serverRuntimeErrorTask")}
                        )
                    }, accessToken = it, target = target)
                }, invalidTokenTask = { })
    }

}