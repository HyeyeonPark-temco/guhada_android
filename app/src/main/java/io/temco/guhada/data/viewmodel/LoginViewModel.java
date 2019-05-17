package io.temco.guhada.data.viewmodel;

import androidx.databinding.Bindable;
import io.temco.guhada.BR;
import io.temco.guhada.common.BaseObservableViewModel;
import io.temco.guhada.common.listener.OnLoginListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.NaverUser;
import io.temco.guhada.data.server.LoginServer;

public class LoginViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "";
    private String id = "", pwd = "";
    private OnLoginListener listener;
    private NaverUser naverUser;

    public LoginViewModel(OnLoginListener listener) {
        this.listener = listener;
    }

    // GETTER & SETTER
    public void setId(String id) {
        this.id = id;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    @Bindable
    public String getId() {
        notifyPropertyChanged(BR.id);
        return id;
    }

    @Bindable
    public String getPwd() {
        notifyPropertyChanged(BR.pwd);
        return pwd;
    }

    // CLICK LISTENER
    public void onClickGuestOrder() {

    }

    public void onClickBack() {

    }

    public void onClickSignIn() {

    }

    public void onClickSignUp() {

    }

    public void onClickFindId() {

    }

    public void onClickFindPwd() {

    }

    public void onClickNaver() {
        listener.onNaverLogin();
    }

    public void onClickKakao() {
        listener.onKakaoLogin();
    }

    public void onClickFacebook() {
        listener.onFacebookLogin();
    }

    public void onClickGoogle() {
        listener.onGoogleLogin();
    }

    public void onCheckedSaveId(boolean checked) {

    }

    public void getNaverUserProfile(String accessToken) {
        OnServerListener listener = (success, o) -> {
            if (success) {
                naverUser = (NaverUser) o;
                CommonUtil.debug("[NAVER] SUCCESS: " + naverUser.getId());
            } else {
                CommonUtil.debug("[NAVER] FAILED: " + o.toString());
            }
        };

        LoginServer.getNaverProfile(listener, accessToken);
    }
}
