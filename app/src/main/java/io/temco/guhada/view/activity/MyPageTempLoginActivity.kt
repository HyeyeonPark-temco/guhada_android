package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.auth0.android.jwt.JWT
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.kakao.usermgmt.response.model.UserProfile
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnLoginListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.listener.OnSnsLoginListener
import io.temco.guhada.common.sns.SnsLoginModule
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.Token
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.naver.NaverUser
import io.temco.guhada.data.viewmodel.account.LoginViewModel
import io.temco.guhada.databinding.ActivityMypagetemploginBinding
import io.temco.guhada.view.activity.base.BindActivity
import java.util.*


/**
 *
 * @author park jungho
 * 19.09.05
 * 마이페이지 sns 회원정보를 확인 할때 사용하는 Activity
 *
 */
class MyPageTempLoginActivity : BindActivity<ActivityMypagetemploginBinding>() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var mLoginListener: OnSnsLoginListener

    override fun getBaseTag(): String = MyPageTempLoginActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_mypagetemplogin
    override fun getViewType(): Type.View = Type.View.MYPAGE_TEMP_LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        this.overridePendingTransition(0, 0)
        super.onCreate(savedInstanceState)
    }

    override fun init() {
        initFacebookSdk()

        // INIT SNS LOGIN
        mLoginListener = object : OnSnsLoginListener {
            override fun redirectTermsActivity(type: Int, data: Any?, email: String?) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener redirectTermsActivity")
                setResultFinish(Activity.RESULT_CANCELED, "회원정보를 찾을 수 없습니다.")
                /*mViewModel.setSnsUser(data)
                val intent = Intent(this@MyPageTempLoginActivity, TermsActivity::class.java)
                startActivityForResult(intent, type)*/
            }

            override fun kakaoLogin(result: UserProfile) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener kakaoLogin")
                SnsLoginModule.kakaoLogin(result, getSnsLoginServerListener())
            }

            override fun redirectMainActivity(data: Token) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener redirectMainActivity")
                val id = JWT(data.accessToken!!).getClaim("userId").asString()
                setResultFinish(Activity.RESULT_OK, id + "")
                /*Preferences.setToken(data)
                val token = Preferences.getToken()
                Toast.makeText(this@MyPageTempLoginActivity, "[LOGIN SUCCESS] " + token!!.accessToken!!, Toast.LENGTH_SHORT).show()*/
            }

            override fun showMessage(message: String) {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "OnSnsLoginListener showMessage")
                setResultFinish(Activity.RESULT_CANCELED, message)
            }
        }

        SnsLoginModule.initFacebookLogin(object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response -> mViewModel.facebookLogin(`object`, getSnsLoginServerListener()) }
                val bundle = Bundle()
                bundle.putString("fields", "id,name,email,picture")
                graphRequest.parameters = bundle
                graphRequest.executeAsync()
            }

            override fun onCancel() {
                CommonUtil.debug("[FACEBOOK] CANCEL")
            }

            override fun onError(error: FacebookException) {
                CommonUtil.debug("[FACEBOOK] ERROR: $error")
            }
        })
        SnsLoginModule.initGoogleLogin()
        SnsLoginModule.initKakaoLogin(mLoginListener)
        SnsLoginModule.initNaverLogin(mBinding.buttonLoginNaver, mLoginListener, getSnsLoginServerListener())
        // INIT BINDING
        mViewModel = LoginViewModel(object : OnLoginListener {
            override fun onGoogleLogin() {
                startActivityForResult(Intent(SnsLoginModule.getGoogleClientInstance()), Flag.RequestCode.RC_GOOGLE_LOGIN)
            }

            override fun onKakaoLogin() {
                if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "buttonLoginKakao performClick")
                mBinding.buttonLoginKakao.performClick()
            }

            override fun onFacebookLogin() {
                mBinding.buttonLoginFacebook.setReadPermissions(Arrays.asList("public_profile", "email"))
                mBinding.buttonLoginFacebook.performClick()
            }

            @SuppressLint("HandlerLeak")
            override fun onNaverLogin() {
                mBinding.buttonLoginNaver.performClick()
            }

            override fun redirectJoinActivity() {
                setResultFinish(Activity.RESULT_CANCELED, "회원정보를 찾을 수 없습니다.")
            }

            override fun redirectFindAccountActivity() {
                setResultFinish(Activity.RESULT_CANCELED, "회원정보를 찾을 수 없습니다.")
            }

            override fun showMessage(message: String) {
                setResultFinish(Activity.RESULT_CANCELED, message)
            }

            override fun showSnackBar(message: String) {
                setResultFinish(Activity.RESULT_CANCELED, message)
            }

            override fun closeActivity(resultCode: Int) {
                setResultFinish(resultCode, "")
            }
        })
        mBinding.viewModel = mViewModel
        mBinding.includeLoginHeader.viewModel = mViewModel
        mBinding.executePendingBindings()

        if (intent.extras.containsKey("request")) {
            var requestCode = intent.extras?.getInt("request")
            if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "requestCode", requestCode!!)
            when (requestCode) {
                Flag.RequestCode.GOOGLE_LOGIN_MY -> mViewModel.onClickGoogle()
                Flag.RequestCode.NAVER_LOGIN_MY -> mViewModel.onClickNaver()
                Flag.RequestCode.FACEBOOK_LOGIN_MY -> mViewModel.onClickFacebook()
                Flag.RequestCode.KAKAO_LOGIN_MY -> mViewModel.onClickKakao()
            }
        }
    }

    private fun initFacebookSdk() {
        FacebookSdk.sdkInitialize(applicationContext)
        LoginManager.getInstance().logOut()
    }

    override fun onDestroy() {
        super.onDestroy()
        SnsLoginModule.removeKakaoCallback()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        SnsLoginModule.handlerActivityResultForFacebook(requestCode, resultCode, data)
        SnsLoginModule.handleActivityResultForKakao(requestCode, resultCode, data)

        if (mViewModel.snsUser == null) {
            if (CustomLog.flag) CustomLog.L("onActivityResult", "getSnsUser ", "null -----")
            SnsLoginModule.handleActivityResultForGoogle(requestCode, data, mLoginListener, getSnsLoginServerListener())
        }

        if (data != null && data.getBooleanExtra("agreeCollectPersonalInfoTos", false)) {
            val tempUser = mViewModel.tempSnsUser
            tempUser.agreeCollectPersonalInfoTos = data.getBooleanExtra("agreeCollectPersonalInfoTos", false)
            tempUser.agreeEmailReception = data.getBooleanExtra("agreeEmailReception", false)
            tempUser.agreePurchaseTos = data.getBooleanExtra("agreePurchaseTos", false)
            tempUser.agreeSaleTos = data.getBooleanExtra("agreeSaleTos", false)
            tempUser.agreeSmsReception = data.getBooleanExtra("agreeSmsReception", false)
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Flag.RequestCode.KAKAO_LOGIN_MY -> {
                    mViewModel.tempSnsUser.snsType = "KAKAO"
                    SnsLoginModule.kakaoLogin(mViewModel.snsUser as UserProfile, getSnsLoginServerListener())
                }
                Flag.RequestCode.NAVER_LOGIN_MY -> {
                    mViewModel.tempSnsUser.snsType = "NAVER"
                    SnsLoginModule.naverLogin(mViewModel.snsUser as NaverUser, getSnsLoginServerListener())
                }
                Flag.RequestCode.RC_GOOGLE_LOGIN_MY -> {
                    if (mViewModel.snsUser == null)
                        if (CustomLog.flag) CustomLog.L("onActivityResult RC_GOOGLE_LOGIN", "getSnsUser ", "null -----")
                    mViewModel.tempSnsUser.snsType = "GOOGLE"
                    SnsLoginModule.googleLogin(mViewModel.snsUser as GoogleSignInAccount?, getSnsLoginServerListener())
                }
                Flag.RequestCode.FACEBOOK_LOGIN_MY -> mViewModel.tempSnsUser.snsType = "FACEBOOK"
            }
        } else {
            mViewModel.snsUser = null
        }
        super.onActivityResult(requestCode, resultCode, data)
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
                        setResultFinish(Activity.RESULT_OK, "" + id)
                    }
                    Flag.ResultCode.DATA_NOT_FOUND ->
                        // SNS 회원가입
                        mViewModel.joinSnsUser(OnServerListener { success1, o1 ->
                            if (success1) {
                                val m = o1 as BaseModel<Token>
                                if (m.resultCode == Flag.ResultCode.SUCCESS) {
                                    val t = Token()
                                    t.accessToken = m.data.accessToken
                                    t.refreshToken = m.data.refreshToken
                                    t.expiresIn = m.data.expiresIn
                                    setResultFinish(Activity.RESULT_CANCELED, "회원정보를 찾을 수 없습니다.")
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

    override fun onBackPressed() {
        this@MyPageTempLoginActivity.overridePendingTransition(0, 0)
        super.onBackPressed()
    }


    private fun setResultFinish(resultCode: Int, msg: String) {
        var intent = Intent()
        intent.putExtra("resultMsg", msg)
        setResult(resultCode, intent)
        this@MyPageTempLoginActivity.overridePendingTransition(0, 0)
        finish()
    }

}