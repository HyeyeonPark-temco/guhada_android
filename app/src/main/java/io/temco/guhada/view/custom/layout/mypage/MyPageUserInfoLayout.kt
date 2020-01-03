package io.temco.guhada.view.custom.layout.mypage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.AttributeSet
import com.auth0.android.jwt.JWT
import com.kakao.usermgmt.response.model.UserProfile
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.*
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.common.util.*
import io.temco.guhada.data.model.Token
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.viewmodel.account.LoginViewModel
import io.temco.guhada.data.viewmodel.mypage.MyPageUserInfoViewModel
import io.temco.guhada.databinding.CustomlayoutMypageUserinfoBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.activity.MyPageTempLoginActivity
import io.temco.guhada.view.activity.UserInfoActivity
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
    private lateinit var mLoginListener: OnSnsLoginListener


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
            if (mViewModel.mypageUserInfoLoginCheckType.get() == 3) {
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.FACEBOOK_LOGIN_MY)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.FACEBOOK_LOGIN_MY)
            } else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickGoogle {
            if (mViewModel.mypageUserInfoLoginCheckType.get() == 4) {
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.GOOGLE_LOGIN_MY)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.RC_GOOGLE_LOGIN_MY)
            } else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickKakao {
            if (CustomLog.flag) CustomLog.L("setOnClickKakao", "setOnClickKakao")
            if (mViewModel.mypageUserInfoLoginCheckType.get() == 2) {
                /*var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.KAKAO_LOGIN_MY)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.KAKAO_LOGIN_MY)*/
                mBinding.includeMypageuserinfoUserpassword.buttonLoginKakao.performClick()
            } else showLoginTypeUser()
        }
        mBinding.includeMypageuserinfoUserpassword.setOnClickNaver {
            if (mViewModel.mypageUserInfoLoginCheckType.get() == 1) {
                var intent = Intent(context, MyPageTempLoginActivity::class.java)
                intent.putExtra("request", Flag.RequestCode.NAVER_LOGIN_MY)
                (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.NAVER_LOGIN_MY)
            } else showLoginTypeUser()
        }

        if (checkUserLogin()) {
            setInitView()
        }


        // INIT SNS LOGIN
        mLoginListener = object : OnSnsLoginListener {
            override fun redirectTermsActivity(type: Int, data: Any?, email: String?) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener redirectTermsActivity")
                CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "회원정보를 찾을 수 없습니다.")
            }

            override fun kakaoLogin(result: UserProfile) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener kakaoLogin")
                SnsLoginModule.kakaoLogin(result, getSnsLoginServerListener())
            }

            override fun redirectMainActivity(data: Token) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener redirectMainActivity")
                val id = JWT(data.accessToken!!).getClaim("userId").asString()
                if (id?.toLong() ?: 0L != 0L) {
                    if (id?.toLong() ?: 0L == CommonUtil.checkUserId()) {
                        setUserData()
                    } else {
                        CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "현제 로그인된 회원과 다른 사용자입니다.")
                    }
                } else CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "회원정보를 찾을 수 없습니다.")
            }

            override fun showMessage(message: String) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener showMessage")
                //setResultFinish(Activity.RESULT_CANCELED,message)
            }
        }

        SnsLoginModule.initKakaoLogin(mLoginListener)

        setEventBus()
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
        CommonViewUtil.showDialog(context as MainActivity, message, false, false)
    }

    override fun onGoogleLogin() {}
    override fun onKakaoLogin() {}
    override fun onFacebookLogin() {}
    override fun onNaverLogin() {}
    override fun redirectJoinActivity() {}
    override fun redirectFindAccountActivity() {}
    override fun closeActivityAfterLogin(resultCode: Int, firstAppLogin: Boolean) {}

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

    override fun onResume() {
        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "onResume ", "init -----")
        if (checkUserLogin()) setInitView()
    }

    override fun onStart() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroy() {
        try {
            SnsLoginModule.removeKakaoCallback()
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    override fun onFocusView() {}
    override fun onReleaseView() {}

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

        if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "setInitView ", "false userId -----", mViewModel.userId)
        mViewModel.checkPasswordConfirm.set(false)
        mLoadingIndicatorUtil.show()
        mViewModel.userLoginType(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if (mViewModel.mypageUserInfoLoginCheckType.get() == 0) {
                    mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd.setEnable(true)
                    mUserInfoViewModel.id = CommonUtil.checkUserEmail()
                }
                mLoadingIndicatorUtil.dismiss()
            }
        })
    }

    private fun setUserData() {
        mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd.text = ""
        var intent = Intent(context as MainActivity, UserInfoActivity::class.java)
        intent.putExtra("loginType", mViewModel.mypageUserInfoLoginCheckType.get())
        (context as MainActivity).startActivityForResult(intent, Flag.RequestCode.USER_INFO)
    }


    @SuppressLint("CheckResult")
    private fun setEventBus() {
        EventBusHelper.mSubject.subscribe {
            if (it.requestCode == Flag.RequestCode.MYPAGE_USERINFO_LOGIN) {
                if (CustomLog.flag) CustomLog.L("MyPageUserInfoLayout", "EventBusHelper ", "it.data -----", it.data.toString())
                var result = it.data.toString().split(",")
                if (!TextUtils.isEmpty(it.data.toString()) && result.size > 1) {
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
        CommonViewUtil.hideKeyborad(mBinding.includeMypageuserinfoUserpassword.edittextviewLoginPwd, context)
        setUserData()
    }


    private fun getSnsLoginServerListener(): OnServerListener {
        return OnServerListener { success, o ->
            if (success) {
                val model = o as BaseModel<*>
                when (model.resultCode) {
                    Flag.ResultCode.SUCCESS -> {
                        // SNS 로그인
                        val token = model.data as Token
                        val id = JWT(token.accessToken!!).getClaim("userId").asString()
                        if (id?.toLong() ?: 0L != 0L) {
                            if (id?.toLong() ?: 0L == CommonUtil.checkUserId()) {
                                setUserData()
                            } else {
                                CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "현제 로그인된 회원과 다른 사용자입니다.")
                            }
                        } else CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "회원정보를 찾을 수 없습니다.")
                    }
                    Flag.ResultCode.DATA_NOT_FOUND ->
                        // SNS 회원가입
                        mUserInfoViewModel.joinSnsUser(OnServerListener { success1, o1 ->
                            if (success1) {
                                val m = o1 as BaseModel<Token>
                                if (m.resultCode == Flag.ResultCode.SUCCESS) {
                                    val t = Token()
                                    t.accessToken = m.data.accessToken
                                    t.refreshToken = m.data.refreshToken
                                    t.expiresIn = m.data.expiresIn
                                    //setResultFinish(Activity.RESULT_CANCELED,"회원정보를 찾을 수 없습니다.")
                                    CommonUtil.showSnackBarCoordinatorLayout(mBinding.includeMypageuserinfoUserpassword.linearlayoutLogin, "회원정보를 찾을 수 없습니다.")
                                } else {
                                    ToastUtil.showMessage(m.message)
                                }
                            }
                        })
                }
            } else {
                val message = o as String
                ToastUtil.showMessage(message)
            }
        }
    }

    override fun redirectLuckyDrawJoinActivity() {}
}