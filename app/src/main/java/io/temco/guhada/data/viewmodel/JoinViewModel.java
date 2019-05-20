package io.temco.guhada.data.viewmodel;

import android.text.TextUtils;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.common.BaseObservableViewModel;
import io.temco.guhada.common.listener.OnJoinListener;
import io.temco.guhada.data.model.User;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;

public class JoinViewModel extends BaseObservableViewModel implements Observer {
    private String toolBarTitle = "";
    private User user = new User();
    private OnJoinListener listener;

    // TERMS CHECKED
    private ObservableBoolean allChecked = new ObservableBoolean(false);
    private ObservableBoolean essentialChecked = new ObservableBoolean(false);
    private ObservableBoolean optionalChecked = new ObservableBoolean(false);


    public JoinViewModel(OnJoinListener listener) {
        this.listener = listener;
        user.addObserver(this);
    }

    // GETTER & SETTER
    @Bindable
    public String getToolBarTitle() {
        return toolBarTitle;
    }

    public void setToolBarTitle(String toolBarTitle) {
        this.toolBarTitle = toolBarTitle;
    }

    @Bindable
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Bindable
    public ObservableBoolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(ObservableBoolean allChecked) {
        this.allChecked = allChecked;
    }

    @Bindable
    public ObservableBoolean getEssentialChecked() {
        return essentialChecked;
    }

    public void setEssentialChecked(ObservableBoolean essentialChecked) {
        this.essentialChecked = essentialChecked;
    }

    @Bindable
    public ObservableBoolean getOptionalChecked() {
        return optionalChecked;
    }

    public void setOptionalChecked(ObservableBoolean optionalChecked) {
        this.optionalChecked = optionalChecked;
    }

    // CLICK LISTENER
    public void onClickBack() {
        listener.closeActivity();
    }

    public void onClickSignUp() {
        if (!isValidEmail(user.getEmail())) {
            listener.showMessage("이메일 형식이 올바르지 않습니다.");
        } else if (!user.getConfirmPassword().equals(user.getPassword())) {
            listener.showMessage("비밀번호가 일치하지 않습니다.");
        } else if (!essentialChecked.get()) {
            listener.showMessage("필수약관 동의가 필요합니다.");
        } else {
            LoginServer.signUp((success, o) -> {
                if (success) {
                    BaseModel model = ((BaseModel) o);
                    switch (model.resultCode) {
                        case 200:
                            listener.showMessage((String) model.data);
                            listener.closeActivity();
                            return;
                        case 6001:
                        case 6002:
                            listener.showMessage(model.message);
                    }
                } else {
                    listener.showMessage((String) o);
                }
            }, user);
        }
    }

    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    // TERMS CHECK LISTENER
    public void onCheckAll(boolean checked) {
        if (checked) {
            user.setAgreePurchaseTos(true);
            user.setAgreeCollectPersonalInfoTos(true);
            user.setAgreeSaleTos(true);
            user.setAgreeEmailReception(true);
            user.setAgreeSmsReception(true);
        } else {
            if (essentialChecked.get() && optionalChecked.get()) {
                user.setAgreePurchaseTos(false);
                user.setAgreeCollectPersonalInfoTos(false);
                user.setAgreeSaleTos(false);
                user.setAgreeEmailReception(false);
                user.setAgreeSmsReception(false);
            }
        }
    }

    public void onCheckEssential(boolean checked) {
        if (checked) {
            essentialChecked = new ObservableBoolean(true);
            user.setAgreePurchaseTos(true);
            user.setAgreeCollectPersonalInfoTos(true);
            notifyPropertyChanged(BR.essentialChecked);
        } else {
            if (user.getAgreePurchaseTos() && user.getAgreeCollectPersonalInfoTos()) {
                essentialChecked = new ObservableBoolean(true);
                user.setAgreePurchaseTos(false);
                user.setAgreeCollectPersonalInfoTos(false);
                notifyPropertyChanged(BR.essentialChecked);
            } else {
                essentialChecked = new ObservableBoolean(false);
            }
        }
    }

    public void onCheckOption(boolean checked) {
        if (checked) {
            optionalChecked = new ObservableBoolean(true);
            user.setAgreeSaleTos(true);
            user.setAgreeEmailReception(true);
            user.setAgreeSmsReception(true);
            notifyPropertyChanged(BR.optionalChecked);
        } else {
            if (user.getAgreeSaleTos() && user.getAgreeEmailReception() && user.getAgreeSmsReception()) {
                optionalChecked = new ObservableBoolean(false);
                user.setAgreeSaleTos(false);
                user.setAgreeEmailReception(false);
                user.setAgreeSmsReception(false);
                notifyPropertyChanged(BR.optionalChecked);
            } else {
                optionalChecked = new ObservableBoolean(false);
            }

        }
    }

    public void onCheckPurchaseTos(boolean checked) {
        user.setAgreePurchaseTos(checked);
    }

    public void onCheckPrivacyTos(boolean checked) {
        user.setAgreeCollectPersonalInfoTos(checked);
    }

    public void onCheckSaleTos(boolean checked) {
        user.setAgreeSaleTos(checked);
    }

    public void onCheckEmailReception(boolean checked) {
        user.setAgreeEmailReception(checked);
    }

    public void onCheckSmsReception(boolean checked) {
        user.setAgreeSmsReception(checked);
    }

    // INSTEAD OF TWO WAY BINDING
    public void setEmail(String email) {
        user.setEmail(email);
    }

    public void setPassword(String password) {
        user.setPassword(password);
    }

    public void setConfirmPassword(String confirmPassword) {
        user.setConfirmPassword(confirmPassword);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            switch ((String) arg) {
                // ESSENTIAL TERMS
                case "agreeCollectPersonalInfoTos":
                case "agreePurchaseTos":
                    boolean personalInfoTosChecked = user.getAgreeCollectPersonalInfoTos();
                    boolean purchaseTosChecked = user.getAgreePurchaseTos();
                    boolean isEssentialAllChecked = personalInfoTosChecked && purchaseTosChecked;
                    if (essentialChecked.get() != isEssentialAllChecked) {
                        essentialChecked = new ObservableBoolean(isEssentialAllChecked);
                        notifyPropertyChanged(BR.essentialChecked);
                    }
                    break;
                case "agreeSaleTos":
                case "agreeEmailReception":
                case "agreeSmsReception":
                    boolean saleTosChecked = user.getAgreeSaleTos();
                    boolean emailReceptionChecked = user.getAgreeEmailReception();
                    boolean smsReceptionChecked = user.getAgreeSmsReception();
                    boolean isOptionAllChecked = saleTosChecked && emailReceptionChecked && smsReceptionChecked;

                    if (optionalChecked.get() != isOptionAllChecked) {
                        optionalChecked = new ObservableBoolean(isOptionAllChecked);
                        notifyPropertyChanged(BR.optionalChecked);
                    }
                    break;
            }
        }
    }
}
