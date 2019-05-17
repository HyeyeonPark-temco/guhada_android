package io.temco.guhada.data.viewmodel;

import androidx.databinding.Bindable;

import io.temco.guhada.BR;
import io.temco.guhada.common.BaseObservableViewModel;
import io.temco.guhada.common.listener.OnLoginListener;

public class LoginViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "로그인";
    private String id = "", pwd = "";
    private OnLoginListener listener;

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

    }

    public void onClickKakao() {

    }

    public void onClickFacebook() {

    }

    public void onClickGoogle() {
        listener.onGoogleLogin();
       // GoogleSignIn.getClient()
    }

    public void onCheckedSaveId(boolean checked) {

    }

}
