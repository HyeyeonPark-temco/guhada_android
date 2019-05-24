package io.temco.guhada.data.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

public class FindPasswordViewModel extends BaseObservableViewModel {
    private OnFindPasswordListener listener;
    private boolean checkedFindPwdByEmail = false;
    private boolean checkedFindIdByVerifyingPhone = false;
    private boolean checkedFindPwdByPhone = false;
    private User user = new User();
    private String verifyNumber = "", verifiedEmail = "", newPassword = "", newPasswordConfirm = "";

    // VERIFY
    private int verifyEmailVisibility = View.GONE;
    private int resultVisibility = View.GONE;
    private int verifyPhoneVisibility = View.GONE;
    private String timerMinute = "02";
    private String timerSecond = "60";

    public FindPasswordViewModel() {
    }

    public FindPasswordViewModel(OnFindPasswordListener listener) {
        this.listener = listener;
    }

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
    public int getVerifyPhoneVisibility() {
        return verifyPhoneVisibility;
    }

    public void setVerifyPhoneVisibility(int verifyPhoneVisibility) {
        this.verifyPhoneVisibility = verifyPhoneVisibility;
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

    @Bindable
    public boolean isCheckedFindPwdByPhone() {
        return checkedFindPwdByPhone;
    }

    public void setCheckedFindPwdByPhone(boolean checkedFindPwdByPhone) {
        this.checkedFindPwdByPhone = checkedFindPwdByPhone;
    }

    @Bindable
    public boolean isCheckedFindIdByVerifyingPhone() {
        return checkedFindIdByVerifyingPhone;
    }

    public void setCheckedFindIdByVerifyingPhone(boolean checkedFindIdByVerifyingPhone) {
        this.checkedFindIdByVerifyingPhone = checkedFindIdByVerifyingPhone;
    }

    public void onCheckedFindPwdByEmail(boolean checked) {
        if (checkedFindPwdByEmail != checked) {
            checkedFindPwdByEmail = checked;
            checkedFindPwdByPhone = false;
            checkedFindIdByVerifyingPhone = false;
//            user = new User();

            notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
//            notifyPropertyChanged(BR.user);
        }
    }

    public void onCheckedFindIdByPhone(boolean checked) {
        if (checkedFindPwdByPhone != checked) {
            checkedFindPwdByPhone = checked;
            checkedFindIdByVerifyingPhone = false;
            checkedFindPwdByEmail = false;
//            user = new User();

            notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
//            notifyPropertyChanged(BR.user);
        }
    }

    public void onCheckedFindIdByVerifyingPhone(boolean checked) {
        if (checkedFindIdByVerifyingPhone != checked) {
            checkedFindIdByVerifyingPhone = checked;
            checkedFindPwdByEmail = false;
            checkedFindPwdByPhone = false;
//            user = new User();

            notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
//            notifyPropertyChanged(BR.user);

            if (checked) {
                listener.redirectVerifyPhoneActivity();
            }
        }
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

    public void onClickRedirectLogin() {
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

        if (checkedFindPwdByEmail) {
            verification.setVerificationTargetType("EMAIL");
        }
        if (checkedFindPwdByPhone) {
            // 휴대폰 번호로 비밀번호 재설정


        }

        LoginServer.verifyNumber((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        resultVisibility = View.VISIBLE;
                        verifiedEmail = user.getEmail();
                        notifyPropertyChanged(BR.resultVisibility);
                        notifyPropertyChanged(BR.verifiedEmail);
                        listener.hideKeyboard();
                        break;
                    case Flag.ResultCode.EXPIRED_VERIFICATION_NUMBER:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                        break;
                    case Flag.ResultCode.INVALID_VERIFICATION_NUMBER:
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
                            case Flag.ResultCode.SUCCESS:
                                listener.showMessage(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                listener.closeActivity();
                                break;
                            case Flag.ResultCode.DATA_NOT_FOUND:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                                break;
                            case Flag.ResultCode.INVALID_VERIFICATION_NUMBER:
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

    private void resetTimer() {
        timerMinute = "02";
        timerSecond = "60";
    }

    // 미완
    public void onClickSendPhone() {
        if (!CommonUtil.validateEmail(user.getEmail())) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidemailformat));
        } else {
            resetTimer();

            if(!verifyNumber.isEmpty()){
                listener.setVerifyNumberViewEmpty();
            }

            verifyPhoneVisibility = View.VISIBLE;
            notifyPropertyChanged(BR.verifyPhoneVisibility);


            CommonUtil.startVerifyNumberTimer(timerSecond, timerMinute, new OnTimerListener() {
                @Override
                public void changeSecond(String second) {
                    timerSecond = second;
                }

                @Override
                public void changeMinute(String minute) {
                    timerMinute = minute;
                }

                @Override
                public void notifyMinuteAndSecond() {
                    notifyPropertyChanged(BR.timerMinute);
                    notifyPropertyChanged(BR.timerSecond);
                }
            });

            listener.showMessage("[클릭] 휴대폰 번호로 비밀번호 재설정 - 인증번호 요청");
        }
    }

}
