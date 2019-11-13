package io.temco.guhada.data.viewmodel.account

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.ObservableBoolean
import com.auth0.android.jwt.JWT
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kakao.usermgmt.response.model.UserProfile
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.listener.OnLoginListener
import io.temco.guhada.common.listener.OnServerListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.TrackingUtil
import io.temco.guhada.data.model.Token
import io.temco.guhada.data.model.base.BaseModel
import io.temco.guhada.data.model.naver.NaverUser
import io.temco.guhada.data.model.user.SnsUser
import io.temco.guhada.data.model.user.User
import io.temco.guhada.data.server.UserServer
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel
import org.json.JSONException
import org.json.JSONObject

/**
 * Login View Model (Kotlin)
 * @author Hyeyeon Park
 * @since 2019.10.22
 */
class LoginViewModel(private val loginListener: OnLoginListener) : BaseObservableViewModel() {
    var toolBarTitle = ""
    var id = if (Preferences.isIdSaved()) Preferences.getSavedId() else ""
        @Bindable
        get() = field
        set(value) {
            field = value
            if (buttonAvailable != (!id.isEmpty() && !pwd.isEmpty())) {
                buttonAvailable = !id.isEmpty() && !pwd.isEmpty()
                notifyPropertyChanged(BR.buttonAvailable)
            }
        }
    var pwd = ""
        @Bindable
        get() = field
        set(value) {
            field = value
            if (buttonAvailable != (!id.isEmpty() && !pwd.isEmpty())) {
                buttonAvailable = !id.isEmpty() && !pwd.isEmpty()
                notifyPropertyChanged(BR.buttonAvailable)
            }
        }
    var buttonAvailable = false
        @Bindable
        get() = field
    var mIsIdSaved = ObservableBoolean(Preferences.isIdSaved())
        @Bindable
        get() = field
    var snsUser: Any? = null
    var tempSnsUser = SnsUser()

    // 럭키드로우 분기 falg
    var mIsEvent = false


    // CLICK LISTENER
    fun onClickGuestOrder() {

    }

