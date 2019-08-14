package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import io.temco.guhada.BR
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.review.ReviewPhotos
import io.temco.guhada.data.model.review.ReviewWrMdResponse
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel

class ReviewWriteViewModel (val context : Context) : BaseObservableViewModel() {

    var mReviewEditPhotos: MutableLiveData<MutableList<ReviewPhotos>> = MutableLiveData()
    var selectedImageIndex = 0

    init {
        mReviewEditPhotos.value = arrayListOf()
    }

    var editTextReviewTxtCount = ObservableField("0")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.editTextReviewTxtCount)
        }


    var modifyReviewStatus = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.modifyReviewStatus)
        }


    var reviewSelectStatus1 = ObservableInt(1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus1)
        }

    var reviewSelectStatus2 = ObservableInt(1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus2)
        }

    var reviewSelectStatus3 = ObservableInt(1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.reviewSelectStatus3)
        }


    fun clickReviewSelectStatus1(select : Int){
        reviewSelectStatus1.set(select)
    }

    fun clickReviewSelectStatus2(select : Int){
        reviewSelectStatus2.set(select)
    }

    fun clickReviewSelectStatus3(select : Int){
        reviewSelectStatus3.set(select)
    }

    fun clickReviewWriteOrModify(data : ReviewWrMdResponse, productId : Long, reviewId : Int, listener : OnCallBackListener){
        if(CustomLog.flag)CustomLog.L("clickReviewWriteOrModify","ReviewWrMdResponse ", data.toString())
        if(modifyReviewStatus.get()){
            ServerCallbackUtil.callWithToken(
                    task = {
                        if (it != null){
                            UserServer.modifyReview(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = {
                                            var data = (o as BaseModel<*>).data as Any
                                            if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "deleteReview failedTask ",data.toString())
                                            listener.callBackListener(true,data)
                                        },
                                        dataNotFoundTask = { listener.callBackListener(false,"dataNotFoundTask") },
                                        failedTask = { listener.callBackListener(false,"failedTask") },
                                        userLikeNotFoundTask = { listener.callBackListener(false,"userLikeNotFoundTask") },
                                        serverRuntimeErrorTask = {  listener.callBackListener(false,"serverRuntimeErrorTask") },
                                        dataIsNull = { listener.callBackListener(false,"dataIsNull") }
                                )
                            }, accessToken = it, productId = productId, reviewId = reviewId, data = data)
                        }
                    }, invalidTokenTask = {  listener.callBackListener(false,"invalidTokenTask") })
        }else{
            ServerCallbackUtil.callWithToken(
                    task = {
                        if (it != null){
                            UserServer.writeReview(OnServerListener { success, o ->
                                ServerCallbackUtil.executeByResultCode(success, o,
                                        successTask = {
                                            var data = (o as BaseModel<*>).data as Any
                                            if (CustomLog.flag) CustomLog.L("MyPageReviewRepository", "deleteReview failedTask ",data.toString())
                                            listener.callBackListener(true,data)
                                        },
                                        dataNotFoundTask = { listener.callBackListener(false,"dataNotFoundTask") },
                                        failedTask = { listener.callBackListener(false,"failedTask") },
                                        userLikeNotFoundTask = { listener.callBackListener(false,"userLikeNotFoundTask") },
                                        serverRuntimeErrorTask = {  listener.callBackListener(false,"serverRuntimeErrorTask") },
                                        dataIsNull = { listener.callBackListener(false,"dataIsNull") }
                                )
                            }, accessToken = it, productId = productId, data = data)
                        }
                    }, invalidTokenTask = {  listener.callBackListener(false,"invalidTokenTask") })
        }
    }

}