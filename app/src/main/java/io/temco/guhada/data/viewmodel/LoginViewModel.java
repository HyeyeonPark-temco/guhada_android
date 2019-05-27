package io.temco.guhada.data.viewmodel;

import androidx.databinding.Bindable;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

import static android.app.Activity.RESULT_CANCELED;

public class LoginViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "";
    private String id = "", pwd = "";
    private boolean buttonAvailable = false;
    private OnLoginListener loginListener;
    private Object snsUser;

    public LoginViewModel(OnLoginListener listener) {
        this.loginListener = listener;
    }

    // GETTER & SETTER

    public Object getSnsUser() {
        return snsUser;
    }

    public void setSnsUser(Object snsUser) {
        this.snsUser = snsUser;
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
                            loginListener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.login_message_invalidinfo));
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

    public void onClickFindAccount() {
        loginListener.redirectFindAccountActivity();
    }

}
