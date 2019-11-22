package io.temco.guhada.data.viewmodel.main

import android.content.Context
import androidx.lifecycle.LiveData
import io.temco.guhada.R
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

    var premiumData : HomeDeal? = null
    var bestData : HomeDeal? = null
    var newInData : HomeDeal? = null

    fun setListData(){
        listData.clear()
        var premiumSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "PREMIUM ITEM", arrayOf(premiumData?.allList!!.size, premiumData?.womenList!!.size, premiumData?.menList!!.size, premiumData?.kidsList!!.size), 3, premiumData!!,false)
        listData.add(premiumSubTitle)

        var bestSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "BEST ITEM", arrayOf(bestData?.allList!!.size, bestData?.womenList!!.size, bestData?.menList!!.size, bestData?.kidsList!!.size), 3, bestData!!,false)
        listData.add(bestSubTitle)
        adapter.notifyDataSetChanged()
        if(newInData != null) setNewInData()
    }

    fun setNewInData(){
        var newInSubTitle = SubTitleItemList(listData.size, HomeType.SubTitleList,
                "NEW IN", arrayOf(newInData?.allList!!.size, newInData?.womenList!!.size, newInData?.menList!!.size, newInData?.kidsList!!.size), 3, newInData!!,false)
        listData.add(newInSubTitle)
        adapter.notifyItemInserted(listData.size)
        getHotKeyword()
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
