package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.enum.ImageUploadTarget
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.common.util.SingleLiveEvent
import io.temco.guhada.data.model.CreateBbsResponse
import io.temco.guhada.data.model.CreateBbsTempResponse
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
    var totalCategoryCount = 0
    var bbsId = 0L
    var bbsTempId = -1L

    val repository = CreateBbsRepository(this)
    var modifyBbsData = CreateBbsResponse()

    var selectedImageIndex = -1

    var bbsPhotos: MutableLiveData<MutableList<ImageResponse>> = MutableLiveData()

    init {
        bbsPhotos.value = mutableListOf()
    }

    var communityDetailModifyData = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.communityDetailModifyData)
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
        set(value) {
            field = value
            notifyPropertyChanged(BR.categoryTitle)
        }

    var filterTitle = ObservableField<String>("") // 스피너 표시 메세지
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.filterList)
        }

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
        if (CustomLog.flag) CustomLog.L("CreateBbsViewModel", "onBbsCategorySelected ", "position -----",position, "selectedCategoryIndex",selectedCategoryIndex)
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

    fun setFilterList(){
        if(communityInfoList.value!![selectedCategoryIndex].communityCategorySub.categoryFilterList.isNullOrEmpty()){
            filterListVisible.set(false)
            selectedFilterIndex = -1
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
        totalCategoryCount = 0
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
                        totalCategoryCount += data.size
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
                        totalCategoryCount -= 1
                        if(totalCategoryCount == 0){
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
                            if (CustomLog.flag) CustomLog.L("CommunityDetailViewModel", "getCommunityCategoryFilter totalCategoryCount ",totalCategoryCount)
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


    fun modifyBbs(response: CreateBbsResponse, listener: OnCallBackListener){
        repository.modifyBbs(bbsId, response, listener)
    }


    fun modifyBbsTemp(response: CreateBbsTempResponse, listener: OnCallBackListener){
        repository.modifyBbsTempData(bbsTempId, response, listener)
    }

    fun imageUpload(file : String, index : Int, listener : OnCallBackListener){
        repository.uploadImage(file, ImageUploadTarget.IMAGE_BBS.name, index, listener = listener)
    }


    fun postTempBbs(response: CreateBbsTempResponse, listener: OnCallBackListener){
        repository.postTempBbs(response, listener)
    }

    fun getBbsTempListData(listener: OnCallBackListener){
        repository.getBbsTempListData(listener)
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


    fun modifyBbs(id : Long, response: CreateBbsResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.modifyBbsData(OnServerListener { success, o ->
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
                        },token, id, response)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }


    fun postTempBbs(response: CreateBbsTempResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.postBbsTempData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag) CustomLog.L("postTempBbs value",value)
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



    fun modifyBbsTempData(id : Long, response: CreateBbsTempResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.modifyBbsTempData(OnServerListener { success, o ->
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
                        },token, id, response)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }




    fun getBbsTempListData(listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.getBbsTempListData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<CommunityTempInfo>).list
                                        if(CustomLog.flag) CustomLog.L("getBbsTempListData value",it)
                                        listener.callBackListener(true, value)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }


    fun uploadImage(fileNm : String, imageType : String, index : Int,listener : OnCallBackListener){
        GatewayServer.uploadImagePath2(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var data = (o as BaseModel<*>).data as ImageResponse
                        if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "uploadImage successTask ",data.toString())
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


    fun deleteTempData(id : Long, listener: OnCallBackListener?){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        CommunityServer.deleteBbsTempData(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<*>).data
                                        if(CustomLog.flag) CustomLog.L("getBbsTempListData value",value)
                                        listener?.callBackListener(true, value)
                                    },
                                    dataNotFoundTask = { listener?.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener?.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener?.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener?.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, id)
                    }
                }, invalidTokenTask = { listener?.callBackListener(false, "invalidTokenTask") })
    }


}