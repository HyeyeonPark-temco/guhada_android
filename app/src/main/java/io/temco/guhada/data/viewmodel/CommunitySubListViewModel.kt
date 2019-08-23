package io.temco.guhada.data.viewmodel

import androidx.lifecycle.MutableLiveData
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.model.community.CommunityCriteria
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.server.SearchServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class CommunitySubListViewModel : BaseObservableViewModel() {
    var mCommunityInfo: CommunityInfo = CommunityInfo()
    var mCommunityResponse: MutableLiveData<CommunityBoard.CommunityResponse> = MutableLiveData()
    var mCategoryFilterList: MutableList<String> = mutableListOf()
    var mSortFilterList: MutableList<String> = mutableListOf(CommunityOrderType.DATE_DESC.label, CommunityOrderType.VIEW_DESC.label, CommunityOrderType.LIKE_DESC.label, CommunityOrderType.COMMENT_DESC.label)
    var mPage = 0
    var mFilterId = -1L
    var mOrder = CommunityOrderType.DATE_DESC.type
    var UNIT_PER_PAGE = 10

    var redirectDetailTask: (item: CommunityBoard) -> Unit = {}

    fun getCommunityList() {
        // init filter id
        if (mFilterId <= 0) {
            val filterList = mCommunityInfo.communityCategorySub.categoryFilterList
            mFilterId = if (filterList.isEmpty()) 0
            else filterList[0].id.toLong()
        }

        CommunityCriteria().apply {
            this.categoryId = mCommunityInfo.communityCategoryId.toLong()
            this.filterId = mFilterId
            this.deleted = false
            this.inUse = true
            this.query = ""
            this.searchType = CommunityCriteria.SearchType.TITLE_CONTENTS.type
        }.let { criteria ->
            SearchServer.getCommunityBoardList(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            if (mPage > 1) {
                                val newList = (it.data as CommunityBoard.CommunityResponse).bbs
                                this.mCommunityResponse.value?.bbs?.addAll(newList)
                                this.mCommunityResponse.postValue(this.mCommunityResponse.value)
                            } else {
                                this.mCommunityResponse.postValue(it.data as CommunityBoard.CommunityResponse)
                            }
                        })
            }, criteria = criteria, order = mOrder, page = ++mPage, unitPerPage = UNIT_PER_PAGE)
        }
    }

    fun onClickMore() = getCommunityList()

    fun onClickItem(item: CommunityBoard) {
        redirectDetailTask(item)
    }
}