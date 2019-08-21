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
    var mPage = 1
    var UNIT_PER_PAGE = 20

    fun getCommunityList() {
        val filterList = mCommunityInfo.communityCategorySub.categoryFilterList
        val filterId =
                if (filterList.isEmpty()) 0
                else filterList[0].id.toLong()

        CommunityCriteria().apply {
            this.categoryId = mCommunityInfo.communityCategoryId.toLong()
            this.filterId = filterId
            this.deleted = false
            this.inUse = true
            this.query = ""
            this.searchType = CommunityCriteria.SearchType.TITLE_CONTENTS.type
        }.let { criteria ->
            SearchServer.getCommunityBoardList(OnServerListener { success, o ->
                ServerCallbackUtil.executeByResultCode(success, o,
                        successTask = {
                            this.mCommunityResponse.postValue(it.data as CommunityBoard.CommunityResponse)
                        })
            }, criteria = criteria, order = CommunityOrderType.DATE_DESC.type, page = mPage, unitPerPage = UNIT_PER_PAGE)
        }

    }
}