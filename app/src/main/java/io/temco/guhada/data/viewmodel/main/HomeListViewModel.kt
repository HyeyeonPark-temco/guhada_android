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
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.HomeListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class HomeListViewModel(val context : Context) : BaseObservableViewModel() {
    val listData : ArrayList<MainBaseModel> = arrayListOf()
    private var adapter : HomeListAdapter = HomeListAdapter(this, listData)
    fun getListAdapter() = adapter

    lateinit var premiumData : HomeDeal
    lateinit var bestData : HomeDeal
    lateinit var newInData : HomeDeal

    var mainHomeEventViewIndex = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mainHomeEventViewIndex)
        }

    fun setListData(){
        setHeaderData(listData)

        var premiumSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "PREMIUM ITEM", arrayOf(premiumData?.allList!!.size, premiumData?.womenList!!.size, premiumData?.menList!!.size, premiumData?.kidsList!!.size), 0, premiumData!!,false)
        listData.add(premiumSubTitle)

        var bestSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "BEST ITEM", arrayOf(bestData?.allList!!.size, bestData?.womenList!!.size, bestData?.menList!!.size, bestData?.kidsList!!.size), 0, bestData!!,false)
        listData.add(bestSubTitle)
        adapter.notifyDataSetChanged()
    }

    fun setNewInData(){
        var newInSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "NEW IN", arrayOf(newInData?.allList!!.size, newInData?.womenList!!.size, newInData?.menList!!.size, newInData?.kidsList!!.size), 0, newInData!!,false)
        listData.add(newInSubTitle)
        adapter.notifyItemInserted(listData.size)
        getHotKeyword()
    }


    private fun setHeaderData(list : ArrayList<MainBaseModel>) {
        val ddd = ArrayList<MainBaseModel>()
        val tmpList = java.util.ArrayList<EventData>()
        // 메인 홈 이벤트 화면의 더미 데이터 --------------------------------
        tmpList.add(EventData(0, "", R.drawable.lucky_main_m_360, "main_banner_mobile", "", "", 5, "lucky"))
        tmpList.add(EventData(1, "", R.drawable.timedeal_main_m_360, "main_banner_mobile", "", "", 4, "timedeal"))
        tmpList.add(EventData(2, "", R.drawable.join_main_m_360, "main_banner_mobile", "", "", 2, ""))
        tmpList.add(EventData(3, "", R.drawable.main_m_2per_360, "main_banner_mobile", "", "", 3, ""))
        tmpList.add(EventData(4, "", R.drawable.genuine_main_m_360, "main_banner_mobile", "", "", 1, ""))
        val event = MainEvent(0, HomeType.MainEvent, tmpList)
        ddd.add(event)
        list.add(event)
        //list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.main_banner_mobile, 384))
        // ------------------------------------------------------------------
    }


    /**
     * HOT KEYWORD
     */
    private fun getHotKeyword() {
        ProductServer.getProductByKeyword(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var keys =  (o as BaseModel<*>).list as List<Keyword>
                        var sub = KeywordMain(listData.size, HomeType.Keyword,"HOT KEYWORD", keys)
                        listData.add(sub)
                        listData.add(MainBaseModel(listData.size,HomeType.Footer,2))
                        adapter.notifyItemRangeInserted(listData.size-2, listData.size)
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }

}


/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 server data 연동 Repository
 */
class HomeListRepository(val context : Context){

    /**
     *  PREMIUM ITEM
     */
    /*private fun getPlusItem() {//getProductByPlusItem
        SearchServer.getProductByPlusItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PREMIUM ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival,false)
                        list.value!!.add(subTitle)
                        //if(CustomLog.flag)CustomLog.L("HomeListRepository getNewArrivals","",list.value!!.size)
                        getBestItem()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }*/


    /**
     * Best ITEM
     */
    /*private fun getBestItem() {
        SearchServer.getProductByBestItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "BEST ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival,false)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getNewIn()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }*/

    /**
     * NEW IN
     */
    /*private fun getNewIn() {
        ProductServer.getProductByNewArrivals(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "NEW IN", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival,false)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getHotKeyword()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }*/

    /**
     * HOT KEYWORD
     */
   /* private fun getHotKeyword() {
        ProductServer.getProductByKeyword(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var keys =  (o as BaseModel<*>).list as List<Keyword>
                        var sub = KeywordMain(list.value!!.size, HomeType.Keyword,"HOT KEYWORD", keys)
                        if(CustomLog.flag)CustomLog.L("getHotKeyword keys",keys)
                        if(CustomLog.flag)CustomLog.L("getHotKeyword sub",sub)
                        list.value!!.add(sub)
                        list.value!!.add(MainBaseModel(list.value!!.size,HomeType.Footer,2))
                        list.value = list.value
                        //getBestStore()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }*/

    /**
     * BEST STORE
     */
    /*private fun getBestStore() {
        ProductServer.getProductByNewArrivals(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "BEST STORE", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 0, newArrival,false)
                        list.value!!.add(subTitle)
                        list.value = list.value
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }*/

}