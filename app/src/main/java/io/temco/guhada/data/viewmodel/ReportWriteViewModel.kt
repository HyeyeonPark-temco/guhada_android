package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.*
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.community.CommunityDetail
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ReportWriteViewModel(val context : Context) : BaseObservableViewModel() {
    val repository = ReportWriteRepository(this)

    var reportType = -1
    var reportTarget : ReportTarget = ReportTarget.NONE

    var productData : Product? = null
    var sellerName : String? = null

    var reportUserId : Int? = null
    var userData : User? = null

    var communityData : CommunityDetail? = null
    var commentData : Comments? = null

    var selectedImageIndex = -1

    var reportPhotos: MutableLiveData<MutableList<String>> = MutableLiveData()

    var writeUserInfo: MutableLiveData<User> = MutableLiveData()
    var reportTypeList: MutableLiveData<ArrayList<ReportTypeData>> = MutableLiveData()
    var userImageUrl: String = ""

    var userId : Long = 0L


    init {
        reportPhotos.value = mutableListOf()
    }


    var editTextReportTxtCount = ObservableField("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.editTextReportTxtCount)
        }


    var checkTermReport = ObservableBoolean(false) // ObservableInt(View.GONE)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkTermReport)
        }

    fun clickheckTermReport(){
        checkTermReport.set(!checkTermReport.get())
    }



    fun setInit(){
        if(reportTarget == ReportTarget.USER && reportUserId != null){
            repository.getUserInfo(reportUserId!!, object  : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(resultFlag){
                        userData = value as User
                        if(CustomLog.flag)CustomLog.L("ReportWriteRepository getUserInfo",resultFlag, "userData", (userData as User))
                    }
                    repository.getUserInfo(object : OnCallBackListener{
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            repository.getReportTypeData()
                            repository.getReportUserPhotoUrl()
                        }
                    })
                }
            })
        }else{
            repository.getUserInfo(object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    repository.getReportTypeData()
                    repository.getReportUserPhotoUrl()
                }
            })
        }
        repository.getUserInfo(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                repository.getReportTypeData()
                repository.getReportUserPhotoUrl()
                if(reportTarget == ReportTarget.USER && reportUserId != null){
                    repository.getUserInfo(reportUserId!!, object  : OnCallBackListener{
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            if(resultFlag){
                                userData = value as User
                                if(CustomLog.flag)CustomLog.L("ReportWriteRepository getUserInfo",resultFlag, "userData", (userData as User))
                            }
                        }
                    })
                }
            }
        })
    }


    var selectReportTypeIndex = -1

    var reportTypeMessage = ObservableField<String>("") // 스피너 표시 메세지
        @Bindable
        get() = field

    var reportTypeMessages = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field

    fun onReportTypeSelected(position: Int) {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onReportTypeSelected ", "position -----",position, "reportTypeMessage",reportTypeMessage)
        if (selectReportTypeIndex != position) {
            selectReportTypeIndex = position
            val message = reportTypeMessages.get()!![position]
            reportTypeMessage.set(message)
            notifyPropertyChanged(BR.reportTypeMessage)
        }
    }


    fun saveReport(reportResponse: ReportResponse, listener: OnCallBackListener){
        repository.saveReport(reportResponse, listener)
    }

    fun imageUpload(file : String, index : Int, listener : OnCallBackListener){
        repository.uploadImage(file, userImageUrl, index, listener = listener)
    }

}


class ReportWriteRepository(val viewModel: ReportWriteViewModel){

    fun getUserInfo(listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        val userId : Long = JWT(token.substring(7, token.length)).getClaim("userId").asLong()!!
                        UserServer.getUserById(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<User>).data
                                        viewModel.writeUserInfo.value = value
                                        listener.callBackListener(true, "successTask")
                                        if(CustomLog.flag)CustomLog.L("ReportWriteRepository User",value)
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        }, userId.toInt())
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }


    fun getUserInfo(id : Int, listener: OnCallBackListener){
        UserServer.getUserById(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<User>).data
                        viewModel.writeUserInfo.value = value
                        listener.callBackListener(true, "successTask")
                        if(CustomLog.flag)CustomLog.L("ReportWriteRepository User",value)
                    },
                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                    failedTask = { listener.callBackListener(false, "failedTask") },
                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
            )
        }, id)
    }
    fun getReportTypeData(){
        ClaimServer.getReportTypeList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<ReportTypeData>).list
                        var typeList = arrayListOf<ReportTypeData>()
                        var nameList = mutableListOf<String>()
                        for (type in value){
                            loop@for (target in type.targets){
                                if(viewModel.reportTarget.name == target){
                                    typeList.add(type)
                                    nameList.add(type.description)
                                    break@loop
                                }
                            }
                        }
                        viewModel.reportTypeMessages.set(nameList)
                        viewModel.notifyPropertyChanged(BR.reportTypeMessages)
                        viewModel.reportTypeList.value = typeList
                        viewModel.onReportTypeSelected(0)
                        if(CustomLog.flag)CustomLog.L("getReportTypeData typeList size",typeList.size)
                    },
                    dataNotFoundTask = { },
                    failedTask = { },
                    userLikeNotFoundTask = { },
                    serverRuntimeErrorTask = { }
            )
        })
    }


    fun getReportUserPhotoUrl(){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.getReportUserPhotoUrl(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<JsonObject>).data
                                        viewModel.userImageUrl = value.get("data").asString
                                        if(CustomLog.flag)CustomLog.L("getReportUserPhotoUrl value",value.get("data").asString)
                                    },
                                    dataNotFoundTask = { },
                                    failedTask = { },
                                    userLikeNotFoundTask = { },
                                    serverRuntimeErrorTask = { }
                            )
                        },token)
                    }
                }, invalidTokenTask = { })
    }


    fun saveReport(reportResponse: ReportResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.saveReport(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag)CustomLog.L("saveReport value",value)
                                        listener.callBackListener(true, "successTask")
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, reportResponse)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }


    fun uploadImage(fileNm : String, path : String,index : Int,listener : OnCallBackListener){
        GatewayServer.uploadImagePath(OnServerListener { success, o ->
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
        },path,fileNm)
    }

}