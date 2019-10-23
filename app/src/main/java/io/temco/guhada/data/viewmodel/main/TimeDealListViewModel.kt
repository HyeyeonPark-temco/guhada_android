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
import java.util.*
import kotlin.collections.ArrayList

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

    // 더미 타일딜 데이터
    fun getTimeDealItem(listener: OnCallBackListener) {
        if(listData.isNotEmpty()) listData.clear()
        SearchServer.getProductByBestItem(4, OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        listData.add(DummyImage(listData.size, HomeType.Dummy, R.color.transparent, 280))

                        var deals =  (o as BaseModel<*>).data as HomeDeal
                        var timeDeal : ArrayList<TimeDeal> = arrayListOf()
                        var index = 1
                        var current = Calendar.getInstance()
                        /**
                         * 데이터를 서버에서 받아와 넣는 시점에 expiredTimeLong 값을 계산해서 생성함
                         */
                        for(t in deals.allList!!) {
                            var time = ((index+1) * 11100L + (index+1)) * 1000L // 더미 종료 시간
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
                            index++
                        }
                        for(t in deals.womenList!!) {
                            var time = ((index+1) * 12800L + (index+1))* 1000L // 더미 종료 시간
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
                            index++
                        }
                        for(t in deals.menList!!) {
                            var time = ((index+1) * 13700L + (index+1))* 1000L // 더미 종료 시간
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
                            index++
                        }
                        for(t in deals.kidsList!!) {
                            var time = ((index+1) * 14600L + (index+1))* 1000L // 더미 종료 시간
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
                            index++
                        }
                        listData.addAll(timeDeal)

                        listData.add(MainBaseModel(timeDeal.size,HomeType.Footer,2))
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
