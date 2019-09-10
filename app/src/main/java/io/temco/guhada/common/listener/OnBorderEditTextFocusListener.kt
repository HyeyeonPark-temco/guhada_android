package io.temco.guhada.common.listener

import android.view.View


interface OnBorderEditTextFocusListener {
    fun onFocusChange(v : View?, hasFocus : Boolean )
}