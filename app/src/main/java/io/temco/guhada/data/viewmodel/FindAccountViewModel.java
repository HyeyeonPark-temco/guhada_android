package io.temco.guhada.data.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import java.util.Objects;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

public class FindAccountViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "아이디/비밀번호 찾기";
    private OnFindAccountListener findAccountListener;
    private boolean checkedFindIdByInfo = false;
    private boolean checkedFindIdByVerifyingPhone = false;
    private int resultVisibility = View.GONE;

    public ObservableField<User> mUser = new ObservableField<User>(new User());

    // FIND ID BY VERIFYING PHONE
    private boolean checkedAllTerms = false;
    private int checkedTermsCount = 0;
    private boolean[] checkedTerms = {false, false, false, false, false};

    // VERIFY NUMBER
    private int verifyNumberVisibility = View.GONE;
    private String verifyNumber = "";
    private String timerMinute = "02";
    private String timerSecond = "60";

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
    public ObservableField<User> getMUser() {
        return mUser;
    }

    public void setmUser(ObservableField<User> mUser) {
        this.mUser = mUser;
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
        findAccountListener.closeActivity();
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
        if (checked) {
            if (checkedFindIdByInfo) {
                checkedFindIdByInfo = false;
                notifyPropertyChanged(BR.checkedFindIdByInfo);
            }
            findAccountListener.redirectVerifyPhoneActivity();
        }

        checkedFindIdByVerifyingPhone = checked;
        notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
    }

    public void onClickGender(View view) {
        Objects.requireNonNull(mUser.get()).setGender(Integer.parseInt((String) view.getTag()));
        notifyPropertyChanged(BR.mUser);
    }

    public void onClickSignUp() {
        findAccountListener.redirectSignUpActivity();
    }

    public void onClickSignIn() {
        findAccountListener.closeActivity();
        //  findAccountListener.redirectSignInActivity();
    }

    public void onClickSendId() {

    }

    public void onClickCopyId() {
        findAccountListener.copyToClipboard(Objects.requireNonNull(mUser.get()).getEmail());
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
     * [아이디찾기] 회원정보
     */
    public void onClickFindId() {
        LoginServer.findUserId((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case Flag.ResultCode.SUCCESS:
                        User user = (User) model.data;

                        Objects.requireNonNull(mUser.get()).setPhoneNumber(user.getPhoneNumber());
                        Objects.requireNonNull(mUser.get()).setEmail(user.getEmail());
                        Objects.requireNonNull(mUser.get()).setJoinAt(user.getJoinAt());
                        setResultVisibility(View.VISIBLE);

                        notifyPropertyChanged(BR.mUser);
                        notifyPropertyChanged(BR.resultVisibility);

                        findAccountListener.hideKeyboard();
                        return;
                    case Flag.ResultCode.DATA_NOT_FOUND:
                        String message = BaseApplication.getInstance().getResources().getString(R.string.findid_message_wronginfo);
                        findAccountListener.showSnackBar(message);
                }
            } else {
                findAccountListener.showMessage((String) o);
            }
        }, Objects.requireNonNull(mUser.get()).getName(), Objects.requireNonNull(mUser.get()).getPhoneNumber());
    }

}
