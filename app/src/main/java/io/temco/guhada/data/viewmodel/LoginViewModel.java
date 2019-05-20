package io.temco.guhada.data.viewmodel;

import androidx.databinding.Bindable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import io.temco.guhada.BR;
import io.temco.guhada.common.BaseObservableViewModel;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverUser;
import io.temco.guhada.data.model.SnsUser;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.UserProfile;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;

public class LoginViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "";
    private String id = "", pwd = "";
    private boolean buttonAvailable = false;
    private OnLoginListener loginListener;
    private NaverUser naverUser;

    public LoginViewModel(OnLoginListener listener) {
        this.loginListener = listener;
    }

    // GETTER & SETTER
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

    }

    public void onClickSignIn() {
        LoginServer.signIn((success, o) -> {
            if (success) {
                BaseModel model = ((BaseModel) o);
                switch (model.resultCode) {
                    case 200:
                        Token token = (Token) model.data;
                        loginListener.showMessage(token.getAccessToken());
                        return;
                    case 5004: // DATA NOT FOUND
                    case 6003: // WRONG PASSWORD
                        loginListener.showMessage(model.message);
                        return;
                }
            } else {
                loginListener.showMessage((String) o);
            }
        }, new User(id, pwd));
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
        loginListener.onKakaoLogin();
    }

    public void onClickFacebook() {
        loginListener.onFacebookLogin();
    }

    public void onClickGoogle() {
        loginListener.onGoogleLogin();
    }

    public void onCheckedSaveId(boolean checked) {

    }

    public void getNaverUserProfile(String accessToken) {
        OnServerListener listener = (success, o) -> {
            if (success) {
                naverUser = (NaverUser) o;
                loginListener.showMessage(naverUser.getEmail());
                CommonUtil.debug("[NAVER] SUCCESS: " + naverUser.getEmail());
            } else {
                CommonUtil.debug("[NAVER] FAILED: " + o.toString());
            }
        };

        LoginServer.getNaverProfile(listener, accessToken);
    }
}
