package io.temco.guhada.data.viewmodel.main

import android.content.Context
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.db.entity.CategoryEntity
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.ProductServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.main.KidsListAdapter

/**
 * @author park jungho
 * 19.07.18
 *
 * 메인 홈 리스트 CustomView ViewModel
 */
class KidsListViewModel(val context : Context) : BaseObservableViewModel() {
    var categoryList : MutableList<CategoryEntity>? = null

    val listData : ArrayList<MainBaseModel> = arrayListOf()
    private var adapter : KidsListAdapter = KidsListAdapter(this, listData)
    fun getListAdapter() = adapter

    val currentSubTitleIndexArray = arrayOf(3,3,3)

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
