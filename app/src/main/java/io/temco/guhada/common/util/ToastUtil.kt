package io.temco.guhada.common.util

import android.annotation.SuppressLint
import android.os.Looper
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
object ToastUtil : FrameLayout(BaseApplication.getInstance().applicationContext) {
    private lateinit var mBinding: ViewToastBinding

    @JvmStatic
    fun showMessage(message: String) {
        if (Looper.myLooper() == null)
            Looper.prepare()

        if (!::mBinding.isInitialized) {
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_toast, this, false)
        }

        mBinding.message = message
//        mBinding.executePendingBindings()
        Toast(context)
                .apply {
                    duration = Toast.LENGTH_SHORT
                    view = mBinding.root
                }
                .let {
                    it.show()
                }
//        Looper.loop()
    }
}