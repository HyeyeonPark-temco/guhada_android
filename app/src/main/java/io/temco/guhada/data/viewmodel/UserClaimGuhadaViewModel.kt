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
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.UserClaimGuhadaResponse
import io.temco.guhada.data.model.UserClaimGuhadaType
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class UserClaimGuhadaViewModel(val context : Context) : BaseObservableViewModel() {
    var repository = UserClaimGuhadaRepository(this)

    var userClaimDescriptionIndex = -1
    var userClaimDescriptionChildIndex = -1
    var userId : Long = 0L
    var userImageUrl: String = ""

    var userClaimGuhadaImages: MutableLiveData<MutableList<String>> = MutableLiveData()

    var userClaimDescription = arrayListOf("1","2","3")
    var userClaimDescriptionChild = arrayListOf("1","2","3")
    var writeUserInfo: MutableLiveData<User> = MutableLiveData()

    init {
        repository.getUserInfo(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
            }
        })
        repository.getUserClaimGuhadaTypeList()
    }

    var editTextUserClaimGuhadaTxtCount = ObservableField<String>("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.editTextUserClaimGuhadaTxtCount)
        }

    var userClaimDescriptionMessage = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userClaimDescriptionMessage)
        }

    var userClaimDescriptionChildMessage = ObservableField<String>("")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userClaimDescriptionChildMessage)
        }

    var checkTermUserClaimGuhada = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkTermUserClaimGuhada)
        }

    fun onUserClaimDescriptionSelected(position : Int){

    }

    fun onUserClaimDescriptionChildSelected(position : Int){

    }

    fun onClickCheckTermUserClaim(){
        checkTermUserClaimGuhada.set(!checkTermUserClaimGuhada.get())
    }


}


class UserClaimGuhadaRepository(val viewModel : UserClaimGuhadaViewModel){

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
                                        if(CustomLog.flag) CustomLog.L("ReportWriteRepository User",value)
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

    fun getUserClaimGuhadaTypeList(){
        ClaimServer.getUserClaimGuhadaTypeList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<UserClaimGuhadaType>).list
                        var typeList = arrayListOf<UserClaimGuhadaType>()
                        var nameList = mutableListOf<String>()
                        for (parent in value){
                            nameList.add(parent.description)
                        }
                        if(CustomLog.flag) CustomLog.L("getReportTypeData typeList size",typeList.size)
                    },
                    dataNotFoundTask = { },
                    failedTask = { },
                    userLikeNotFoundTask = { },
                    serverRuntimeErrorTask = { }
            )
        })
    }


    fun gettUserClaimGuhadaImage(){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.gettUserClaimGuhadaImage(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<JsonObject>).data
                                        viewModel.userImageUrl = value.get("data").asString
                                        if(CustomLog.flag) CustomLog.L("getReportUserPhotoUrl value",value.get("data").asString)
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


    fun saveUserClaimGuhada(userId : Long, response: UserClaimGuhadaResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.saveUserClaimGuhada(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<Any>).data
                                        if(CustomLog.flag) CustomLog.L("saveReport value",value)
                                        listener.callBackListener(true, "successTask")
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, userId, response)
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