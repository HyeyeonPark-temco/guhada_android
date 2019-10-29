package io.temco.guhada.data.viewmodel

import android.app.Activity
import android.content.Context
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.enum.PointPopupType
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ServerCallbackUtil
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.point.PointPopupInfo
import io.temco.guhada.data.model.user.UserSize
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.custom.dialog.CustomMessageDialog

class UserSizeUpdateViewModel(val context: Context) : BaseObservableViewModel() {

    private var userSize: UserSize = UserSize()

    var userHeightTxtList: ArrayList<String> = arrayListOf()
    var userWeightTxtList: ArrayList<String> = arrayListOf()
    var userFootTxtList: ArrayList<String> = arrayListOf()

    init {
        var txtcm = context.getString(R.string.user_size_update_sub1_value)
        var txtkg = context.getString(R.string.user_size_update_sub2_value)
        var txtmm = context.getString(R.string.user_size_update_sub3_value)
        /*var desc = *//*context.getString(R.string.common_select_spinner)*//*""
        userHeightTxtList.add(desc)
        userWeightTxtList.add(desc)
        userFootTxtList.add(desc)*/
        for (i in 120..220) userHeightTxtList.add(String.format(txtcm, i))
        for (i in 30..130) userWeightTxtList.add(String.format(txtkg, i))
        for (i in 200..320 step 10) userFootTxtList.add(String.format(txtmm, i))
    }


    fun setUserSize(isNewUserSize: Boolean, userSize: UserSize) {
        if (CustomLog.flag) CustomLog.L("UserSizeUpdateViewModel", "setUserSize", userSize.toString())
        this.userSizeUpdateisNew.set(isNewUserSize)
        this.userSize = userSize
        var hadler = Handler(context.mainLooper)
        hadler.postDelayed({
            userSizeUpdateHeight.set(userSize.height)
            userSizeUpdateWeight.set(userSize.weight)
            userSizeUpdateFoot.set(userSize.shoe)
            userSizeUpdateTop.set(userSize.top)
            userSizeUpdateBottom.set(userSize.bottom)
        }, 250)
    }


    var userSizeUpdateHeight = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateHeight)
        }

    var userSizeUpdateWeight = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateWeight)
        }
    var userSizeUpdateFoot = ObservableInt(0)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateFoot)
        }

    var userSizeUpdateTop = ObservableField("XS")
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateTop)
        }

    var userSizeUpdateBottom = ObservableInt(23)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateBottom)
        }

    var userSizeUpdateCheckTerm = ObservableBoolean(false)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateCheckTerm)
        }


    var userSizeUpdateisNew = ObservableBoolean(true)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateisNew)
        }


    fun clickUserSizeBottom(select: Int) {
        userSizeUpdateBottom.set(select)
    }

    fun clickUserSizeTop(select: String) {
        userSizeUpdateTop.set(select)
    }

    fun clickUserSizeCheck() {
        userSizeUpdateCheckTerm.set(!userSizeUpdateCheckTerm.get())
    }

    fun onClickUpdate() {
        if (userSizeUpdateCheckTerm.get()) {
            userSize.height = userSizeUpdateHeight.get()
            userSize.weight = userSizeUpdateWeight.get()
            userSize.shoe = userSizeUpdateFoot.get()
            userSize.bottom = userSizeUpdateBottom.get()
            userSize.top = userSizeUpdateTop.get()!!
            if (CustomLog.flag) CustomLog.L("UserSizeUpdateViewModel", "onClickUpdate ", userSize.toString())
            if (userSize.bottom > 0 && userSize.height > 0 && userSize.shoe > 0 && !"".equals(userSize.top) && userSize.weight > 0) {
                ServerCallbackUtil.callWithToken(
                        task = {
                            if (it != null) {
                                if (userSizeUpdateisNew.get()) {
                                    UserServer.saveUserSize(OnServerListener { success, o ->
                                        ServerCallbackUtil.executeByResultCode(success, o,
                                                successTask = {
                                                    val data = (o as BaseModel<*>).data as Any
                                                    if (CustomLog.flag) CustomLog.L("UserSizeUpdateViewModel", "saveUserSize successTask ", data.toString())

                                                    /**
                                                     * 포인트 적립 팝업 data 전달
                                                     * @author Hyeyeon Park
                                                     * @since 2019.10.29
                                                     */
                                                    if (data is PointPopupInfo)
                                                        CommonUtil.startPointDialogActivity(context as Activity, PointPopupType.SIZE.type, data)

                                                },
                                                dataNotFoundTask = { failDialogShow() },
                                                failedTask = { failDialogShow() },
                                                userLikeNotFoundTask = { failDialogShow() },
                                                serverRuntimeErrorTask = { failDialogShow() },
                                                dataIsNull = { failDialogShow() }
                                        )
                                    }, accessToken = it, data = userSize)
                                } else {
                                    UserServer.modifyUserSize(OnServerListener { success, o ->
                                        ServerCallbackUtil.executeByResultCode(success, o,
                                                successTask = {
                                                    var data = (o as BaseModel<*>).data as Any
                                                    if (CustomLog.flag) CustomLog.L("UserSizeUpdateViewModel", "modifyUserSize successTask ", data.toString())
                                                    CustomMessageDialog(message = context.resources.getString(R.string.user_size_update_modify_dialog_desc),
                                                            cancelButtonVisible = false,
                                                            confirmTask = {
                                                                (context as Activity).setResult(Activity.RESULT_OK)
                                                                (context as Activity).finish()
                                                            }
                                                    ).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "UserSizeUpdateViewModel")
                                                },
                                                dataNotFoundTask = { failDialogShow() },
                                                failedTask = { failDialogShow() },
                                                userLikeNotFoundTask = { failDialogShow() },
                                                serverRuntimeErrorTask = { failDialogShow() },
                                                dataIsNull = { failDialogShow() }
                                        )
                                    }, accessToken = it, data = userSize)
                                }
                            }
                        }, invalidTokenTask = { })
            } else {
                CustomMessageDialog(message = context.resources.getString(R.string.user_size_update_check_desc),
                        cancelButtonVisible = false,
                        confirmTask = { }
                ).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "UserSizeUpdateViewModel")
            }
        } else {
            CustomMessageDialog(message = context.resources.getString(R.string.user_size_update_check_desc),
                    cancelButtonVisible = false,
                    confirmTask = { }
            ).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "UserSizeUpdateViewModel")
        }
    }


    private fun failDialogShow() {
        CustomMessageDialog(message = "등록중 오류가 발생되었습니다.",
                cancelButtonVisible = false,
                confirmTask = {
                    (context as Activity).setResult(Activity.RESULT_CANCELED)
                    (context as Activity).finish()
                }
        ).show(manager = (context as AppCompatActivity).supportFragmentManager, tag = "UserSizeUpdateViewModel")
    }


    fun onShippingSub1Selected(position: Int) {
        userSizeUpdateHeight.set(userHeightTxtList[position].substring(0, userHeightTxtList[position].length - 2).toInt())
    }

    fun onShippingSub2Selected(position: Int) {
        userSizeUpdateWeight.set(userWeightTxtList[position].substring(0, userWeightTxtList[position].length - 2).toInt())
    }

    fun onShippingSub3Selected(position: Int) {
        userSizeUpdateFoot.set(userFootTxtList[position].substring(0, userFootTxtList[position].length - 2).toInt())
    }

}