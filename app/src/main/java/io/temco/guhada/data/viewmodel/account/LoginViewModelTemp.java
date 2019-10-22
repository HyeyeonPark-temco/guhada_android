package io.temco.guhada.data.viewmodel.account;

import androidx.annotation.Nullable;
import androidx.databinding.Bindable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.naver.NaverUser;
import io.temco.guhada.data.model.user.SnsUser;
import io.temco.guhada.data.model.user.User;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Login View Model (Java)
 * 미사용
 * @author Hyeyeon Park
 */
public class LoginViewModelTemp extends BaseObservableViewModel {
    public String toolBarTitle = "";
    private String id = Preferences.getSavedId(), pwd = "";
    private boolean buttonAvailable = false;
    private boolean isIdSaved = Preferences.isIdSaved();
    private OnLoginListener loginListener;
    private Object snsUser;
    private SnsUser tempSnsUser = new SnsUser();

    public LoginViewModelTemp(OnLoginListener listener) {
        this.loginListener = listener;

    }

    // GETTER & SETTER

    public boolean isIdSaved() {
        return isIdSaved;
    }

    public void setIdSaved(boolean idSaved) {
        isIdSaved = idSaved;
    }

    public Object getSnsUser() {
        return snsUser;
    }

    public void setSnsUser(Object snsUser) {
        this.snsUser = snsUser;
    }

    public SnsUser getTempSnsUser() {
        return tempSnsUser;
    }

    public void setTempSnsUser(SnsUser tempSnsUser) {
        this.tempSnsUser = tempSnsUser;
    }

    public void setId(String id) {
        this.id = id;

        if (buttonAvailable != (!id.isEmpty() && !pwd.isEmpty())) {
            buttonAvailable = !id.isEmpty() && !pwd.isEmpty();
            notifyPropertyChanged(BR.buttonAvailable);
        }
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;

        if (buttonAvailable != (!id.isEmpty() && !pwd.isEmpty())) {
            buttonAvailable = !id.isEmpty() && !pwd.isEmpty();
            notifyPropertyChanged(BR.buttonAvailable);
        }
    }

    public void setButtonAvailable(boolean buttonAvailable) {
        this.buttonAvailable = buttonAvailable;
    }

    @Bindable
    public String getId() {
        return id;
    }

    @Bindable
    public String getPwd() {
        return pwd;
    }

    @Bindable
    public boolean isButtonAvailable() {
        return buttonAvailable;
    }

    // CLICK LISTENER
    public void onClickGuestOrder() {

    }

    public void onClickBack() {
        loginListener.closeActivity(RESULT_CANCELED);
    }

