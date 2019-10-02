package io.temco.guhada.view.activity

import android.app.Activity
import android.text.TextUtils
import android.content.Intent
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.viewmodel.mypage.UserInfoViewModel
import io.temco.guhada.databinding.ActivityUserinfoBinding
import io.temco.guhada.view.activity.base.BindActivity
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.temco.guhada.BR
import io.temco.guhada.common.Flag
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.model.user.UserUpdateInfo
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
import io.temco.guhada.view.custom.BorderEditTextView

/**
 * @author park jungho
 * @author Hyeyeon Park
 *
 * 회원 정보 등록
 *
 */
class UserInfoActivity : BindActivity<ActivityUserinfoBinding>() {

    private lateinit var mViewModel: UserInfoViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private var isPassFocus = false


    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.activity_userinfo
    override fun getViewType(): Type.View = Type.View.USER_INFO


    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mViewModel = UserInfoViewModel(this@UserInfoActivity)
        mBinding.viewModel = mViewModel

        mBinding.setOnClickCloseButton { finish() }
        mBinding.setOnClickOkButton {
            if(CustomLog.flag)CustomLog.L("UserInfoActivity","init",mViewModel.user)
            var userUpInfo = UserUpdateInfo().apply { setData(mViewModel.user,null) }
            if(CustomLog.flag)CustomLog.L("UserInfoActivity","init userUpInfo",userUpInfo)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkUserLogin()) setInitView()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RequestCode.VERIFY_USERINFO.flag -> { // 이메일, 휴대폰번호 인증
                if (resultCode == Activity.RESULT_OK) {
                    val email = data?.getStringExtra("email")
                    val mobile = data?.getStringExtra("mobile")

                    if (email != null) mBinding.edittextMypageuserinfoEmail.text = email
                    if (mobile != null) mBinding.textviewMypageuserinfoMobile.text = mobile

                    mViewModel.user.email = email
                    mViewModel.user.mobile = mobile
                    mViewModel.user.phoneNumber = mobile

                    mBinding.executePendingBindings()
                }
            }
            Flag.RequestCode.USER_SIZE -> if (resultCode == Activity.RESULT_OK) getUserSize()
        }
    }

    private fun checkUserLogin(): Boolean {
        if (!CommonUtil.checkToken()) {
            CommonViewUtil.showDialog(this@UserInfoActivity, "로그인이 필요한 화면입니다.", false, object : OnBaseDialogListener {
                override fun onClickOk() {
                    finish()
                }
            })
            return false
        } else return true
    }


    private fun setInitView() {
        mViewModel.userId = CommonUtil.checkUserId()
        setUserData()
    }

    private fun setUserData() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setUserData ", "--/banks---", mViewModel.userId)
        // 유저 정보 가져오기
        mViewModel.userCheck(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                mBinding.user = mViewModel.user
                mBinding.executePendingBindings()
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener", "resultFlag", resultFlag, "value", value)
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener", "userEmail -----", mViewModel.userEmail)
            }
        })

        // 닉네임 변경
        setNickNameListener()

        // 환불 계좌정보
        initRefundAccountView()

        // 이메일 인증
        mBinding.edittextMypageuserinfoEmail.setOnClickListener { redirectUserInfoVerifyActivity(true) }

        // 휴대폰 번호 인증
        mBinding.textviewMypageuserinfoMobile.setOnClickListener { redirectUserInfoVerifyActivity(false) }

        // 유저 사이즈
        getUserSize()

        mBinding.buttonMypageuserinfoSizeinsert.setOnClickListener {
            var intent = Intent(this@UserInfoActivity, UserSizeUpdateActivity::class.java)
            if (mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (this@UserInfoActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }

        mBinding.buttonMypageuserinfoSizemodify.setOnClickListener {
            var intent = Intent(this@UserInfoActivity, UserSizeUpdateActivity::class.java)
            if (mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (this@UserInfoActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }
    }

    private fun redirectUserInfoVerifyActivity(isEmail: Boolean) {
        val intent = Intent(mBinding.root.context, VerifyUserInfoActivity::class.java)
        intent.putExtra("isEmail", isEmail)
        (mBinding.root.context as Activity).startActivityForResult(intent, RequestCode.VERIFY_USERINFO.flag)
    }

    private fun getUserSize() {
        mViewModel.repository.getUserSize(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if (resultFlag) {
                    setUserSize()
                } else {
                    mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.GONE
                    mBinding.buttonMypageuserinfoSizeinsert.visibility = View.VISIBLE
                    mBinding.buttonMypageuserinfoSizemodify.visibility = View.GONE
                }
            }
        })
    }


    private fun setUserSize() {
        if (mViewModel.mUserSize.weight > 0 || mViewModel.mUserSize.shoe > 0 || mViewModel.mUserSize.height > 0 || mViewModel.mUserSize.bottom > 0 || !TextUtils.isEmpty(mViewModel.mUserSize.top)) {
            mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.VISIBLE
            mBinding.buttonMypageuserinfoSizeinsert.visibility = View.GONE
            mBinding.buttonMypageuserinfoSizemodify.visibility = View.VISIBLE

            mBinding.textviewMypageuserinfoBottom.text = mViewModel.mUserSize.bottom.toString()
            mBinding.textviewMypageuserinfoHeight.text = mViewModel.mUserSize.height.toString()
            mBinding.textviewMypageuserinfoShoe.text = mViewModel.mUserSize.shoe.toString()
            mBinding.textviewMypageuserinfoTop.text = mViewModel.mUserSize.top
            mBinding.textviewMypageuserinfoWeight.text = mViewModel.mUserSize.weight.toString()
        } else {
            mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.GONE
            mBinding.buttonMypageuserinfoSizeinsert.visibility = View.VISIBLE
            mBinding.buttonMypageuserinfoSizemodify.visibility = View.GONE
        }
        setEditTextFocusListener()
        mBinding.executePendingBindings()
    }


    private fun setEditTextFocusListener() {
        if (CustomLog.flag) CustomLog.L("JoinActivity", "setEditTextFocusListener")
        mBinding.edittextJoinPassword.setOnBorderEditTextFocusListener(object : OnBorderEditTextFocusListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!isPassFocus) isPassFocus = hasFocus
                if (isPassFocus && !hasFocus) {
                    isPassFocus = false
                    if (CustomLog.flag) CustomLog.L("JoinActivity", "edittextJoinPassword validationCheck", mBinding.edittextJoinPassword.text)
                    if (!TextUtils.isEmpty(mBinding.edittextJoinPassword.text) && !CommonUtil.validatePassword(mBinding.edittextJoinPassword.text)) {
                        mBinding.textviewJoinPasswordfocus.visibility = View.VISIBLE
                    } else {
                        mBinding.textviewJoinPasswordfocus.visibility = View.GONE
                    }
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
        }
        BorderEditTextView.setInverseBindingListener(mBinding.edittextJoinConfirmpassword) {
            val pas1 = mBinding.edittextJoinPassword.text
            val pas2 = mBinding.edittextJoinConfirmpassword.text
            if (!TextUtils.isEmpty(pas1) && !TextUtils.isEmpty(pas2) && pas1 != pas2) {
                mBinding.textviewJoinConfirmpasswordfocus.setText(R.string.findpwd_message_notequalpwd)
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.VISIBLE
            } else
                mBinding.textviewJoinConfirmpasswordfocus.visibility = View.GONE
        }
    }


    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    private fun showLoginTypeUser() {
        var message = ""
        when (mViewModel.mypageUserInfoLoginCheckType.get()) {
            0 -> message = "이메일로 "
            1 -> message = "네이버로 "
            2 -> message = "카카오로 "
            3 -> message = "페이스북으로 "
            4 -> message = "구글로 "
        }
        message += "가입한 사용자 입니다."
        CommonViewUtil.showDialog(baseContext as MainActivity, message, false, false)
    }

    private fun setNickNameListener() {
        mBinding.edittextMypageuserinfoNickname.setOnFocusChangeListener { v, hasFocus ->
            if (!mViewModel.isNickNameFocus) mViewModel.isNickNameFocus = hasFocus
            if (mViewModel.isNickNameFocus && !hasFocus) {
                mViewModel.getUserByNickName()
            }
        }
    }

    private fun initRefundAccountView() {
        mViewModel.getRefundBanks()
        mBinding.includeMypageuserinfoBank.viewModel = mViewModel
        mViewModel.mBankNumInputAvailableTask = {
            if (mViewModel.mIsCheckAccountAvailable.get())
                mBinding.includeMypageuserinfoBank.buttonRequestrefundCheckaccount.text = resources.getString(R.string.requestorderstatus_refund_checkbank)
            else
                mBinding.includeMypageuserinfoBank.buttonRequestrefundCheckaccount.text = resources.getString(R.string.requestorderstatus_refund_checkbank_success)

            mBinding.executePendingBindings()
        }
        mViewModel.mRefundBanks.observe(this, Observer { banks ->
            val bankNameList = mutableListOf<String>()
            Observable.fromIterable(banks)
                    .map {
                        it.bankName
                    }.subscribe {
                        bankNameList.add(it)
                    }.dispose()

            bankNameList.add(mBinding.root.context.getString(R.string.requestorderstatus_refund_bankhint1))
            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.adapter = CommonSpinnerAdapter(context = mBinding.root.context, layoutRes = R.layout.item_common_spinner, list = bankNameList).apply {
                this.mItemCount = bankNameList.size - 1
            }
            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (position < banks.size) {
                        val selectedBank: PurchaseOrder.Bank? = banks[position]
                        if (selectedBank != null) {
                            mBinding.includeMypageuserinfoBank.textviewRequestrefundBankname.text = selectedBank.bankName
                            mViewModel.mRefundRequest.refundBankCode = selectedBank.bankCode

                            mViewModel.mIsCheckAccountAvailable = ObservableBoolean(true)
                            mViewModel.notifyPropertyChanged(BR.mIsCheckAccountAvailable)
                        }
                    }
                }
            }

            mViewModel.mBankAccount.observe(this, Observer {
                // 환불 계좌 정보
                val accountOwner = mViewModel.mRefundRequest.refundBankAccountOwner
                val accountNumber = mViewModel.mRefundRequest.refundBankAccountNumber
                val accountBankCode = mViewModel.mRefundRequest.refundBankCode

                mBinding.includeMypageuserinfoBank.edittextRequestrefundBankowner.setText(it.name)
            })

            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.setSelection(bankNameList.size - 1)
        })
    }


}