package io.temco.guhada.common.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.databinding.ViewToastBinding

/**
 * Custom Toast Util
 * call ToastUtil.showMessage(message)
 * @author Hyeyeon Park
 */

@SuppressLint("ViewConstructor")
object ToastUtil : FrameLayout(BaseApplication.getInstance().applicationContext) {
    private var mBinding: ViewToastBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_toast, this, false)

    fun showMessage(message: String) {
        mBinding.message = message
        mBinding.executePendingBindings()

        Toast(context)
                .apply {
                    duration = Toast.LENGTH_SHORT
                    view = mBinding.root
                }
                .let {
                    it.show()
                }
    }
}