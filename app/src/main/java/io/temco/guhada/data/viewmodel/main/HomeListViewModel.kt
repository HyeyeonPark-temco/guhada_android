package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
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

    lateinit var mainBanner: List<MainBanner>

    var currentSubTitleIndexArray = arrayOf(0,0,0)

    var premiumItemSize = 0
    var bestItemSize = 0
    var newInItemSize = 0

    var mainHomeEventViewIndex = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.mainHomeEventViewIndex)
        }

    fun setListData(){
        setHeaderData(listData)
        premiumItemSize = 0
        bestItemSize = 0
        newInItemSize = 0
        currentSubTitleIndexArray = arrayOf(0,0,0)

        addListViewItem(premiumData!!,"PREMIUM ITEM",  Type.SerchFilterCondition.PLUS.name,0, currentSubTitleIndexArray[0])
        addListViewItem(bestData!!,"BEST ITEM",  Type.SerchFilterCondition.BEST.name, 1,currentSubTitleIndexArray[1])

        adapter.notifyDataSetChanged()
    }

    fun setNewInData(){
        addListViewItem(newInData!!,"NEW IN",  Type.SerchFilterCondition.NEW.name, 2,currentSubTitleIndexArray[2])
        /*var newInSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "NEW IN", arrayOf(newInData?.allList!!.size, newInData?.womenList!!.size, newInData?.menList!!.size, newInData?.kidsList!!.size), currentSubTitleIndexArray[2], newInData!!,false)
        newInItemSize = newInSubTitle.listSize[currentSubTitleIndexArray[2]]
        listData.add(newInSubTitle)*/

        adapter.notifyItemInserted(listData.size)
        getHotKeyword()
    }


    private fun addListViewItem(homeDeal : HomeDeal, title : String, typeNm : String, subTitleIndex : Int, currentSubTitleIndex : Int){
        var subTitleLayout = SubTitleLayout(listData.size, HomeType.SubTitleLayout, title,
                arrayOf(homeDeal.allList!!.size, homeDeal.womenList!!.size, homeDeal.menList!!.size, homeDeal.kidsList!!.size),
                true, subTitleIndex, currentSubTitleIndex)
        listData.add(subTitleLayout)
        var dealList = when(currentSubTitleIndex){
            1-> homeDeal.womenList!!
            2-> homeDeal.menList!!
            3-> homeDeal.kidsList!!
            else->homeDeal.allList!!
        }
        for (deal in dealList){
            var dealItem = DealItem(listData.size, HomeType.DealItemOne, deal)
            listData.add(dealItem)
        }
        var bestViewMore = ViewMoreLayout(listData.size, HomeType.ViewMoreLayout, currentSubTitleIndex, typeNm)
        listData.add(bestViewMore)
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