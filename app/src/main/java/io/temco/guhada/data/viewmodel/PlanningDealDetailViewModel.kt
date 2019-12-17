package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.model.main.*
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.server.SettleServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.adapter.PlanningDealDetailListAdapter
import io.temco.guhada.view.custom.recycler.CustomRecyclerView

class PlanningDealDetailViewModel(val context : Context) : BaseObservableViewModel(){
    lateinit var recyclerView :CustomRecyclerView

    var mSortFilterLabel = arrayListOf<String>(ProductOrderType.NEW_PRODUCT.label, ProductOrderType.MARKS.label, ProductOrderType.LOW_PRICE.label, ProductOrderType.HIGH_PRICE.label)
    var mSortFilterType = arrayListOf<ProductOrderType>(ProductOrderType.NEW_PRODUCT, ProductOrderType.MARKS, ProductOrderType.LOW_PRICE, ProductOrderType.HIGH_PRICE)
    var mFilterIndex = 0
    var listData = arrayListOf<PlanningDealBase>()
    var adapter = PlanningDealDetailListAdapter(this, listData)
    var isLoading = true
    var imageBannerUrl = ""

    var planningDealDetailId = 0
    var currentPage = 0
    var totalPage = -1

    lateinit var planningDataInfo : PlanningDetailData
    var mPageNumber = 0
    var mTotalCount = 0
    var planningDealListTotalCount = ObservableField<String>("0")
    @Bindable
    get() = field
    set(value) {
        field = value
        notifyPropertyChanged(BR.planningDealListTotalCount)
    }
    var planningDealSortType = ObservableField<ProductOrderType>(ProductOrderType.NEW_PRODUCT)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.planningDealSortType)
        }


    /**
     *  Event List
     */
    fun getPlanningDetail(init : Boolean, listener : OnCallBackListener?) {
        if(init){
            listData.clear()
            currentPage = 0
        }
        currentPage++
        SettleServer.getPlanningDetail(planningDealDetailId, currentPage, OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var list = arrayListOf<PlanningDealBase>()

                        if(!::planningDataInfo.isInitialized){
                            planningDataInfo =  (o as BaseModel<*>).data as PlanningDetailData
                            if(CustomLog.flag)CustomLog.L("getPlanningDetail","planningDataInfo",planningDataInfo)
                            if(CustomLog.flag)CustomLog.L("getPlanningDetail","planningDataInfo planListDetails",planningDataInfo.planListDetails)
                            var index = adapter.itemCount
                            mTotalCount = planningDataInfo.totalItemCount
                            planningDealListTotalCount.set(mTotalCount.toString())

                            var title = PlanningDealTitle(0, PlanningDealType.Title,2, planningDataInfo,
                                    planningDataInfo.detailTitle, Type.getWebUrl()+"event/luckydraw",
                                    (planningDataInfo.startDate.split(" ")[0] + " ~ " +planningDataInfo.endDate.split(" ")[0]))
                            list.add(title)
                            var imageBanner = PlanningDealImageBanner(1, PlanningDealType.ImageBanner, 2, planningDataInfo.mobileImageUrl?.toString() ?: "")
                            list.add(imageBanner)
                            var subTitle = PlanningDealSubTitle(2, PlanningDealType.SubTitle, 2, mTotalCount)
                            list.add(subTitle)
                            if(totalPage == -1){
                                totalPage = mTotalCount/20 + (if(mTotalCount%20 > 0) 1 else 0)
                            }
                            listener?.callBackListener(true, planningDataInfo.title)
                            if(CustomLog.flag)CustomLog.L("getPlanningDetail","totalPage",totalPage)
                        }
                        //getDealListData(true, false)
                        var index = adapter.itemCount

                        var detailList = ((o as BaseModel<*>).data as PlanningDetailData).planListDetails

                        if(index == 0){
                            for ((i,d) in detailList.withIndex()){
                                var deal = PlanningDealData(list.size+i, PlanningDealType.Deal,1,d)
                                list.add(deal)
                            }
                            adapter.setItems(list)
                            adapter.notifyDataSetChanged()
                            /*if(isSortChange) recyclerView.scrollToPosition(2)*/
                        }else{
                            for ((i,d) in detailList.withIndex()){
                                var deal = PlanningDealData(index+i, PlanningDealType.Deal,1,d)
                                list.add(deal)
                            }
                            adapter.setItems(list)
                            adapter.notifyItemRangeChanged(index, adapter.itemCount)
                        }
                        isLoading = false
                    },
                    dataNotFoundTask = {

                    },
                    failedTask = {

                    }
            )
        })
    }


}

enum class ProductOrderType(val code :String, val label: String){
    NEW_PRODUCT("DATE", "신상품 순"),
    MARKS("SCORE", "평점 순"),
    LOW_PRICE("PRICE_ASC", "낮은가격 순"),
    HIGH_PRICE("PRICE_DESC", "높은가격 순")
}