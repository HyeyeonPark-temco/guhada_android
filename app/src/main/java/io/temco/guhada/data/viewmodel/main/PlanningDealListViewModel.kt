package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.event.EventListData
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.SettleServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.EventListAdapter
import io.temco.guhada.view.adapter.main.HomeListAdapter
import io.temco.guhada.view.adapter.main.PlanningDealListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class PlanningDealListViewModel(val context : Context) : BaseObservableViewModel() {
    var mSortFilterType: MutableList<PlanningProgressType> = mutableListOf(PlanningProgressType.ALL,PlanningProgressType.PROGRESS,PlanningProgressType.END)
    var mSortFilterLabel: MutableList<String> = mutableListOf(PlanningProgressType.ALL.label, PlanningProgressType.PROGRESS.label,PlanningProgressType.END.label)
    var mFilterIndex = 0

    val listData: ArrayList<MainBaseModel> = arrayListOf()
    lateinit var adapter: PlanningDealListAdapter
    fun getListAdapter() = adapter

    /**
     *  Event List
     */
    fun getEventList() {
        listData.clear()
        SettleServer.getPlanningList(mSortFilterType[mFilterIndex], OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var dataList =  (o as BaseModel<*>).list as List<PlanningListData>
                        var tmpList = arrayListOf<MainBaseModel>()

                        var header = PlanningHeader(0, HomeType.PlanningHeader, PlanningHeaderData(dataList.size, arrayListOf(), arrayListOf()))
                        tmpList.add(header)
                        for ((i,e) in dataList.withIndex()){
                            var data = PlanningList(i+1, HomeType.PlanningList, e)
                            tmpList.add(data)
                        }
                        if (CustomLog.flag) CustomLog.L("PlanningDealListViewModel LIFECYCLE", "onViewCreated listData.size 1----------------",tmpList.size)
                        listData.addAll(tmpList)
                        adapter.notifyDataSetChanged()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

}


enum class PlanningProgressType(val code : String, var label:String) {
    ALL("ALL", "전체 이벤트"),
    PROGRESS("PROGRESS", "진행중 이벤트"),
    END("END", "종료 이벤트")
}