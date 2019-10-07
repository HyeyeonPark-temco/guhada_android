package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.viewmodel.CustomDialogActivityViewModel
import io.temco.guhada.view.activity.base.BindActivity


/**
 * @author park jungho
 * 19.08.05
 * Side Menu Activity
 *
 */
class CustomDialogActivity : BindActivity<io.temco.guhada.databinding.ActivityCustomdialogBinding>() {

    private lateinit var mViewModel: CustomDialogActivityViewModel

    override fun getBaseTag(): String = CustomDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_customdialog
    override fun getViewType(): Type.View = Type.View.REVIEW_POINT_DIALOG

    override fun init() {
        mViewModel = CustomDialogActivityViewModel(this)
        var email = intent.getStringExtra("email") ?: CommonUtil.checkUserEmail()
        if(TextUtils.isEmpty(email)){
            mBinding.email = ""
        }else mBinding.email = email
        mBinding.setOnClickOk {
            setResult(Activity.RESULT_OK)
            finish()
        }
        mBinding.executePendingBindings()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    ////////////////////////////////////////////////////////////////////////////////
}