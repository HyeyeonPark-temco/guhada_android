package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener
import io.temco.guhada.common.listener.OnJoinListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.account.JoinViewModel
import io.temco.guhada.databinding.ActivityJoinBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.BorderEditTextView

/**
 * 회원가입
 * @author Hyeyeon Park
 */
class JoinActivity : BindActivity<ActivityJoinBinding>() {
    private lateinit var mViewModel: JoinViewModel
    private var isEmailFocus = false
    private var isPassFocus = false

    override fun getBaseTag(): String = JoinActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_join
    override fun getViewType(): Type.View = Type.View.JOIN

    override fun init() {
        initViewModel()

        mBinding.purchaseClickListener = View.OnClickListener { CommonUtilKotlin.startTermsPurchase(this@JoinActivity) }
        mBinding.setPersonalClickListener { CommonUtilKotlin.startTermsPersonal(this@JoinActivity) }
        mBinding.setSaleClickListener { CommonUtilKotlin.startTermsSale(this@JoinActivity) }
        setEditTextFocusListener()

        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = JoinViewModel().apply {
            this.listener = object : OnJoinListener {
                override fun showMessage(message: String) {
                    Toast.makeText(this@JoinActivity, message, Toast.LENGTH_SHORT).show()
                }

                override fun closeActivity(resultCode: Int) {
                    if (resultCode == Activity.RESULT_OK) {
                        val intent = Intent(this@JoinActivity, CustomDialogActivity::class.java)
                        intent.putExtra("email", mViewModel.user.email)
                        this@JoinActivity.startActivityForResult(intent, Flag.RequestCode.WELCOME_DIALOG)
                    }
                    setResult(resultCode)
                    finish()
                }

                override fun showSnackBar(message: String) {
                    CommonUtil.showSnackBar(mBinding.linearlayoutJoinForm, message)
                }
            }
        }

        mViewModel.toolBarTitle = getString(R.string.join_title)
        mBinding.includeJoinHeader.viewModel = mViewModel
        mBinding.viewModel = mViewModel
    }

    private fun setEditTextFocusListener() {
        if (CustomLog.flag) CustomLog.L("JoinActivity", "setEditTextFocusListener")

        mBinding.edittextJoinEmail.setOnBorderEditTextFocusListener(object : OnBorderEditTextFocusListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!isEmailFocus) isEmailFocus = hasFocus
                if (isEmailFocus && !hasFocus) {
                    isEmailFocus = false
                    if (CustomLog.flag) CustomLog.L("JoinActivity", "edittextJoinEmail validationCheck", mBinding.edittextJoinEmail.text)

                    if (TextUtils.isEmpty(mBinding.edittextJoinEmail.text)) {
                        mBinding.textviewJoinEmailfocus.setText(R.string.findpwd_message_invalidemailformat_none)
                        mBinding.textviewJoinEmailfocus.visibility = View.VISIBLE
                    } else {
                        if (!TextUtils.isEmpty(mBinding.edittextJoinEmail.text) && !CommonUtil.validateEmail(mBinding.edittextJoinEmail.text)) {
                            mBinding.textviewJoinEmailfocus.setText(R.string.findpwd_message_invalidemailformat)
                            mBinding.textviewJoinEmailfocus.visibility = View.VISIBLE
                        } else
                            mBinding.textviewJoinEmailfocus.visibility = View.GONE
                    }
                } else
                    mBinding.textviewJoinEmailfocus.visibility = View.GONE
            }
        })

        mBinding.edittextJoinPassword.setOnBorderEditTextFocusListener(object : OnBorderEditTextFocusListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!isPassFocus) isPassFocus = hasFocus

                if (isPassFocus && !hasFocus) {
                    if (CustomLog.flag) CustomLog.L("JoinActivity", "edittextJoinPassword validationCheck", mBinding.edittextJoinPassword.text)

                    isPassFocus = false
                    mBinding.textviewJoinPasswordfocus.visibility =
                            if (!TextUtils.isEmpty(mBinding.edittextJoinPassword.text) && !CommonUtil.validatePassword(mBinding.edittextJoinPassword.text)) View.VISIBLE
                            else View.GONE
                } else
                    mBinding.textviewJoinPasswordfocus.visibility = View.GONE
            }
        })

        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinPassword) {
            val pas1 = mBinding.edittextJoinPassword.text
            val pas2 = mBinding.edittextJoinConfirmpassword.text
            if (!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && pas1 != pas2) {
                mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd)
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.VISIBLE
            } else {
                if (!CommonUtil.validatePassword(mBinding.edittextJoinPassword.text)) {
                    mBinding.textviewJoinPasswordfocus.visibility = View.VISIBLE
                } else {
                    mBinding.textviewJoinPasswordfocus.visibility = View.GONE
                    mBinding.textviewJoinConfirmpasswordfocus.visibility = View.GONE
                }
            }
            mViewModel.essentialChecked.set(mViewModel.essentialChecked.get())
            mViewModel.notifyPropertyChanged(BR.essentialChecked)
        }

        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinConfirmpassword) {
            val pas1 = mBinding.edittextJoinPassword.text
            val pas2 = mBinding.edittextJoinConfirmpassword.text
            if (!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && pas1 != pas2) {
                mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd)
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.VISIBLE
            } else
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.GONE
            mViewModel.essentialChecked.set(mViewModel.essentialChecked.get())
            mViewModel.notifyPropertyChanged(BR.essentialChecked)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Flag.RequestCode.WELCOME_DIALOG && resultCode == Activity.RESULT_OK) {
            finish()
        }
    }
}