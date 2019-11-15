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

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class EventListViewModel(val context : Context) : BaseObservableViewModel() {
    private var repository: EventListRepository = EventListRepository(this,context)

    var eventProgress : String = ""
    private val _listData : SingleLiveEvent<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = EventListAdapter(this,listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getListAdapter() = adapter

}


/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 server data 연동 Repository
 */
class EventListRepository(val model : EventListViewModel, val context : Context){
    private var list = SingleLiveEvent<ArrayList<MainBaseModel>>()

    fun getList() : SingleLiveEvent<ArrayList<MainBaseModel>>{
        if (list.value.isNullOrEmpty()){
            setInitData()
        }
        return list
    }


    private fun setInitData() {
        list.value = ArrayList()
        getEventList(model.eventProgress)
    }

    /**
     *  PREMIUM ITEM
     */
    private fun getEventList(eventProgress : String) {//getProductByPlusItem
        SettleServer.getEventList(eventProgress, OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var eventList =  (o as BaseModel<*>).list as List<EventListData>
                        var tmpList = arrayListOf<MainBaseModel>()

                        var header = EventHeader(0, HomeType.EventHeader, EventHeaderData(eventList.size, arrayListOf(), arrayListOf()))
                        tmpList.add(header)
                        for ((i,e) in eventList.withIndex()){
                            var data = EventList(i+1, HomeType.EventList, e)
                            tmpList.add(data)
                        }
                        if (CustomLog.flag) CustomLog.L("EventListLayout LIFECYCLE", "onViewCreated listData.size 1----------------",tmpList.size)
                        list.value!!.addAll(tmpList)
                        list.value = list.value
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }



}