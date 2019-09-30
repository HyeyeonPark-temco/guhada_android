package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnBorderEditTextFocusListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnLoginListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.data.viewmodel.account.LoginViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageUserInfoViewModel
import io.temco.guhada.databinding.CustomlayoutMypageUserinfoBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.activity.MyPageTempLoginActivity
import io.temco.guhada.view.activity.UserSizeUpdateActivity
import io.temco.guhada.view.activity.VerifyEmailActivity
import io.temco.guhada.view.adapter.CommonSpinnerAdapter
import io.temco.guhada.view.custom.BorderEditTextView
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 회원정보수정 화면
 *
 */
class MyPageUserInfoLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageUserinfoBinding, MyPageUserInfoViewModel>(context, attrs, defStyleAttr), OnLoginListener {

    private lateinit var mUserInfoViewModel: LoginViewModel
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private var isPassFocus = false


    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_userinfo


    override fun init() {
        mViewModel = MyPageUserInfoViewModel(context)
        mBinding.viewModel = mViewModel
        mUserInfoViewModel = LoginViewModel(this)
        mBinding.includeMypageuserinfoUserpassword.viewModel = mUserInfoViewModel
        mBinding.includeMypageuserinfoUserpassword.userInfoViewModel = mViewModel
        mLoadingIndicatorUtil = LoadingIndicatorUtil(context)

        // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
        mBinding.includeMypageuserinfoUserpassword.setOnClickFacebook {
            if(mViewModel.mypageUserInfoLoginCheckType.get() == 3){
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.FACEBOOK_LOGIN)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.FACEBOOK_LOGIN)
            }else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickGoogle {
            if(mViewModel.mypageUserInfoLoginCheckType.get() == 4){
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.GOOGLE_LOGIN)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.RC_GOOGLE_LOGIN)
            }else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickKakao {
            if(mViewModel.mypageUserInfoLoginCheckType.get() == 2){
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.KAKAO_LOGIN)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.KAKAO_LOGIN)
            }else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickNaver {
            if(mViewModel.mypageUserInfoLoginCheckType.get() == 1){
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.NAVER_LOGIN)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.NAVER_LOGIN)
            }else showLoginTypeUser()
        }

        if (checkUserLogin()) {
            setInitView()
        }

        setEventBus()
    }

    // 0 : email, 1 : naver, 2 : kakao, 3 : facebook, 4 : google
    private fun showLoginTypeUser(){
        var message = ""
        when(mViewModel.mypageUserInfoLoginCheckType.get()){
            0->message = "이메일로 "
            1->message = "네이버로 "
            2->message = "카카오로 "
            3->message = "페이스북으로 "
            4->message = "구글로 "
        }
        message += "가입한 사용자 입니다."
        CommonViewUtil.showDialog(context as MainActivity, message, false, false)
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
                mBinding.includeMypageuserinfoBank.edittextRequestrefundBankowner.setText(it.name)
            })

            mBinding.includeMypageuserinfoBank.spinnerRequestorderstatusBank.setSelection(bankNameList.size - 1)
        })
    }


    override fun onGoogleLogin() {

    }

    override fun onKakaoLogin() {

    }

    override fun onFacebookLogin() {

    }

    override fun onNaverLogin() {

    }

    override fun redirectJoinActivity() {}

    override fun redirectFindAccountActivity() {}

    override fun showMessage(message: String) {
        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
    }

    override fun showSnackBar(message: String) {
        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
    }

    override fun closeActivity(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            successLogin()
        }
    }

    override fun onFocusView() {

    }

    override fun onResume() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "onResume ", "init -----")
        if (checkUserLogin()) setInitView()
    }

    override fun onStart() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    private fun checkUserLogin(): Boolean {
        if (!CommonUtil.checkToken()) {
            CommonViewUtil.showDialog(context as MainActivity, "로그인이 필요한 화면입니다.", false, object : OnBaseDialogListener {
                override fun onClickOk() {
                    (context as MainActivity).moveMainTab(2)
                }
            })
            return false
        } else return true
    }


    private fun setInitView() {
        mViewModel.userId = CommonUtil.checkUserId()
        mViewModel.checkPasswordConfirm.set(true)

        mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd.setEnable(false)

        if(Preferences.getPasswordConfirm()){
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setInitView ", "true userId -----", mViewModel.userId)
            mViewModel.checkPasswordConfirm.set(true)
            setUserData()
        }else{
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setInitView ", "false userId -----", mViewModel.userId)
            mViewModel.checkPasswordConfirm.set(false)
            mLoadingIndicatorUtil.show()
            mViewModel.userLoginType(object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(mViewModel.mypageUserInfoLoginCheckType.get() == 0){
                        mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd.setEnable(true)
                        mUserInfoViewModel.id = CommonUtil.checkUserEmail()
                    }
                    mLoadingIndicatorUtil.dismiss()
                }
            })
        }
    }

    private fun setUserData(){
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setUserData ", "--/banks---", mViewModel.userId)
        mViewModel.userCheck(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener","resultFlag",resultFlag, "value",value)
                if(CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener",  "userEmail -----",mViewModel.userEmail)
            }
        })

        // 닉네임 변경
        setNickNameListener()

        // 환불 계좌정보
        initRefundAccountView()

        // 이메일
        mBinding.edittextMypageuserinfoEmail.setOnClickListener {
            val intent = Intent(mBinding.root.context, VerifyEmailActivity::class.java)
            (mBinding.root.context as Activity).startActivityForResult(intent, RequestCode.VERIFY_EMAIL.flag)
        }

        // 유저 사이즈
        getUserSize()


        mBinding.buttonMypageuserinfoSizeinsert.setOnClickListener {
            var intent = Intent(context as MainActivity, UserSizeUpdateActivity::class.java)
            if(mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }

        mBinding.buttonMypageuserinfoSizemodify.setOnClickListener {
            var intent = Intent(context as MainActivity, UserSizeUpdateActivity::class.java)
            if(mViewModel.mUserSize != null) intent.putExtra("userSize", mViewModel.mUserSize)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.USER_SIZE)
        }
    }


    private fun getUserSize(){
        mViewModel.repository.getUserSize(object : OnCallBackListener{
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if(resultFlag){
                    setUserSize()
                }else{
                    mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.GONE
                    mBinding.buttonMypageuserinfoSizeinsert.visibility = View.VISIBLE
                    mBinding.buttonMypageuserinfoSizemodify.visibility = View.GONE
                }
            }
        })
    }


    private fun setUserSize(){
        if(mViewModel.mUserSize.weight > 0 || mViewModel.mUserSize.shoe > 0 || mViewModel.mUserSize.height > 0 || mViewModel.mUserSize.bottom > 0 || !TextUtils.isEmpty(mViewModel.mUserSize.top)){
            mBinding.linearlayoutMypageuserinfoSizeinfo.visibility = View.VISIBLE
            mBinding.buttonMypageuserinfoSizeinsert.visibility = View.GONE
            mBinding.buttonMypageuserinfoSizemodify.visibility = View.VISIBLE

            mBinding.textviewMypageuserinfoBottom.text = mViewModel.mUserSize.bottom.toString()
            mBinding.textviewMypageuserinfoHeight.text = mViewModel.mUserSize.height.toString()
            mBinding.textviewMypageuserinfoShoe.text = mViewModel.mUserSize.shoe.toString()
            mBinding.textviewMypageuserinfoTop.text = mViewModel.mUserSize.top
            mBinding.textviewMypageuserinfoWeight.text = mViewModel.mUserSize.weight.toString()
        }else{
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


    @SuppressLint("CheckResult")
    private fun setEventBus() {
        EventBusHelper.mSubject.subscribe {
            if (it.requestCode == RequestCode.VERIFY_EMAIL.flag) {
                val email = it.data as String?
                mBinding.edittextMypageuserinfoEmail.text = email ?: ""
                mBinding.executePendingBindings()
            } else if(it.requestCode == Flag.RequestCode.USER_SIZE){
                getUserSize()
            }else {
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "EventBusHelper ", "it.data -----", it.data.toString())
                var result = it.data.toString().split(",")
                if(!TextUtils.isEmpty(it.data.toString()) && result.size > 1){
                    var resultCode = result[0].toInt()
                    var message: String? = result[1]
                    if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "EventBusHelper ", "resultCode -----", resultCode, "resultCode", resultCode)
                    if (resultCode == Activity.RESULT_OK && !TextUtils.isEmpty(message)) {
                        var returnId = message?.toLong()
                        if (returnId == CommonUtil.checkUserId()) {
                            successLogin()
                        } else {
                            CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "현제 로그인된 회원과 다른 사용자입니다.")
                        }
                    } else {
                        if (TextUtils.isEmpty(message)) message = "회원 확인중 오류가 발생되었습니다."
                        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
                    }
                }
            }
        }
    }


    private fun successLogin() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "successLogin ----------------------------------------")
        Preferences.setPasswordConfirm(true)
        mViewModel.checkPasswordConfirm.set(true)
        CommonViewUtil.hideKeyborad(mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd, context)
        setUserData()
    }


}