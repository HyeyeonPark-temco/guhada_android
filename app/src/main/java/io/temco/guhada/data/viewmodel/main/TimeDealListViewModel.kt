package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.lifecycle.LiveData
import io.temco.guhada.R
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
    private var repository: TimeDealListRepository = TimeDealListRepository(context)


    private val _listData : SingleLiveEvent<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = TimeDealListAdapter(this,listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getListAdapter() = adapter

}


/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 server data 연동 Repository
 */
class TimeDealListRepository(val context : Context){
    private val unitPerPage = 30
    // 메인 홈 list data
    private var list = SingleLiveEvent<ArrayList<MainBaseModel>>()

    fun getList() : SingleLiveEvent<ArrayList<MainBaseModel>>{
        if (list.value.isNullOrEmpty()){
            setInitData()
        }
        return list
    }


    private fun setInitData() {
        list.value = ArrayList()
        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.main_banner_mobile, 384))
        // ------------------------------------------------------------------
        getBestItem()
    }


    /**
     * Best ITEM
     */
    private fun getBestItem() {
        SearchServer.getProductByBestItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var deals =  (o as BaseModel<*>).data as HomeDeal
                        var timeDeal : ArrayList<TimeDeal> = arrayListOf()
                        var index = 1
                        for(t in deals.allList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t))
                            index++
                        }
                        for(t in deals.womenList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t))
                            index++
                        }
                        for(t in deals.menList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t))
                            index++
                        }
                        for(t in deals.kidsList!!) {
                            timeDeal.add(TimeDeal(index,HomeType.TimeDeal, t))
                            index++
                        }
                        list.value!!.addAll(timeDeal)
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