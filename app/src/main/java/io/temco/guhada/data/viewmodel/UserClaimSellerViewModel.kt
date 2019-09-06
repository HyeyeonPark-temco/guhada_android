package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.ImageResponse
import io.temco.guhada.data.model.SellerInquireOrder
import io.temco.guhada.data.model.UserClaimSellerResponse
import io.temco.guhada.data.model.UserClaimSellerType
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.server.ClaimServer
import io.temco.guhada.data.server.GatewayServer
import io.temco.guhada.data.server.OrderServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class UserClaimSellerViewModel(val context : Context) : BaseObservableViewModel()  {
    val repository = UserClaimSellerRepository(this)
    var productId = -1L
    var sellerId = -1L
    var orderProdGroupId = -1L
    var userImageUrl = ""
    var userId : Long = 0L
    var selectedImageIndex = -1

    var sellerInquireOrderList = arrayListOf<SellerInquireOrder>()

    var userClaimSellerImages: MutableLiveData<MutableList<String>> = MutableLiveData()

    var userClaimSellerDescriptionData = mutableListOf<UserClaimSellerType>()
    var initUserClaimSellerDescription = false

    init {
        userClaimSellerImages.value = mutableListOf()
    }
    var userClaimSellerDescriptionList = ObservableField<MutableList<String>>(mutableListOf()) // 스피너 표시 메세지
        @Bindable
        get() = field



    var editTextUserClaimSellerTxtCount = ObservableField<String>("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.editTextUserClaimSellerTxtCount)
        }

    var checkUserClaimSellerOrderList = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.checkUserClaimSellerOrderList)
        }


    fun getSellerInquireOrder(listener: OnCallBackListener){
        repository.getSellerInquireOrder(sellerId, listener)
    }

}


class UserClaimSellerRepository(val viewModel : UserClaimSellerViewModel){


    fun getSellerInquireOrder(sellerId : Long, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        OrderServer.getSellerInquireOrder(OnServerListener { success, o ->
                            ServerCallbackUtil.executeByResultCode(success, o,
                                    successTask = {
                                        var value = (it as BaseModel<SellerInquireOrder>).list
                                        if(CustomLog.flag) CustomLog.L("getSellerInquireOrder value",value.size)
                                        var list = arrayListOf<SellerInquireOrder>()
                                        for (i in value){
                                            if(i.productId == viewModel.productId){
                                                if(viewModel.orderProdGroupId == -1L){
                                                    list.add(i)
                                                }else if(viewModel.orderProdGroupId == i.orderProdGroupId){
                                                    list.add(i)
                                                }
                                            }
                                        }
                                        viewModel.sellerInquireOrderList = list
                                        if(CustomLog.flag) CustomLog.L("getSellerInquireOrder list",list.size)
                                        listener.callBackListener(true, "successTask")
                                    },
                                    dataNotFoundTask = { listener.callBackListener(false, "dataNotFoundTask") },
                                    failedTask = { listener.callBackListener(false, "failedTask") },
                                    userLikeNotFoundTask = { listener.callBackListener(false, "userLikeNotFoundTask") },
                                    serverRuntimeErrorTask = { listener.callBackListener(false, "serverRuntimeErrorTask") }
                            )
                        },token, sellerId)
                    }
                }, invalidTokenTask = { listener.callBackListener(false, "invalidTokenTask") })
    }

    fun getUserClaimSellerTypeList(listener: OnCallBackListener){
        ClaimServer.getUserClaimSellerTypeList(OnServerListener { success, o ->
            ServerCallbackUtil.executeByResultCode(success, o,
                    successTask = {
                        var value = (it as BaseModel<UserClaimSellerType>).list
                        var nameList = mutableListOf<String>()
                        for (parent in value){
                            nameList.add(parent.description)
                        }
                        viewModel.userClaimSellerDescriptionData = value
                        viewModel.userClaimSellerDescriptionList.set(nameList)
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


    fun getUserClaimSellerImage(){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.getUserClaimSellerImage(OnServerListener { success, o ->
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


    fun saveUserClaimSeller(userId : Long, response: UserClaimSellerResponse, listener: OnCallBackListener){
        ServerCallbackUtil.callWithToken(
                task = { token ->
                    if (token != null) {
                        ClaimServer.saveUserClaimSeller(OnServerListener { success, o ->
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