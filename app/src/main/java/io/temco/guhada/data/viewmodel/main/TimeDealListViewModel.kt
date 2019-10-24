package io.temco.guhada.data.viewmodel.main

import android.content.Context
import io.reactivex.Observable
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.main.DummyImage
import io.temco.guhada.data.model.main.HomeType
import io.temco.guhada.data.model.main.MainBaseModel
import io.temco.guhada.data.model.main.TimeDeal
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
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
class TimeDealListViewModel(val context: Context) : BaseObservableViewModel() {

    val listData: ArrayList<MainBaseModel> = arrayListOf()

    lateinit var adapter: TimeDealListAdapter
    fun getListAdapter() = adapter

    // 더미 타일딜 데이터
    fun getTimeDealItem(listener: OnCallBackListener) {
        if (listData.isNotEmpty()) listData.clear()
        ProductServer.getTimeDeal(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        listData.add(DummyImage(listData.size, HomeType.Dummy, R.color.transparent, 280))
                        val list = it.data as MutableList<Deal>
                        var index = 1
                        Observable.fromIterable(list).map { deal ->
                            val current = Calendar.getInstance()
                            val time = ((index + 1) * 11100L + (index + 1)) * 1000L
                            TimeDeal(index = index++, deal = deal, endTime = deal.timeDealInfo.remainedTimeForEnd, expiredTimeLong = current.timeInMillis + time)
                        }.subscribe { timeDeal ->
                            listData.add(timeDeal)
                        }
//                        var deals =  (o as BaseModel<*>).data as HomeDeal
//                        var timeDeal : ArrayList<TimeDeal> = arrayListOf()
//                        var index = 1
//                        var current = Calendar.getInstance()
//                        /**
//                         * 데이터를 서버에서 받아와 넣는 시점에 expiredTimeLong 값을 계산해서 생성함
//                         */
//                        for(t in deals.allList!!) {
//                            var time = ((index+1) * 11100L + (index+1)) * 1000L // 더미 종료 시간
//                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
//                            index++
//                        }
//                        for(t in deals.womenList!!) {
//                            var time = ((index+1) * 12800L + (index+1))* 1000L // 더미 종료 시간
//                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
//                            index++
//                        }
//                        for(t in deals.menList!!) {
//                            var time = ((index+1) * 13700L + (index+1))* 1000L // 더미 종료 시간
//                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
//                            index++
//                        }
//                        for(t in deals.kidsList!!) {
//                            var time = ((index+1) * 14600L + (index+1))* 1000L // 더미 종료 시간
//                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t, time,current.timeInMillis+time))
//                            index++
//                        }
//                        listData.addAll(timeDeal)
//
                        listData.add(MainBaseModel(list.size, HomeType.Footer, 2))
                        listener.callBackListener(true, "")
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "")
                    },
                    failedTask = {
                        listener.callBackListener(false, "")
                    }
            )
        }, pageIndex = 0, unitPerPage = 5)
    }
}
