package io.temco.guhada.data.viewmodel.community

import android.content.Context
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.*
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*


class CommunityMainViewPagerViewModel (val context : Context) : BaseObservableViewModel() {

    var communityInfoList : SingleLiveEvent<ArrayList<CommunityInfo>> = SingleLiveEvent()

    var communityInfoMap : SortedMap<Int, CommunityCategory> = sortedMapOf()
    var communityCategoryMap : SortedMap<Int, SortedMap<Int,CommunityCategorySub>> = sortedMapOf()
    var totalCatergoyCount = 0


    /**
     * @author park jungho
     *
     * 커뮤니티 탭 정보 가져오기
     */
    fun getCommunityInfo(){
        totalCatergoyCount = 0
        CommunityServer.getCommunityAll(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategory>
                        for (info in data){
                            communityInfoMap.put(info.id, info)
                            getCommunityCategory(info.id)
                        }
                        if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData successTask ",data.toString())
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        })
    }

    fun getCommunityCategory(categoryId : Int){
        CommunityServer.getCommunityCategoryAll(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategorySub>
                        for (info in data){
                            if(!communityCategoryMap.containsKey(categoryId) || communityCategoryMap.get(categoryId).isNullOrEmpty()){
                                var map : SortedMap<Int,CommunityCategorySub> = sortedMapOf()
                                map.put(info.id, info)
                                communityCategoryMap.put(categoryId,map)
                            }else{
                                communityCategoryMap.get(categoryId)?.put(info.id, info)
                            }
                            getCommunityCategoryFilter(categoryId, info.id)
                        }
                        totalCatergoyCount += data.size
                        if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommunityCategory successTask ",communityCategoryMap.toString())
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        },categoryId)
    }


    fun getCommunityCategoryFilter(communityId : Int, categoryId : Int){
        CommunityServer.getCategoryFilter(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategoryfilter>
                        communityCategoryMap.get(communityId)?.get(categoryId)?.categoryFilterList?.addAll(data)
                        totalCatergoyCount -= 1
                        if(totalCatergoyCount == 0){
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "setDetailView getCommunityCategoryFilter 0 ",communityCategoryMap.toString())
                            var list : ArrayList<CommunityInfo> = arrayListOf()
                            list.add(CommunityInfo(CommunityType.MAIN, context.resources.getString(R.string.community_titles_main)))
                            list.add(CommunityInfo(CommunityType.POPULAR, context.resources.getString(R.string.community_titles_popular)))
                            list.add(CommunityInfo(CommunityType.NOTIFICATION, context.resources.getString(R.string.community_titles_noti)))
                            var communityKeys : Iterator<Int> = communityCategoryMap.keys.iterator()
                            while (communityKeys.hasNext()){
                                var key = communityKeys.next()
                                var communityCategoryKeys : Iterator<Int> = communityCategoryMap[key]!!.keys.iterator()
                                while (communityCategoryKeys.hasNext()){
                                    var cId = communityCategoryKeys.next()
                                    var info = communityCategoryMap[key]!![cId]
                                    var tab = CommunityInfo()
                                    tab.communityId = info!!.communityId
                                    tab.communityCategoryId = info!!.id
                                    tab.communityCategory = communityInfoMap[info!!.communityId]!!
                                    tab.communityCategorySub = info
                                    tab.communityName = communityInfoMap[info!!.communityId]!!.name
                                    tab.communityCategoryName = info!!.name
                                    list.add(tab)
                                }
                            }
                            /*for (map in communityCategoryMap.values.so){
                                for(info in map.values){
                                    var tab = CommunityInfo()
                                    tab.communityId = info.communityId
                                    tab.communityCategoryId = info.id
                                    tab.communityCategory = communityInfoMap[info.communityId]!!
                                    tab.communityCategorySub = info
                                    tab.communityName = communityInfoMap[info.communityId]!!.name
                                    tab.communityCategoryName = info.name
                                    list.add(tab)
                                }
                            }*/
                            communityInfoList.value = list
                        }else{
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommunityCategoryFilter totalCatergoyCount ",totalCatergoyCount)
                        }
                    },
                    dataNotFoundTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataNotFoundTask ") },
                    failedTask = {if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData failedTask ") },
                    userLikeNotFoundTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData userLikeNotFoundTask ") },
                    serverRuntimeErrorTask = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData serverRuntimeErrorTask ") },
                    dataIsNull = { if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getDetaileData dataIsNull ") }
            )
        },categoryId)
    }

}