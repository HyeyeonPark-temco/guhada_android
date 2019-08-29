package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.enum.ImageUploadTarget
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.CreateBbsResponse
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.*
import io.temco.guhada.data.server.CommunityServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import java.util.*

class CreateBbsViewModel(val context : Context) : BaseObservableViewModel() {

    var communityInfoList : SingleLiveEvent<ArrayList<CommunityInfo>> = SingleLiveEvent()
    var communityInfoMap : SortedMap<Int, CommunityCategory> = sortedMapOf()
    var communityCategoryMap : SortedMap<Int, SortedMap<Int,CommunityCategorySub>> = sortedMapOf()
    var totalCatergoyCount = 0

    val repository = CreateBbsRepository(this)
    var bbsData = CreateBbsResponse()

    var selectInfoIndex = 0

    var selectedImageIndex = -1

    var bbsPhotos: MutableLiveData<MutableList<String>> = MutableLiveData()

    init {
        bbsPhotos.value = mutableListOf()
    }

    var visibleImageCheckLayout = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.visibleImageCheckLayout)
        }


    var categoryTitle = ObservableField<String>("") //
        @Bindable
        get() = field

    var filterTitle = ObservableField<String>("") // 스피너 표시 메세지
        @Bindable
        get() = field

    var selectedCategoryIndex = -1
    var categoryList = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field

    var selectedFilterIndex = -1
    var selectedFilterInit = false
    var filterList = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field

    var filterListVisible = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.filterListVisible)
        }

    fun onBbsCategorySelected(position: Int) {
        if (selectedCategoryIndex != position) {
            selectedCategoryIndex = position
            val message = categoryList.get()!![position]
            categoryTitle.set(message)
            notifyPropertyChanged(BR.categoryTitle)
            if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "onBbsCategorySelected ", "position -----",position, "categoryTitle",categoryTitle.get()!!)
            setFilterList()
        }
    }


    fun onFilterSelect(position: Int) {
        if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "onFilterSelect ", "position -----",position,
                "selectedFilterIndex",selectedFilterIndex, "selectedFilterInit", selectedFilterInit)
        if (selectedFilterInit && selectedFilterIndex != position) {
            selectedFilterIndex = position
            val message = filterList.get()!![position]
            filterTitle.set(message)
            notifyPropertyChanged(BR.filterTitle)
        }
        selectedFilterInit = true
    }

    fun setCategoryList(){
        var list = arrayListOf<String>()
        for (index in 0 until communityInfoList.value!!.size){
            list.add(communityInfoList.value!![index].communityCategoryName)
        }
        categoryList.set(list)
        selectedCategoryIndex = -1
        notifyPropertyChanged(BR.categoryList)
        selectedFilterInit = false
    }

    private fun setFilterList(){
        if(communityInfoList.value!![selectedCategoryIndex].communityCategorySub.categoryFilterList.isNullOrEmpty()){
            filterListVisible.set(false)
        }else{
            filterListVisible.set(true)
            var list = arrayListOf<String>()
            for (index in 0 until communityInfoList.value!![selectedCategoryIndex].communityCategorySub.categoryFilterList.size){
                list.add(communityInfoList.value!![selectedCategoryIndex].communityCategorySub.categoryFilterList[index].name)
            }
            filterList.set(list)
            selectedFilterIndex = -1
            filterTitle.set("")
            notifyPropertyChanged(BR.filterList)
        }

    }





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

    private fun getCommunityCategory(categoryId : Int){
        CommunityServer.getCommunityCategoryAll(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategorySub>
                        for (info in data){
                            if(!communityCategoryMap.containsKey(categoryId) || communityCategoryMap.get(categoryId).isNullOrEmpty()){
                                var map : SortedMap<Int, CommunityCategorySub> = sortedMapOf()
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


    private  fun getCommunityCategoryFilter(communityId : Int, categoryId : Int){
        CommunityServer.getCategoryFilter(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).list as List<CommunityCategoryfilter>
                        communityCategoryMap.get(communityId)?.get(categoryId)?.categoryFilterList?.addAll(data)
                        totalCatergoyCount -= 1
                        if(totalCatergoyCount == 0){
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "setDetailView getCommunityCategoryFilter 0 ",communityCategoryMap.toString())
                            var list : ArrayList<CommunityInfo> = arrayListOf()
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

    fun postBbs(response: CreateBbsResponse, listener: OnCallBackListener){
        repository.postBbs(response, listener)
    }

    fun imageUpload(file : String, index : Int, listener : OnCallBackListener){
        repository.uploadImage(file, ImageUploadTarget.IMAGE_BBS.name, index, listener = listener)
    }

}


class CreateBbsRepository(val viewModel: CreateBbsViewModel){


    fun postBbs(response: CreateBbsResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.postBbsData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag) CustomLog.L("postBbs value",value)
                                        listener.callBackListener(true, "successTask")
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, response)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }


    fun uploadImage(fileNm : String, imageType : String, index : Int,listener : OnCallBackListener){
        GatewayServer.uploadImage(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as ImageResponse
                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "uploadImage failedTask ",data.toString())
                        data.index = index
                        listener.callBackListener(true,data)
                    },
                    dataNotFoundTask = { listener.callBackListener(false,"dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false,"failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false,"userLikeNotFoundTask") },
                    serverRuntimeErrorTask = {  listener.callBackListener(false,"serverRuntimeErrorTask") },
                    dataIsNull = { listener.callBackListener(false,"dataIsNull") }
            )
        },imageType,fileNm)
    }

}