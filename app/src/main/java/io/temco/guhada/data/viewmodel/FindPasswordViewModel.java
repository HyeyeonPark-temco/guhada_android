package io.temco.guhada.data.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.retrofit.service.LoginService;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

public class FindPasswordViewModel extends BaseObservableViewModel {
    private OnFindPasswordListener listener;
    private boolean checkedFindPwdByEmail = false;
    private User user = new User();
    private String verifyNumber = "", verifiedEmail = "", newPassword = "", newPasswordConfirm = "";

    // VERIFY
    private int verifyEmailVisibility = View.GONE;
    private int resultVisibility = View.GONE;
    private int verifyNumberVisibility = View.GONE;
    private String timerMinute = "02";
    private String timerSecond = "60";

    @Bindable
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Bindable
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public int getVerifyNumberVisibility() {
        return verifyNumberVisibility;
    }

    public void setVerifyNumberVisibility(int verifyNumberVisibility) {
        this.verifyNumberVisibility = verifyNumberVisibility;
    }

    @Bindable
    public int getResultVisibility() {
        return resultVisibility;
    }

    public void setResultVisibility(int resultVisibility) {
        this.resultVisibility = resultVisibility;
    }

    @Bindable
    public String getVerifyNumber() {
        return verifyNumber;
    }

    public void setVerifyNumber(String verifyNumber) {
        this.verifyNumber = verifyNumber;
    }

    @Bindable
    public String getVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(String verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    @Bindable
    public String getTimerMinute() {
        return timerMinute;
    }

    public void setTimerMinute(String timerMinute) {
        this.timerMinute = timerMinute;
    }

    @Bindable
    public String getTimerSecond() {
        return timerSecond;
    }

    public void setTimerSecond(String timerSecond) {
        this.timerSecond = timerSecond;
    }

    @Bindable
    public boolean isCheckedFindPwdByEmail() {
        return checkedFindPwdByEmail;
    }

    public void setCheckedFindPwdByEmail(boolean checkedFindPwdByEmail) {
        this.checkedFindPwdByEmail = checkedFindPwdByEmail;
    }

    public void onCheckedFindPwdByEmail(boolean checked) {
        checkedFindPwdByEmail = checked;
        notifyPropertyChanged(BR.checkedFindPwdByEmail);
    }

    @Bindable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Bindable
    public int getVerifyEmailVisibility() {
        return verifyEmailVisibility;
    }

    public void setVerifyEmailVisibility(int verifyEmailVisibility) {
        this.verifyEmailVisibility = verifyEmailVisibility;
    }

    public OnFindPasswordListener getListener() {
        return listener;
    }

    public void setListener(OnFindPasswordListener listener) {
        this.listener = listener;
    }

    public void onClickRedirectLogin(){
        listener.closeActivity();
    }

    public void onClickSendEmail() {
        if (CommonUtil.validateEmail(user.getEmail())) {
            LoginServer.verifyEmail((success, o) -> {
                if (success) {
                    BaseModel model = (BaseModel) o;
                    switch (model.resultCode) {
                        case 200:
                            verifyEmailVisibility = View.VISIBLE;
                            notifyPropertyChanged(BR.verifyEmailVisibility);

                            int minute = (int) ((double) ((BaseModel) o).data / 60000);
                            if (String.valueOf(minute).length() == 1) {
                                listener.startTimer("0" + (minute - 1), "60");
                            } else {
                                listener.startTimer(String.valueOf(minute - 1), "60");
                            }
                            listener.hideKeyboard();
                            break;
                        case 6005:
                            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                            break;
                    }
                } else {
                    String message = (String) o;
                    listener.showSnackBar(message);
                }
            }, user);
        } else {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidemailformat));
        }
    }

    public void onClickVerifyNumber() {
        Verification verification = new Verification();
        verification.setVerificationNumber(verifyNumber);
        verification.setVerificationTarget(user.getEmail());
        verification.setVerificationTargetType("EMAIL");

        LoginServer.verifyNumber((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case 200:
                        resultVisibility = View.VISIBLE;
                        verifiedEmail = user.getEmail();
                        notifyPropertyChanged(BR.resultVisibility);
                        notifyPropertyChanged(BR.verifiedEmail);
                        listener.hideKeyboard();
                        break;
                    case 6004:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                        break;
                    case 6007:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invaludverification));
                        break;
                }
            } else {
                String message = (String) o;
                listener.showSnackBar(message);
            }
        }, verification);
    }

    public void onClickChangePassword() {
        if (newPassword.equals(newPasswordConfirm)) {
            if (CommonUtil.validatePassword(newPassword)) {
                Verification verification = new Verification();
                verification.setEmail(user.getEmail());
                verification.setNewPassword(newPassword);
                verification.setVerificationNumber(verifyNumber);
                LoginServer.changePassword((success, o) -> {
                    if (success) {
                        BaseModel model = (BaseModel) o;
                        switch (model.resultCode) {
                            case 200:
                                listener.showMessage(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                listener.closeActivity();
                                break;
                            case 5004:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                                break;
                            case 6004:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                                break;
                        }
                    }
                }, verification);
            } else {
                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidformat));
            }
        } else {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_notequalpwd));
        }
    }


}
