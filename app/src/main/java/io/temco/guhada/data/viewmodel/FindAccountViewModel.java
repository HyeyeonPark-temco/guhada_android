package io.temco.guhada.data.viewmodel;

import android.view.View;

import androidx.databinding.Bindable;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.BaseObservableViewModel;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.data.model.Token;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;

public class FindAccountViewModel extends BaseObservableViewModel {
    public String toolBarTitle = "아이디/비밀번호 찾기";
    private OnFindAccountListener findAccountListener;
    private boolean checkedFindIdByInfo = false;
    private boolean checkedFindIdByVerifyingPhone = false;
    private int resultVisibility = View.GONE;
    private String name = "", phoneNumber = "", email = "", joinAt = "";

    // FIND ID BY VERIFYING PHONE
    private boolean isForeigner = false;
    private final int MALE = 1;
    private final int FEMALE = 2;
    private int gender = -1;

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

    public void setForeigner(boolean foreigner) {
        isForeigner = foreigner;
    }

    @Bindable
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Bindable
    public int getResultVisibility() {
        return resultVisibility;
    }

    public void setResultVisibility(int resultVisibility) {
        this.resultVisibility = resultVisibility;
    }

    @Bindable
    public String getJoinAt() {
        return "(" + joinAt + " 가입)";
    }

    public void setJoinAt(String joinAt) {
        this.joinAt = joinAt;
    }

    public void setFindAccountListener(OnFindAccountListener findAccountListener) {
        this.findAccountListener = findAccountListener;
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
        if (checked && checkedFindIdByInfo) {
            checkedFindIdByInfo = false;
            notifyPropertyChanged(BR.checkedFindIdByInfo);
        }
        checkedFindIdByVerifyingPhone = checked;
        notifyPropertyChanged(BR.checkedFindIdByVerifyingPhone);
    }

    public void onClickGender(View view) {
        gender = Integer.parseInt((String) view.getTag());
        notifyPropertyChanged(BR.gender);
    }

    public void onClickSignUp(){
        findAccountListener.redirectSignUpActivity();
    }

    public void onClickSignIn(){
        findAccountListener.closeActivity();
      //  findAccountListener.redirectSignInActivity();
    }

    public void onClickSendId(){

    }

    public void onClickCopyId(){
        findAccountListener.copyToClipboard(email);
    }

    /**
     * [아이디찾기] 회원정보
     */
    public void onClickFindId() {
        LoginServer.findUserId((success, o) -> {
            if (success) {
                BaseModel model = (BaseModel) o;
                switch (model.resultCode) {
                    case 200:
                        User user = (User) model.data;
                        phoneNumber = user.getPhoneNumber();
                        joinAt = user.getJoinAt();
                        email = user.getEmail();
                        notifyPropertyChanged(BR.phoneNumber);
                        notifyPropertyChanged(BR.email);
                        notifyPropertyChanged(BR.joinAt);
                        setResultVisibility(View.VISIBLE);
                        notifyPropertyChanged(BR.resultVisibility);
                        findAccountListener.hideKeyboard();
                        return;
                    case 5004: // DATA NOT FOUND
                        String message = BaseApplication.getInstance().getResources().getString(R.string.join_wrongaccount);
                        findAccountListener.showSnackBar(message);
                }
            } else {
                findAccountListener.showMessage((String) o);
            }
        }, name, phoneNumber);
    }
}
