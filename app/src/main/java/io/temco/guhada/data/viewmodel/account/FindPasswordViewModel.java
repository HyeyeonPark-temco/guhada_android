package io.temco.guhada.data.viewmodel.account;

import android.text.TextUtils;
import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableInt;

import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.listener.OnFindPasswordListener;
import io.temco.guhada.common.listener.OnServerListener;
import io.temco.guhada.common.listener.OnTimerListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CountTimer;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.ServerCallbackUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.common.util.ToastUtil;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.Verification;
import io.temco.guhada.data.model.base.BaseErrorModel;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.user.User;
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
    private ObservableInt resultVisibility = new ObservableInt(View.GONE);
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
    public ObservableInt getResultVisibility() {
        return resultVisibility;
    }

    public void setResultVisibility(ObservableInt resultVisibility) {
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
     * <p>
     * response 변경 (double -> object)
     * { data: {data : Double}, .. }
     * updated 2019.09.09
     *
     * @author Hyeyeon Park
     */
    public void onClickSendEmail() {
        if (CommonUtil.validateEmail(user.getEmail())) {
            listener.showLoadingIndicator();
            user.deleteObserver(this);

            if (CountTimer.isResendable()) {
                UserServer.verifyEmail((success, o) -> {
                    listener.hideLoadingIndicator();

                    if (success) {
                        BaseModel model = (BaseModel) o;
                        switch (model.resultCode) {
                            case 200:
                                verifyEmailVisibility = View.VISIBLE;
                                notifyPropertyChanged(BR.verifyEmailVisibility);

                                Object result = ((LinkedTreeMap) ((BaseModel) o).data).get("data");
                                int second;
                                if (result != null) second = (int) Math.round((Double) result);
                                else second = 600000;
                                int minute = second / 60000;

                                CountTimer.startVerifyNumberTimer("00", String.valueOf(minute), new OnTimerListener() {
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
                                        notifyPropertyChanged(BR.timerSecond);
                                        notifyPropertyChanged(BR.timerMinute);
                                    }
                                });
                                listener.hideKeyboard();
                                break;
                            case 6005:
                                listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_wronginfo));
                                break;
                            default:
                                listener.showSnackBar(model.message);
                        }
                    } else {
                        String message = (String) o;
                        listener.showSnackBar(message);
                    }

                    user.addObserver(this);
                }, user);
            }
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

        if (checkedFindPwdByEmail) {
            verification.setVerificationTargetType("EMAIL");
            verification.setVerificationTarget(user.getEmail());
        } else if (checkedFindPwdByPhone) {
            verification.setVerificationTargetType("MOBILE");
            verification.setVerificationTarget(user.getMobile());
        }

        listener.showLoadingIndicator();
        user.deleteObserver(this);
        UserServer.verifyNumber((success, o) -> {
            BaseModel model = (BaseModel) o;
            if (success) {
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        resultVisibility = new ObservableInt(View.VISIBLE);
                        verifiedEmail = user.getEmail();
                        notifyPropertyChanged(BR.resultVisibility);
                        notifyPropertyChanged(BR.verifiedEmail);
                        break;
                    case Flag.ResultCode.EXPIRED_VERIFICATION_NUMBER:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_expiredverification));
                        listener.hideLoadingIndicator();
                        break;
                    case Flag.ResultCode.INVALID_VERIFICATION_NUMBER:
                        listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invaludverification));
                        listener.hideLoadingIndicator();
                        break;
                    default:
                        listener.showSnackBar(model.message);
                }
            } else {
                listener.showSnackBar(model.message);
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
                verification.setEmail(user.getEmail() == null ? "" : user.getEmail());
                verification.setNewPassword(newPassword);
                verification.setVerificationNumber(verifyNumber);
                verification.setDiCode(di);
                verification.setVerificationTarget(user.getMobile());
                verification.setVerificationTargetType(Verification.IdentityVerifyMethod.MOBILE.getCode());
                verification.setPhoneNumber(user.getMobile() == null ? "" : user.getMobile());
                verification.setMobile(user.getMobile() == null ? "" : user.getMobile());
                verification.setName(user.getName() == null ? "" : user.getName());

                if (CustomLog.INSTANCE.getFlag())
                    CustomLog.INSTANCE.L("비밀번호 재설정 body", verification.toString());
                listener.showLoadingIndicator();
                user.deleteObserver(this);
                OnServerListener serverListener = (success, o) -> {
                    BaseModel model = (BaseModel) o;
                    if (success && model.resultCode == Flag.ResultCode.SUCCESS) {
                        listener.hideLoadingIndicator();

                        if (CustomLog.INSTANCE.getFlag())
                            CustomLog.INSTANCE.L("비밀번호 재설정 response", "본인인증으로 진행 여부:" + checkedFindPwdByVerifyingPhone + "   RESULT MSG:" + model.message);

                        ToastUtil.showMessage(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_successchangepwd));
                        listener.closeActivity();

                    } else {
                        String message = ((BaseModel) o).message != null && !((BaseModel) o).message.isEmpty() ? ((BaseModel) o).message : BaseApplication.getInstance().getString(R.string.common_message_servererror);
                        listener.showSnackBar(message);
                    }
                    listener.hideLoadingIndicator();
                    user.addObserver(this);
                };

                if (checkedFindPwdByVerifyingPhone) {
                    // 본인인증으로 비밀번호 재설정
                    verification.setVerificationNumber(verifyNumber);
                    verification.setVerificationTarget(user.getMobile());
                    verification.setVerificationTargetType(Verification.IdentityVerifyMethod.MOBILE.getCode());
                    UserServer.changePasswordByIdentifying(serverListener, verification);

//                    JsonObject jsonObject = new JsonObject();
//                    jsonObject.addProperty("diCode", di);
//
//                    UserServer.getIdentityVerify((success, o) -> {
//                        BaseModel<Object> model = (BaseModel<Object>) o;
//                        if (success && model.resultCode == Flag.ResultCode.SUCCESS) {
//                            UserServer.changePasswordByIdentifying(serverListener, verification);
//                        } else {
//                            ToastUtil.showMessage(model.message);
//                        }
//                    }, jsonObject);
                } else {
                    if (checkedFindPwdByEmail) {
                        verification.setVerificationNumber(verifyNumber);
                        verification.setVerificationTarget(user.getEmail());
                        verification.setIdentityVerifyMethod(Verification.IdentityVerifyMethod.EMAIL.getCode());
                        verification.setVerificationTargetType(Verification.IdentityVerifyMethod.EMAIL.getCode());
                    } else {
                        verification.setVerificationNumber(verifyNumber);
                        verification.setVerificationTarget(user.getMobile());
                        verification.setIdentityVerifyMethod(Verification.IdentityVerifyMethod.MOBILE.getCode());
                        verification.setVerificationTargetType(Verification.IdentityVerifyMethod.MOBILE.getCode());
                    }

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
        if (CommonUtil.validateEmail(user.getEmail())) {
            user.deleteObserver(this);
            if (CountTimer.isResendable()) {
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

                            Object result = ((LinkedTreeMap) ((BaseModel) o).data).get("data");
                            int second;
                            if (result != null) second = (int) Math.round((Double) result);
                            else second = 180000;
                            int minute = second / 60000;

                            CountTimer.startVerifyNumberTimer("00", String.valueOf(minute), new OnTimerListener() {
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
        } else {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.findpwd_message_invalidemailformat));
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
