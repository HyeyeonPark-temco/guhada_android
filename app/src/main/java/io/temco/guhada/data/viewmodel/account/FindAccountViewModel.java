package io.temco.guhada.data.viewmodel.account;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.user.User;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FindAccountViewModel extends BaseObservableViewModel implements Observer {
    public String toolBarTitle = "아이디/비밀번호 찾기";
    private OnFindAccountListener findAccountListener;
    private boolean checkedFindIdByInfo = false;
    private boolean checkedFindIdByVerifyingPhone = false;
    private int resultVisibility = View.GONE;

    public User user = new User();
    public String di = "";
    public ObservableField<User> mUser = new ObservableField<User>(new User()); // 삭ㅈㅔ 예정

    // FIND ID BY VERIFYING PHONE
    private boolean checkedAllTerms = false;
    private int checkedTermsCount = 0;
    private boolean[] checkedTerms = {false, false, false, false, false};

    // VERIFY NUMBER
    private int verifyNumberVisibility = View.GONE;
    private String verifyNumber = "";
    private String timerMinute = "02";
    private String timerSecond = "60";

    public FindAccountViewModel() {
        this.user.addObserver(this);
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
    public boolean isCheckedFindIdByInfo() {
        return checkedFindIdByInfo;
    }

    public void setCheckedFindIdByInfo(boolean checked) {
        this.checkedFindIdByInfo = checked;
    }

    @Bindable
    public boolean isCheckedFindIdByVerifyingPhone() {
        return checkedFindIdByVerifyingPhone;
    }

    public void setCheckedFindIdByVerifyingPhone(boolean checkedFindIdByVerifyingPhone) {
        this.checkedFindIdByVerifyingPhone = checkedFindIdByVerifyingPhone;
    }

    @Bindable
    public int getResultVisibility() {
        return resultVisibility;
    }

    public void setResultVisibility(int resultVisibility) {
        this.resultVisibility = resultVisibility;
    }

    public void setFindAccountListener(OnFindAccountListener findAccountListener) {
        this.findAccountListener = findAccountListener;
    }

    @Bindable
    public boolean isCheckedAllTerms() {
        return checkedAllTerms;
    }

    public void setCheckedAllTerms(boolean checkedAllTerms) {
        this.checkedAllTerms = checkedAllTerms;
    }

    @Bindable
    public int getCheckedTermsCount() {
        return checkedTermsCount;
    }

    public void setCheckedTermsCount(int checkedTermsCount) {
        this.checkedTermsCount = checkedTermsCount;
    }

    @Bindable
    public boolean[] getCheckedTerms() {
        return checkedTerms;
    }

    public void setCheckedTerms(boolean[] checkedTerms) {
        this.checkedTerms = checkedTerms;
    }

    @Bindable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Bindable
    public int getVerifyNumberVisibility() {
        return verifyNumberVisibility;
    }

    @Bindable
    public String getVerifyNumber() {
        return verifyNumber;
    }

    public void setVerifyNumber(String verifyNumber) {
        this.verifyNumber = verifyNumber;
    }

    // LISTENER
    public void onClickBack() {
        findAccountListener.closeActivity(RESULT_CANCELED);
    }

    public void onCheckedFindIdByInfo(boolean checked) {
        if (checked && checkedFindIdByVerifyingPhone) {
            checkedFindIdByVerifyingPhone = false;
            notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
        }
        checkedFindIdByInfo = checked;
        notifyPropertyChanged(BR.checkedFindIdByInfo);
    }

    public void onCheckedFindIdByVerifyingPhone(boolean checked) {
        if (checked && checkedFindIdByInfo) {
            checkedFindIdByInfo = false;
            notifyPropertyChanged(BR.checkedFindIdByInfo);
        }

        checkedFindIdByVerifyingPhone = checked;
        notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
    }

    /**
     * 본인인증으로 아이디 찾기 클릭
     */
    public void onClickVerifyPhone() {
        findAccountListener.redirectVerifyPhoneActivity();
    }

    public void onClickGender(View view) {
        Objects.requireNonNull(user).setGender(Integer.parseInt((String) view.getTag()));
        // notifyPropertyChanged(BR.mUser);
    }

    public void onClickSignUp() {
        findAccountListener.redirectSignUpActivity();
    }

    public void onClickSignIn() {
        findAccountListener.closeActivity(RESULT_OK);
        findAccountListener.redirectSignInActivity();
    }

    public void onClickSendId() {

    }

    public void onClickCopyId() {
        findAccountListener.copyToClipboard(Objects.requireNonNull(user).getEmail());
    }

    public void onCheckedTerms(View view, boolean checked) {
        String tag = (String) view.getTag();
        checkedTerms[Integer.parseInt(tag)] = checked;
        notifyPropertyChanged(BR.checkedTerms);

        checkedTermsCount = checked ? checkedTermsCount + 1 : checkedTermsCount - 1;
        checkedAllTerms = checked && checkedTermsCount == 5;
        notifyPropertyChanged(BR.checkedAllTerms);
    }

    public void onCheckedAllTerms(boolean checked) {
        if (checkedAllTerms != checked) {
            for (int i = 0; i < checkedTerms.length; i++) {
                checkedTerms[i] = checked;
            }
            notifyPropertyChanged(BR.checkedTerms);
        }
    }

    public void onClickRequestVerifyNumber() {
        verifyNumberVisibility = View.VISIBLE;
        notifyPropertyChanged(BR.verifyNumberVisibility);
//        findAccountListener.startTimer();
    }

    /**
     * 내 회원정보로 아이디 찾기 클릭
     * [아이디찾기] 회원정보
     */
    public void onClickFindId() {
        findAccountListener.showLoadingIndicator();
        user.deleteObserver(this);
        UserServer.findUserId((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        User user = (User) model.data;
                        this.user = user;
                        Objects.requireNonNull(this.user).setPhoneNumber(user.getPhoneNumber());
                        Objects.requireNonNull(this.user).setEmail(user.getEmail());
                        Objects.requireNonNull(this.user).setJoinAt(user.getJoinAt());
                        setResultVisibility(View.VISIBLE);

                        notifyPropertyChanged(BR.user);
                        notifyPropertyChanged(BR.resultVisibility);

                        findAccountListener.hideKeyboard();
                        findAccountListener.hideLoadingIndicator();
                        return;
                    case Flag.ResultCode.DATA_NOT_FOUND:
                        String message = BaseApplication.getInstance().getResources().getString(R.string.findid_message_wronginfo);
                        findAccountListener.showSnackBar(message);
                }
            } else {
                findAccountListener.showMessage((String) o);
            }
            findAccountListener.hideLoadingIndicator();
        }, Objects.requireNonNull(this.user).getName(), Objects.requireNonNull(this.user).getPhoneNumber(), "");
    }

    /**
     * 본인인증 데이터 여부 조회
     * 본인인증으로 아이디 찾기(검증)
     *
     * @param di
     * @author Hyeyeon Park
     * @since 2019.09.23
     */
    public void getIdentityVerify(String di) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("diCode", di);

        UserServer.getIdentityVerify((success, o) -> {
            BaseModel model = (BaseModel) o;
            if (success) {
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        findAccountListener.onSuccessGetIdentifyVerify();
                        return;
                    case Flag.ResultCode.DATA_NOT_FOUND:
                        String message = BaseApplication.getInstance().getResources().getString(R.string.findid_message_wronginfo);
                        findAccountListener.showSnackBar(message);
                        return;
                    default:
                        findAccountListener.showMessage(model.message);
                }
            } else {
                findAccountListener.showMessage(model.message);
            }
            findAccountListener.hideLoadingIndicator();
        }, jsonObject);
    }

    /**
     * 유저 정보 조회
     *
     * @param name
     * @param phoneNumber
     * @author Hyeyeon Park
     * @since 2019.09.23
     */
    public void getUser(String name, String phoneNumber) {
        UserServer.findUserId((success, o) -> {
            BaseModel model = (BaseModel) o;
            if (success) {
                if (model.resultCode == Flag.ResultCode.SUCCESS) {
                    User user = (User) model.data;
                    user.setPhoneNumber(phoneNumber);
                    user.setMobile(phoneNumber);
                    this.user = user;

                    setResultVisibility(View.VISIBLE);
                    notifyPropertyChanged(BR.resultVisibility);
                    notifyPropertyChanged(BR.user);
                } else {
                    findAccountListener.showSnackBar(model.message);
                }
            } else {
                findAccountListener.showSnackBar(model.message);
            }
        }, name, phoneNumber, "");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            switch ((String) arg) {
                case "name":
                case "phoneNumber":
                    notifyPropertyChanged(BR.user);
                    break;
            }
        }
    }
}
