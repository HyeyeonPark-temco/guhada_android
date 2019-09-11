package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.lifecycle.LiveData
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.WomenListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class WomenListViewModel(val context : Context) : BaseObservableViewModel() {
    private var repository: WomenListRepository = WomenListRepository(context)
    var categoryList : MutableList<CategoryEntity>? = null

    private val _listData : SingleLiveEvent<ArrayList<MainBaseModel>> = repository.getList()
    private val adapter = WomenListAdapter(this,listData.value!!)

    val listData :LiveData<ArrayList<MainBaseModel>> get() = _listData

    fun getListAdapter() = adapter

}


/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 server data 연동 Repository
 */
class WomenListRepository(val context : Context){
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
        val ddd = ArrayList<MainBaseModel>()
        val tmpList = java.util.ArrayList<EventData>()
        // 메인 홈 이벤트 화면의 더미 데이터 --------------------------------
        tmpList.add(EventData(0, "https://d3ikprf0m31yc7.cloudfront.net/images/products/thumb/a5e85e5d916e4e1e9d78d0a5e75a7411",
                R.drawable.main_01, "main_01", "", "", 0, ""))
        tmpList.add(EventData(1, "https://d3ikprf0m31yc7.cloudfront.net/images/products/thumb/Aviator_Polarized_Lens_58mm_Sunglasses_RB3025-001.png",
                R.drawable.main_02, "hotkeyword05", "", "", 1, ""))
        tmpList.add(EventData(2, "https://d3ikprf0m31yc7.cloudfront.net/images/products/thumb/19c8c40763734103a56bc1e93c26689a",
                R.drawable.main_03, "focuson04", "", "", 2, ""))
        tmpList.add(EventData(3, "https://d3ikprf0m31yc7.cloudfront.net/images/products/thumb/013c833b13744fadbdebaacc38d968cd",
                R.drawable.main_04, "foryou_08", "", "", 3, ""))
        tmpList.add(EventData(4, "https://d3ikprf0m31yc7.cloudfront.net/images/products/thumb/46b0d03ccb274329b822bba10d138adc",
                R.drawable.main_05, "banner09", "", "", 4, ""))
        val event = MainEvent(0, HomeType.MainEvent, tmpList)
        ddd.add(event)
        list.value!!.add(event)
        // ------------------------------------------------------------------
        getNewArrivals()
    }

    /**
     * new-arrivals    신상품 목록 조회
     */
    private fun getNewArrivals() {
        ProductServer.getProductByNewArrivals(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "NEW ARRIVALS", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival)
                        list.value!!.add(subTitle)
                        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.focuson04, 556))
                        //if(CustomLog.flag)CustomLog.L("HomeListRepository getNewArrivals","",list.value!!.size)
                        getNewArrivalsDummy()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


    /**
     * plus-item    플러스 아이템 목록 조회
     */
    private fun getPlusItem() {
        ProductServer.getProductByPlusItem(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PLUS ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 3, newArrival)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getNewArrivalsDummy1()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

    /**
     * new-arrivals    신상품 목록 조회 더미
     */
    private fun getNewArrivalsDummy() {
        ProductServer.getProductByNewArrivals(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PLUS ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 3, newArrival)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getNewArrivalsDummy1()
                        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.hotkeyword05, 341))
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

    /**
     * new-arrivals    신상품 목록 조회 더미 community/all
     */
    private fun getNewArrivalsDummy1() {
        ProductServer.getProductByNewArrivals(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PLUS ITEM TEST1", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 1, newArrival)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getNewArrivalsDummy2()
                        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.banner_15, 120))
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

    /**
     * new-arrivals    신상품 목록 조회 더미
     */
    private fun getNewArrivalsDummy2() {
        ProductServer.getProductByNewArrivals(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PLUS ITEM  TEST2", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 2, newArrival)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getNewArrivalsDummy3()
                        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.banner09, 230))
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

    /**
     * new-arrivals    신상품 목록 조회 더미
     */
    private fun getNewArrivalsDummy3() {
        ProductServer.getProductByNewArrivals(6,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PLUS ITEM TEST3", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.banner_13, 120))
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }





}