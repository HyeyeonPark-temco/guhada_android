package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.AttributeSet
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnLoginListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.account.LoginViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageUserInfoViewModel
import io.temco.guhada.databinding.CustomlayoutMypageUserinfoBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.activity.MyPageTempLoginActivity
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

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_userinfo
    override fun init() {
        mViewModel = MyPageUserInfoViewModel(context)
        mBinding.viewModel = mViewModel
        mUserInfoViewModel = LoginViewModel(this)
        mBinding.includeMypageuserinfoUserpassword.viewModel = mUserInfoViewModel
        mBinding.includeMypageuserinfoUserpassword.userInfoViewModel = mViewModel

        mBinding.includeMypageuserinfoUserpassword.setOnClickFacebook {
            var intent = Intent(context, MyPageTempLoginActivity::class.java)
            intent.putExtra("request",Flag.RequestCode.FACEBOOK_LOGIN)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.FACEBOOK_LOGIN)
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickGoogle {
            var intent = Intent(context, MyPageTempLoginActivity::class.java)
            intent.putExtra("request",Flag.RequestCode.GOOGLE_LOGIN)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.RC_GOOGLE_LOGIN)
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickKakao {
            var intent = Intent(context, MyPageTempLoginActivity::class.java)
            intent.putExtra("request",Flag.RequestCode.KAKAO_LOGIN)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.KAKAO_LOGIN)
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickNaver {
            var intent = Intent(context, MyPageTempLoginActivity::class.java)
            intent.putExtra("request",Flag.RequestCode.NAVER_LOGIN)
            (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.NAVER_LOGIN)
        }

        if(checkUserLogin()){
            setInitView()
        }
        setEventBus()
    }


    override fun onGoogleLogin() {

    }

    override fun onKakaoLogin() {

    }

    override fun onFacebookLogin() {

    }

    override fun onNaverLogin() {

    }

    override fun redirectJoinActivity() { }

    override fun redirectFindAccountActivity() {  }

    override fun showMessage(message: String) {
        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
    }

    override fun showSnackBar(message: String) {
        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
    }

    override fun closeActivity(resultCode: Int) {
        if(resultCode == Activity.RESULT_OK){
            successLogin()
        }
    }

    override fun onFocusView() {

    }

    override fun onResume() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "onResume ", "init -----")
        if(checkUserLogin()) setInitView()
    }

    override fun onStart() {
    }

    override fun onPause() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    private fun checkUserLogin() : Boolean{
        if(!CommonUtil.checkToken()){
            CommonViewUtil.showDialog(context as MainActivity, "로그인이 필요한 화면입니다.", false ,object : OnBaseDialogListener{
                override fun onClickOk() {
                    (context as MainActivity).moveMainTab(2)
                }
            })
            return false
        }else return true
    }


    private fun setInitView() {
        mViewModel.userId = CommonUtil.checkUserId()
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setInitView ", "userId -----",mViewModel.userId)
        mViewModel.checkPasswordConfirm.set(true)

        /*if(Preferences.getPasswordConfirm()){
            mViewModel.checkPasswordConfirm.set(true)
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "checkPasswordConfirm ", "true -----")
        }else{
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "checkPasswordConfirm ", "false -----")
            mViewModel.checkPasswordConfirm.set(false)
            mViewModel.userCheck(object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    if(CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener","resultFlag",resultFlag, "value",value)
                    if(CustomLog.flag) CustomLog.L("MyPageUserInfoLayout callBackListener",  "userEmail -----",mViewModel.userEmail)
                }
            })
        }*/
    }

    @SuppressLint("CheckResult")
    private fun setEventBus(){
        EventBusHelper.mSubject.subscribe {
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "EventBusHelper ", "it.data -----",it.data.toString())
            var result = it.data.toString().split(",")
            var resultCode = result[0].toInt()
            var message =  result[1]
            if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "EventBusHelper ", "resultCode -----",resultCode,"resultCode",resultCode)
            if(resultCode == Activity.RESULT_OK && !TextUtils.isEmpty(message)){
                var returnId = message.toLong()
                if(returnId == CommonUtil.checkUserId()){
                    successLogin()
                }else{
                    CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "현제 로그인된 회원과 다른 사용자입니다.")
                }
            }else{
                if(TextUtils.isEmpty(message)) message = "회원 확인중 오류가 발생되었습니다."
                CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, message)
            }
        }
    }

    private fun successLogin(){
        Preferences.setPasswordConfirm(true)
        mViewModel.checkPasswordConfirm.set(true)
    }


}