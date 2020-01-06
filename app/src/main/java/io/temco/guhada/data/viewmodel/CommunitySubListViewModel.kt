package io.temco.guhada.data.viewmodel

import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityCriteria
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CommunitySubListViewModel : BaseObservableViewModel() {
    private val UNIT_PER_PAGE = 20
    var mCommunityInfo: CommunityInfo = CommunityInfo()
    var mCommunityResponse: MutableLiveData<CommunityBoard.CommunityResponse> = MutableLiveData()
    var mCategoryFilterList: MutableList<String> = mutableListOf()
    var mSortFilterList: MutableList<String> = mutableListOf(CommunityOrderType.DATE_DESC.label, CommunityOrderType.VIEW_DESC.label/*, CommunityOrderType.LIKE_DESC.label*/, CommunityOrderType.COMMENT_DESC.label)
    var mPage = 0
    var mFilterId = -1
    var mOrder = CommunityOrderType.DATE_DESC.type
    var mEmptyViewVisible = ObservableBoolean(false)
        @Bindable
        get() = field

    var mRedirectDetailTask: (item: CommunityBoard) -> Unit = {}

    fun getCommunityList() {
        // init filter id
        if (mFilterId <= 0) {
            val filterList = mCommunityInfo.communityCategorySub.categoryFilterList
            mFilterId = if (filterList.isEmpty()) 0 else filterList[0].id
        }

        JsonObject().let { criteria ->
            if (mCommunityInfo.communityCategoryId.toLong() > 0) {
                criteria.addProperty("searchType", CommunityCriteria.SearchType.TITLE_CONTENTS.type)
                criteria.addProperty("categoryId", mCommunityInfo.communityCategoryId.toLong())
                criteria.addProperty("filterId", mFilterId)
                criteria.addProperty("query", "")
            } else
                criteria.addProperty("searchType", CommunityCriteria.SearchType.CONTENTS.type) // 전체글 조회

            SearchServer.getCommunityBoardList(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = { this.mCommunityResponse.postValue(it.data as CommunityBoard.CommunityResponse) })
            }, criteria = criteria, order = mOrder, page = ++mPage, unitPerPage = UNIT_PER_PAGE)
        }
    }

    fun onClickItem(item: CommunityBoard) = mRedirectDetailTask(item)
}