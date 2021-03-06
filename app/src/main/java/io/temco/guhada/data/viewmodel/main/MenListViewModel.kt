package io.temco.guhada.data.viewmodel.main

import android.content.Context
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.MenListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class MenListViewModel(val context : Context) : BaseObservableViewModel() {
    var categoryList : MutableList<CategoryEntity>? = null

    val listData : ArrayList<MainBaseModel> = arrayListOf()
    private var adapter : MenListAdapter = MenListAdapter(this, listData)
    fun getListAdapter() = adapter

    val currentSubTitleIndexArray = arrayOf(2,2,2)

    var premiumItemSize = 0
    var bestItemSize = 0
    var newInItemSize = 0

    var premiumData : HomeDeal? = null
    var bestData : HomeDeal? = null
    var newInData : HomeDeal? = null

    fun setListData(){
        premiumItemSize = 0
        bestItemSize = 0
        newInItemSize = 0
        listData.clear()

        addListViewItem(premiumData!!,"PREMIUM ITEM",  Type.SerchFilterCondition.PLUS.name, 0,currentSubTitleIndexArray[0])
        addListViewItem(bestData!!,"BEST ITEM",  Type.SerchFilterCondition.BEST.name, 1,currentSubTitleIndexArray[1])
        /*var premiumSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "PREMIUM ITEM", arrayOf(premiumData?.allList!!.size, premiumData?.womenList!!.size, premiumData?.menList!!.size, premiumData?.kidsList!!.size), currentSubTitleIndexArray[0], premiumData!!,false)
        premiumItemSize = premiumSubTitle.listSize[currentSubTitleIndexArray[0]]
        listData.add(premiumSubTitle)

        var bestSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "BEST ITEM", arrayOf(bestData?.allList!!.size, bestData?.womenList!!.size, bestData?.menList!!.size, bestData?.kidsList!!.size), currentSubTitleIndexArray[1], bestData!!,false)
        bestItemSize = bestSubTitle.listSize[currentSubTitleIndexArray[1]]
        listData.add(bestSubTitle)*/

        adapter.notifyDataSetChanged()
        if(newInData != null) setNewInData()
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
class MenListRepository(val context : Context){
    private val unitPerPage = 10
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
        /*val ddd = ArrayList<MainBaseModel>()
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
        list.value!!.add(event)*/
        //list.value!!.add(DummyImage(list.value!!.size, HomeType.Dummy, R.drawable.main_banner_mobile, 384))
        // ------------------------------------------------------------------
        getPlusItem()
    }


    /**
     * PREMIUM ITEM
     */
    private fun getPlusItem() {//getProductByPlusItem
        SearchServer.getProductByPlusItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "PREMIUM ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 2, newArrival,false)
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
    }

    /**
     * Best ITEM
     */
    private fun getBestItem() {//getProductByPlusItem
        SearchServer.getProductByBestItem(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "BEST ITEM", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 2, newArrival,false)
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
    }


    /**
     * NEW IN
     */
    private fun getNewIn() {
        ProductServer.getProductByNewArrivals(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "NEW IN", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 2, newArrival,false)
                        list.value!!.add(subTitle)
                        list.value = list.value
                        getHotKeyword()
                        //getBestItem()
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


    /**
     * HOT KEYWORD
     */
    private fun getHotKeyword() {
        ProductServer.getProductByKeyword(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var keys =  (o as BaseModel<*>).list as List<Keyword>
                        var sub = KeywordMain(list.value!!.size, HomeType.Keyword,"HOT KEYWORD", keys)
                        if(CustomLog.flag) CustomLog.L("getHotKeyword keys",keys)
                        if(CustomLog.flag) CustomLog.L("getHotKeyword sub",sub)
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
    }


    /**
     * BEST STORE
     */
    private fun getBestStore() {
        ProductServer.getProductByNewArrivals(unitPerPage,OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var newArrival =  (o as BaseModel<*>).data as HomeDeal
                        var subTitle = SubTitleItemList(list.value!!.size, HomeType.SubTitleList,
                                "BEST STORE", arrayOf(newArrival.allList!!.size, newArrival.womenList!!.size, newArrival.menList!!.size, newArrival.kidsList!!.size), 2, newArrival,false)
                        list.value!!.add(subTitle)
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