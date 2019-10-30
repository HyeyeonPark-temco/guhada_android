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

    fun getTimeDealItem(listener: OnCallBackListener) {
        if (listData.isNotEmpty()) listData.clear()
        ProductServer.getTimeDeal(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        listData.add(DummyImage(listData.size, HomeType.Dummy, R.color.transparent, 320))
                        val list = it.data as MutableList<Deal>
                        var index = 1
                        Observable.fromIterable(list).map { deal ->
                            val current = Calendar.getInstance().timeInMillis
                            val time = (deal.timeDealInfo.remainedTimeForEnd) * 1000L
                            TimeDeal(index = index++, deal = deal, endTime = deal.timeDealInfo.remainedTimeForEnd, expiredTimeLong = current + time)
                        }.subscribe { timeDeal ->
                            listData.add(timeDeal)
                        }

                        //listData.add(MainBaseModel(list.size, HomeType.Footer, 2))
                        listener.callBackListener(true, "")
                    },
                    dataNotFoundTask = {
                        listener.callBackListener(false, "")
                    },
                    failedTask = {
                        listener.callBackListener(false, "")
                    }
            )
        })
    }
}
