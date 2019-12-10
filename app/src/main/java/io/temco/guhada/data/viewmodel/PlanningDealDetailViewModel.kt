package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.body.FilterBody
import io.temco.guhada.data.server.SearchServer
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



    fun getDealListData(isInit : Boolean, isSortChange : Boolean){
        if(isInit){
            adapter.items.clear()
            mPageNumber = 1
        }
        mPageNumber++
        var body = FilterBody()
        body.categoryIds.add(1)
        body.searchResultOrder = planningDealSortType.get()!!.code
        SearchServer.getProductListByCategoryFilter(body, mPageNumber, OnServerListener{ success, o ->
            if (adapter != null) {
                if (success) {
                    var index = adapter.itemCount
                    mTotalCount = (o as ProductList).countOfDeals
                    planningDealListTotalCount.set(mTotalCount.toString())
                    if(index == 0){
                        var list = arrayListOf<PlanningDealBase>()
                        var title = PlanningDealTitle(0, PlanningDealType.Title,2,
                                "9월의 마지막 혜택, 구하다 퍼플등급\n회원 15% 할인쿠폰 지급","2019. 05. 01 ~ 2019. 05. 31")
                        list.add(title)
                        var imageBanner = PlanningDealImageBanner(1, PlanningDealType.ImageBanner, 2, imageBannerUrl)
                        list.add(imageBanner)
                        var subTitle = PlanningDealSubTitle(2, PlanningDealType.SubTitle, 2, mTotalCount)
                        list.add(subTitle)
                        for ((i,d) in (o as ProductList).deals.withIndex()){
                            var deal = PlanningDealData(list.size+i, PlanningDealType.Deal,1,d)
                            list.add(deal)
                        }
                        adapter.setItems(list)
                        adapter.notifyDataSetChanged()
                        if(isSortChange) recyclerView.scrollToPosition(2)
                    }else{
                        var list = arrayListOf<PlanningDealBase>()
                        for ((i,d) in (o as ProductList).deals.withIndex()){
                            var deal = PlanningDealData(index+i, PlanningDealType.Deal,1,d)
                            list.add(deal)
                        }
                        adapter.setItems(list)
                        adapter.notifyItemRangeChanged(index, adapter.itemCount)
                    }

                    if(index == 0)
                    else
                    isLoading = false
                }
            }
        })
    }
}

enum class ProductOrderType(val code :String, val label: String){
    NEW_PRODUCT("DATE", "신상품 순"),
    MARKS("SCORE", "평점 순"),
    LOW_PRICE("PRICE_ASC", "낮은가격 순"),
    HIGH_PRICE("PRICE_DESC", "높은가격 순")
}