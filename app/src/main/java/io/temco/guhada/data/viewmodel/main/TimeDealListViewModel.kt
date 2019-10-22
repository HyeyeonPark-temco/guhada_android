package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.lifecycle.LiveData
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.TimeDealListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class TimeDealListViewModel(val context : Context) : BaseObservableViewModel() {

    val listData : ArrayList<MainBaseModel> = arrayListOf()

    lateinit var adapter : TimeDealListAdapter
    fun getListAdapter() = adapter

    fun getTimeDealItem(listener: OnCallBackListener) {
        if(listData.isNotEmpty()) listData.clear()
        SearchServer.getProductByBestItem(10, OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        listData.add(DummyImage(listData.size, HomeType.Dummy, R.color.transparent, 320))
                        var deals =  (o as BaseModel<*>).data as HomeDeal
                        var timeDeal : ArrayList<TimeDeal> = arrayListOf()
                        var index = 1
                        for(t in deals.allList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, ((index+1) * 314000L + (index+1))))
                            index++
                        }
                        for(t in deals.womenList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, ((index+1) * 31000L + (index+1))))
                            index++
                        }
                        for(t in deals.menList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, ((index+1) * 441000L + (index+1))))
                            index++
                        }
                        for(t in deals.kidsList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, ((index+1) * 2341000L + (index+1))))
                            index++
                        }
                        listData.addAll(timeDeal)
                        listener.callBackListener(true,"")
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false,"")
                    },
                    failedTask = {
                        listener.callBackListener(false,"")
                    }
            )
        })
    }
}
