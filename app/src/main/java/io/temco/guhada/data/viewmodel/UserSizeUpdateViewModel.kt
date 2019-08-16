package io.temco.guhada.data.viewmodel

import android.content.Context
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import io.temco.guhada.view.custom.dialog.CustomMessageDialog

class UserSizeUpdateViewModel (val context : Context) : BaseObservableViewModel() {

    var userHeightTxtList : ArrayList<String> = arrayListOf()
    var userWeightTxtList : ArrayList<String> = arrayListOf()
    var userFootTxtList : ArrayList<String> = arrayListOf()

    init {
        var txtcm = context.getString(R.string.user_size_update_sub1_value)
        var txtkg = context.getString(R.string.user_size_update_sub2_value)
        var txtmm = context.getString(R.string.user_size_update_sub3_value)
        for (i in 120..220) userHeightTxtList.add(String.format(txtcm,i))
        for (i in 30..130) userWeightTxtList.add(String.format(txtkg,i))
        for (i in 200..320) userFootTxtList.add(String.format(txtmm,i))
    }

    var userSizeUpdateHeight = ObservableInt(-1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateHeight)
        }

    var userSizeUpdateWeight = ObservableInt(-1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateWeight)
        }
    var userSizeUpdateShoe = ObservableInt(-1)
        @Bindable
        get() = field
        set(value) {
            field = value
            notifyPropertyChanged(BR.userSizeUpdateShoe)
        }

    var userSizeUpdateTop = ObservableField("L")
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

    fun clickUserSizeBottom(select : Int){
        userSizeUpdateBottom.set(select)
    }

    fun clickUserSizeTop(select : String){
        userSizeUpdateTop.set(select)
    }

    fun clickUserSizeCheck(){
        userSizeUpdateCheckTerm.set(!userSizeUpdateCheckTerm.get())
    }

    fun onClickUpdate(){
        if(userSizeUpdateCheckTerm.get()){

        }else{
            CustomMessageDialog(message = context.resources.getString(R.string.user_size_update_check_desc),
                    cancelButtonVisible = false,
                    confirmTask = { }
            )
        }
    }



    fun onShippingSub1Selected(position: Int) {
        userSizeUpdateHeight.set(userHeightTxtList[position].substring(0,userHeightTxtList[position].length-2).toInt())
    }

    fun onShippingSub2Selected(position: Int) {
        userSizeUpdateWeight.set(userWeightTxtList[position].substring(0,userWeightTxtList[position].length-2).toInt())
    }

    fun onShippingSub3Selected(position: Int) {
        userSizeUpdateShoe.set(userFootTxtList[position].substring(0,userFootTxtList[position].length-2).toInt())
    }

}