    public void onClickSignIn() {
        if (CommonUtil.validateEmail(id)) {
            UserServer.signIn((success, o) -> {
                if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("LoginViewModel onClickSignIn","success",success);
                if (success) {
                    BaseModel model = ((BaseModel) o);
                    if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("LoginViewModel onClickSignIn","model.resultCode",model.resultCode);
                    switch (model.resultCode) {
                        case Flag.ResultCode.SUCCESS:
                            Token token = (Token) model.data;
                            if (Preferences.getToken() != null) Preferences.clearToken(false);
                            Preferences.setToken(token);
                            // save id
                            if (isIdSaved) {
                                String savedId = Preferences.getSavedId();
                                if (!savedId.equals(id)) {
                                    Preferences.setSavedId(id);
                                }
                            }

                            loginListener.closeActivity(RESULT_OK);
                            CommonUtil.debug(token.getAccessToken());
                            return;
                        case Flag.ResultCode.USER_NOT_FOUND:
                        case Flag.ResultCode.SIGNIN_INVALID_PASSWORD:
                            loginListener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.login_message_notequalpwd));
                    }
                } else {
                    loginListener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.common_message_servererror));
                    CommonUtil.debug((String) o);
                }
            }, new User(id, pwd));
        } else {
            loginListener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.login_message_wrongidformat));
        }
    }

    public void onClickSignUp() {
        loginListener.redirectJoinActivity();
    }

    public void onClickFindId() {

    }

    public void onClickFindPwd() {

    }

    public void onClickNaver() {
        loginListener.onNaverLogin();
    }

    public void onClickKakao() {
        if(CustomLog.getFlag())CustomLog.L("MyPageTempLoginActivity","requestCode onClickKakao");
        loginListener.onKakaoLogin();
    }

    public void onClickFacebook() {
        loginListener.onFacebookLogin();
    }

    public void onClickGoogle() {
        loginListener.onGoogleLogin();
    }

    public void onCheckedSaveId(boolean checked) {
        isIdSaved = checked;
        Preferences.setIsIdSaved(checked);
        if (!checked) {
            Preferences.setSavedId("");
        }
    }

    public void onClickFindAccount() {
        loginListener.redirectFindAccountActivity();
    }

    public void joinSnsUser(OnServerListener listener) {
        if (tempSnsUser.getSnsType() != null) {
            switch (tempSnsUser.getSnsType()) {
                case "KAKAO":
                    createSnsUser(Long.toString(((UserProfile) snsUser).getId()),
                            ((UserProfile) snsUser).getEmail(),
                            ((UserProfile) snsUser).getProfileImagePath(),
                            ((UserProfile) snsUser).getNickname());
                    break;
                case "NAVER":
                    createSnsUser(((NaverUser) snsUser).getId(),
                            ((NaverUser) snsUser).getEmail(),
                            ((NaverUser) snsUser).getProfileImage(),
                            ((NaverUser) snsUser).getName());
                    break;
                case "GOOGLE":
                    if (snsUser != null) {
                        createSnsUser(((GoogleSignInAccount) snsUser).getId(),
                                ((GoogleSignInAccount) snsUser).getEmail(),
                                ((GoogleSignInAccount) snsUser).getPhotoUrl() != null ? ((GoogleSignInAccount) snsUser).getPhotoUrl().toString() : null,
                                ((GoogleSignInAccount) snsUser).getDisplayName());
                    }

                    break;

                case "FACEBOOK":

                    break;
            }
        }

        UserServer.joinSnsUser(listener, tempSnsUser);
    }

    private void createSnsUser(String id, String email, @Nullable String imageUrl, String name) {
        tempSnsUser.setSnsId(id);
        tempSnsUser.setEmail(email);

        if (tempSnsUser.getUserProfile() == null)
            tempSnsUser.setUserProfile(new io.temco.guhada.data.model.user.UserProfile());

        tempSnsUser.getUserProfile().setName(name);
        if (imageUrl != null) tempSnsUser.getUserProfile().setImageUrl(imageUrl);
        tempSnsUser.getUserProfile().setSnsId(id);
        tempSnsUser.getUserProfile().setEmail(email);
    }

    public void facebookLogin(JSONObject object, OnServerListener serverListener) {
        try {
            String email = object.getString("email");
            String name = object.getString("name");

            // imageUrl
            JsonParser parser = new JsonParser();
            Object o = parser.parse(object.getString("picture"));
            JsonObject jsonObject = (JsonObject) o;
            JsonObject jsonObject1 = (JsonObject) parser.parse(jsonObject.get("data").toString());
            String imageUrl = jsonObject1.get("url").toString();
            String snsId = object.getString("id");

            // create SnsUser
            SnsUser user = new SnsUser();
            user.setEmail(email);
            user.setSnsId(snsId);
            user.setSnsType("FACEBOOK");

            io.temco.guhada.data.model.user.UserProfile profile = new io.temco.guhada.data.model.user.UserProfile();
            profile.setSnsId(snsId);
            profile.setEmail(email);
            profile.setName(name);
            profile.setImageUrl(imageUrl);
            user.setUserProfile(profile);

            this.tempSnsUser = user;
            UserServer.facebookLogin(serverListener, user);
        } catch (JSONException e) {
            CommonUtil.debug("[FACEBOOK] EXCEPTION: " + e.getMessage());
        }
    }
}
