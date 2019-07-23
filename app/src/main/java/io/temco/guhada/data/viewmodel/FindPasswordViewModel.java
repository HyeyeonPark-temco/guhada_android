package io.temco.guhada.data.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CountTimer;
import io.temco.guhada.data.model.user.User;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

public class FindPasswordViewModel extends BaseObservableViewModel implements Observer {
    private OnFindPasswordListener listener;
    private boolean checkedFindPwdByEmail = false;
    private boolean checkedFindPwdByVerifyingPhone = false;
    private boolean checkedFindPwdByPhone = false;
    private User user = new User();
    private String verifyNumber = "", verifiedEmail = "", newPassword = "", newPasswordConfirm = "", di = "", mobile = "";

    // VERIFY
    private int verifyEmailVisibility = View.GONE;
    private int verifyPhoneVisibility = View.GONE;
    private int resultVisibility = View.GONE;
    private String timerMinute = "02";
    private String timerSecond = "60";

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public FindPasswordViewModel() {
        this.user.addObserver(this);
    }

    public FindPasswordViewModel(OnFindPasswordListener listener) {
        this.listener = listener;
        this.user.addObserver(this);
    }

    public String getDi() {
        return di;
    }

    public void setDi(String di) {
        this.di = di;
    }

    @Bindable
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
        notifyPropertyChanged(BR.newPassword);
    }

    @Bindable
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
        notifyPropertyChanged(BR.newPasswordConfirm);
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

    public void setVerifyNumber(String number) {
        this.verifyNumber = number;
        notifyPropertyChanged(BR.verifyNumber);
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
    public boolean isCheckedFindPwdByVerifyingPhone() {
        return checkedFindPwdByVerifyingPhone;
    }

    public void setCheckedFindPwdByVerifyingPhone(boolean checkedFindPwdByVerifyingPhone) {
        this.checkedFindPwdByVerifyingPhone = checkedFindPwdByVerifyingPhone;
    }


    public void onCheckedFindPwdByEmail(boolean checked) {
        if (checkedFindPwdByEmail != checked) {
            resetTimer();
            checkedFindPwdByEmail = checked;
            checkedFindPwdByPhone = false;
            checkedFindPwdByVerifyingPhone = false;
//            user = new User();

            notifyPropertyChanged(BR.checkedFindPwdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
//            notifyPropertyChanged(BR.user);
        }
    }

    public void onCheckedFindIdByPhone(boolean checked) {
        if (checkedFindPwdByPhone != checked) {
            resetTimer();
            checkedFindPwdByPhone = checked;
            checkedFindPwdByVerifyingPhone = false;
            checkedFindPwdByEmail = false;

            notifyPropertyChanged(BR.checkedFindPwdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
        }
    }

    public void onCheckedFindIdByVerifyingPhone(boolean checked) {
        if (checkedFindPwdByVerifyingPhone != checked) {
            resetTimer();
            checkedFindPwdByVerifyingPhone = checked;
            checkedFindPwdByEmail = false;
            checkedFindPwdByPhone = false;

            notifyPropertyChanged(BR.checkedFindPwdByVerifyingPhone);
            notifyPropertyChanged(BR.checkedFindPwdByEmail);
            notifyPropertyChanged(BR.checkedFindPwdByPhone);
        }
    }

    public void onClickVerifyPhone() {
        listener.redirectVerifyPhoneActivity();
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

    /**
     * 이메일로 인증번호 발송
     */
    public void onClickSendEmail() {
        if (CommonUtil.validateEmail(user.getEmail())) {
            listener.showLoadingIndicator();
            user.deleteObserver(this);
            UserServer.verifyEmail((success, o) -> {
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
                            listener.hideLoadingIndicator();
                        break;
                        case 6005:
                            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                    }
                } else {
                    String message = (String) o;
                    listener.showSnackBar(message);
                }
                user.addObserver(this);
                listener.hideLoadingIndicator();
            }, user);
        } else {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidemailformat));
        }
    }

    /**
     * 인증번호 검증
     */
    public void onClickVerifyNumber() {
        Verification verification = new Verification();
        verification.setVerificationNumber(verifyNumber);
        verification.setVerificationTarget(user.getEmail());

        if (checkedFindPwdByEmail) {
            verification.setVerificationTargetType("EMAIL");

        } else if (checkedFindPwdByPhone) {
            verification.setVerificationTargetType("MOBILE");
        }

        listener.showLoadingIndicator();
        user.deleteObserver(this);
        UserServer.verifyNumber((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        resultVisibility = View.VISIBLE;
                        verifiedEmail = user.getEmail();
                        notifyPropertyChanged(BR.resultVisibility);
                        notifyPropertyChanged(BR.verifiedEmail);
                        listener.hideKeyboard();
                        listener.hideLoadingIndicator();
                        break;
                    case Flag.ResultCode.EXPIRED_VERIFICATION_NUMBER:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                        listener.hideLoadingIndicator();
                        break;
                    case Flag.ResultCode.INVALID_VERIFICATION_NUMBER:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invaludverification));
                        listener.hideLoadingIndicator();
                        break;
                }
            } else {
                String message = (String) o;
                listener.showSnackBar(message);
            }
            listener.hideLoadingIndicator();
            user.addObserver(this);
        }, verification);
    }

    /**
     * 비밀번호 재설정 API 호출
     */
    public void onClickChangePassword() {
        if (newPassword.equals(newPasswordConfirm)) {
            if (CommonUtil.validatePassword(newPassword)) {
                Verification verification = new Verification();

                verification.setEmail(user.getEmail());
                verification.setNewPassword(newPassword);
                verification.setVerificationNumber(verifyNumber);
                verification.setDiCode(di);
                verification.setMobile(mobile);

                listener.showLoadingIndicator();
                user.deleteObserver(this);
                OnServerListener serverListener = (success, o) -> {
                    if (success) {
                        BaseModel model = (BaseModel) o;
                        listener.hideLoadingIndicator();
                        switch (model.resultCode) {
                            case Flag.ResultCode.SUCCESS:
                                listener.showMessage(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                // listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                                listener.closeActivity();
                                break;
                            case Flag.ResultCode.DATA_NOT_FOUND:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                                break;
                            case Flag.ResultCode.INVALID_VERIFICATION_NUMBER:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                                break;
                            case Flag.ResultCode.NOT_FOUND_VERIFY_INFO:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidverificationdata));
                                break;
                            default :
                                listener.showSnackBar(((BaseModel) o).message);
                        }
                    } else {
                        listener.showSnackBar((String) o);
                    }
                    listener.hideLoadingIndicator();
                    user.addObserver(this);
                };

                if (checkedFindPwdByVerifyingPhone) {
                    // 본인인증으로 비밀번호 재설정
                    UserServer.changePasswordByIdentifying(serverListener, verification);
                } else {
                    UserServer.changePassword(serverListener, verification);
                }
            } else {
                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidformat));
            }
        } else {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_notequalpwd));
        }
    }

    private void resetTimer() {
        CountTimer.stopTimer();
        timerMinute = "02";
        timerSecond = "60";
    }

    /**
     * 휴대폰으로 비밀번호 재설정
     * 인증 번호 요청
     */
    public void onClickSendPhone() {
        if (!CommonUtil.validateEmail(user.getEmail())) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidemailformat));
        } else {
            user.deleteObserver(this);
            UserServer.verifyPhone((success, o) -> {
                if (success) {
                    BaseModel model = (BaseModel) o;
                    if (model.resultCode == Flag.ResultCode.SUCCESS) {
                        resetTimer();

                        if (!verifyNumber.isEmpty()) {
                            listener.setVerifyNumberViewEmpty();
                        }

                        verifyPhoneVisibility = View.VISIBLE;
                        notifyPropertyChanged(BR.verifyPhoneVisibility);

                        CountTimer.startVerifyNumberTimer(timerSecond, timerMinute, new OnTimerListener() {
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

                    } else {
                        listener.showSnackBar(model.message);
                    }
                } else {
                    String message = o != null ? (String) o : "잠시 후 다시 시도해주세요.";
                    listener.showSnackBar(message);
                }

                user.addObserver(this);
            }, user);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            switch ((String) arg) {
                case "name":
                case "email":
                case "phoneNumber":
                case "mobile":
                    notifyPropertyChanged(BR.user);
                    break;
            }
        }
    }
}
