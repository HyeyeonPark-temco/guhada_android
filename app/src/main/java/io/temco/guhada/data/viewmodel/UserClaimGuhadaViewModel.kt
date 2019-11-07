package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.auth0.android.jwt.JWT
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.R
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


    var userId : Long = 0L
    var userImageUrl: String = ""
    var selectedImageIndex = -1

    var userClaimGuhadaImages: MutableLiveData<MutableList<String>> = MutableLiveData()

    var userClaimDescriptionData = mutableListOf<UserClaimGuhadaType>()
    var initUserClaimDescription = false
    var initUserClaimDescriptionChild = false

    var userClaimDescriptionList = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field

    var userClaimDescriptionChildList = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field

    lateinit var writeUserInfo : User

    init {
        userClaimGuhadaImages.value = arrayListOf()
        repository.getUserClaimGuhadaTypeList(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                userClaimDescriptionIndex.set(-1)
                userClaimDescriptionMessage.set(context.resources.getString(R.string.userclaim_guhada_hint1))
                notifyPropertyChanged(BR.userClaimDescriptionList)
                userClaimDescriptionChildIndex.set(-1)
                userClaimDescriptionChildMessage.set(context.resources.getString(R.string.userclaim_guhada_hint2))
            }
        })
        repository.getUserClaimGuhadaImage()
    }

    var userClaimDescriptionIndex = ObservableInt(-1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userClaimDescriptionIndex)
        }
    var userClaimDescriptionChildIndex = ObservableInt(-1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userClaimDescriptionChildIndex)
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


    fun getUserInfo(listener : OnCallBackListener){
        repository.getUserInfo(listener)
    }


    fun onUserClaimDescriptionSelected(position : Int){
        if(position != userClaimDescriptionIndex.get() && initUserClaimDescription){
            userClaimDescriptionMessage.set(userClaimDescriptionList.get()!!.get(position))
            userClaimDescriptionIndex.set(position)
            setUserClaimDescriptionChildList()
        }else initUserClaimDescription = true
    }


    fun onUserClaimDescriptionChildSelected(position : Int){
        if(position != userClaimDescriptionChildIndex.get() && initUserClaimDescriptionChild){
            userClaimDescriptionChildMessage.set(userClaimDescriptionChildList.get()!!.get(position))
            userClaimDescriptionChildIndex.set(position)
        }else initUserClaimDescriptionChild = true
    }

    fun onClickCheckTermUserClaim(){
        checkTermUserClaimGuhada.set(!checkTermUserClaimGuhada.get())
    }


    private fun setUserClaimDescriptionChildList(){
        if(userClaimDescriptionChildList.get().isNullOrEmpty()) userClaimDescriptionChildList.set(mutableListOf())
        else userClaimDescriptionChildList.get()!!.clear()
        for (i in userClaimDescriptionData.get(userClaimDescriptionIndex.get()).children!!){
            userClaimDescriptionChildList.get()!!.add(i.description)
        }
        userClaimDescriptionChildIndex.set(-1)
        userClaimDescriptionChildMessage.set(context.resources.getString(R.string.userclaim_guhada_hint2))
        notifyPropertyChanged(BR.userClaimDescriptionChildList)
    }


}


class UserClaimGuhadaRepository(val viewModel : UserClaimGuhadaViewModel){

    fun getUserInfo(listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        val userId : Long = JWT(token.substring(7, token.length)).getClaim("userId").asLong()!!
                        viewModel.userId = userId
                        UserServer.getUserById(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<User>).data
                                        viewModel.writeUserInfo = value
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


    fun getUserClaimGuhadaTypeList(listener: OnCallBackListener){
        ClaimServer.getUserClaimGuhadaTypeList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<UserClaimGuhadaType>).list
                        var nameList = mutableListOf<String>()
                        for (parent in value){
                            nameList.add(parent.description)
                        }
                        viewModel.userClaimDescriptionData = value
                        viewModel.userClaimDescriptionList.set(nameList)
                        if(CustomLog.flag) CustomLog.L("getUserClaimGuhadaTypeList nameList size", nameList)
                        listener.callBackListener(true, "successTask")
                    },
                    dataNotFoundTask = { },
                    failedTask = { },
                    userLikeNotFoundTask = { },
                    serverRuntimeErrorTask = { }
            )
        })
    }


    fun getUserClaimGuhadaImage(){
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
        GatewayServer.uploadImagePathQuery(OnServerListener { success, o ->
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