    fun onClickBack() {
        loginListener.closeActivity(RESULT_CANCELED)
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_CancelButton.eventName)
    }

    fun onClickSignIn() {
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_InputId.eventName)
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_Inputpw.eventName)
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_LoginButton.eventName)
        signIn(email = id)
    }

    fun onClickMypageSignIn() {
        val savedEmail = Preferences.getSavedId()
        val email = if (savedEmail.isNullOrEmpty())
            Preferences.getToken().let { token ->
                JWT(token.accessToken ?: "").getClaim("user_name").asString()
            }
        else savedEmail

        if (!email.isNullOrEmpty()) signIn(email)
    }

    private fun signIn(email: String) {
        if (CommonUtil.validateEmail(email)) {
            UserServer.signIn(OnServerListener { success, o ->
                if (CustomLog.flag) CustomLog.L("LoginViewModel onClickSignIn", "success", success)
                if (success) {
                    val model = o as BaseModel<*>
                    if (CustomLog.flag) CustomLog.L("LoginViewModel onClickSignIn", "model.resultCode", model.resultCode)
                    when (model.resultCode) {
                        Flag.ResultCode.SUCCESS -> {
                            val token = model.data as Token
                            if (Preferences.getToken() != null) Preferences.clearToken(false)
                            Preferences.setToken(token)
                            // save id
                            if (mIsIdSaved.get())
                                Preferences.setSavedId(email)

                            loginListener.closeActivity(RESULT_OK)
                            CommonUtil.debug(token.accessToken)
                            return@OnServerListener
                        }
                        Flag.ResultCode.USER_NOT_FOUND, Flag.ResultCode.SIGNIN_INVALID_PASSWORD -> loginListener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.login_message_notequalpwd))
                    }
                } else {
                    loginListener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.common_message_servererror))
                    CommonUtil.debug(o as String)
                }
            }, User(email, pwd))
        } else {
            loginListener.showSnackBar(BaseApplication.getInstance().resources.getString(R.string.login_message_wrongidformat))
        }
    }

    fun onClickSignUp() {
        if (mIsEvent) {
            loginListener.redirectLuckyDrawJoinActivity()
        } else {
            loginListener.redirectJoinActivity()
            TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_SignUpButton.eventName)
        }
    }

    fun onClickFindId() {
        loginListener.redirectFindAccountActivity()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_FindIdButton.eventName)
    }

    fun onClickFindPwd() {
        loginListener.redirectFindAccountActivity()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_FindPwButton.eventName)
    }

    fun onClickNaver() {
        loginListener.onNaverLogin()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_NaverLoginButton.eventName)
    }

    fun onClickKakao() {
        if (CustomLog.flag) CustomLog.L("MyPageTempLoginActivity", "requestCode onClickKakao")
        loginListener.onKakaoLogin()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_KakaoLoginButton.eventName)
    }

    fun onClickFacebook() {
        loginListener.onFacebookLogin()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_FacebookLoginButton.eventName)
    }

    fun onClickGoogle() {
        loginListener.onGoogleLogin()
        TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_GoogleLoginButton.eventName)
    }

    fun onCheckedSaveId(checked: Boolean) {
        if (mIsIdSaved.get() != checked) {
            TrackingUtil.sendKochavaEvent(TrackingEvent.Login.Login_MainP_SaveIdClick.eventName)

            mIsIdSaved.set(checked)
            Preferences.setIsIdSaved(checked)
            if (!checked) {
                Preferences.setSavedId("")
            }
            notifyPropertyChanged(BR.mIsIdSaved)
        }
    }

    fun onClickFindAccount() {
        loginListener.redirectFindAccountActivity()
    }

    fun joinSnsUser(listener: OnServerListener) {
        if (tempSnsUser.snsType != null) {
            when (tempSnsUser.snsType) {
                "KAKAO" -> createSnsUser(
                        id = java.lang.Long.toString((snsUser as UserProfile).id),
                        email = (snsUser as UserProfile).email,
                        imageUrl = (snsUser as UserProfile).profileImagePath,
                        name = (snsUser as UserProfile).nickname)
                "NAVER" -> createSnsUser(
                        id = (snsUser as NaverUser).id,
                        email = (snsUser as NaverUser).email,
                        imageUrl = (snsUser as NaverUser).profileImage,
                        name = (snsUser as NaverUser).name)
                "GOOGLE" -> if (snsUser != null) {
                    createSnsUser(
                            id = (snsUser as GoogleSignInAccount).id,
                            email = (snsUser as GoogleSignInAccount).email,
                            imageUrl =
                            if ((snsUser as GoogleSignInAccount).photoUrl != null)
                                (snsUser as GoogleSignInAccount).photoUrl!!.toString()
                            else null,
                            name = (snsUser as GoogleSignInAccount).displayName)
                }
                "FACEBOOK" -> {
                }
            }
        }

        UserServer.joinSnsUser(listener, tempSnsUser)
    }

    private fun createSnsUser(id: String?, email: String?, imageUrl: String?, name: String?) {
        tempSnsUser.snsId = id
        tempSnsUser.email = email

        if (tempSnsUser.userProfile == null)
            tempSnsUser.userProfile = io.temco.guhada.data.model.user.UserProfile()

        tempSnsUser.userProfile!!.name = name
        if (imageUrl != null) tempSnsUser.userProfile!!.imageUrl = imageUrl
        tempSnsUser.userProfile!!.snsId = id
        tempSnsUser.userProfile!!.email = email
    }

    fun facebookLogin(`object`: JSONObject, serverListener: OnServerListener) {
        try {
            val email = `object`.getString("email")
            val name = `object`.getString("name")

            // imageUrl
            val parser = JsonParser()
            val o = parser.parse(`object`.getString("picture"))
            val jsonObject = o as JsonObject
            val jsonObject1 = parser.parse(jsonObject.get("data").toString()) as JsonObject
            val imageUrl = jsonObject1.get("url").toString()
            val snsId = `object`.getString("id")

            // create SnsUser
            val user = SnsUser()
            user.email = email
            user.snsId = snsId
            user.snsType = "FACEBOOK"

            val profile = io.temco.guhada.data.model.user.UserProfile()
            profile.snsId = snsId
            profile.email = email
            profile.name = name
            profile.imageUrl = imageUrl
            user.userProfile = profile

            this.tempSnsUser = user
            UserServer.facebookLogin(serverListener, user)
        } catch (e: JSONException) {
            CommonUtil.debug("[FACEBOOK] EXCEPTION: " + e.message)
        }

    }
}