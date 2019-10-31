package io.temco.guhada.data.viewmodel.account;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnTermsListener;
import io.temco.guhada.data.model.user.User;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class TermsViewModel extends BaseObservableViewModel implements Observer {
    private OnTermsListener listener;
    private String toolBarTitle = BaseApplication.getInstance().getResources().getString(R.string.terms_title);
    private User user = new User();

    private ObservableBoolean allChecked = new ObservableBoolean(false);
    private ObservableBoolean essentialChecked = new ObservableBoolean(false);
    private ObservableBoolean optionalChecked = new ObservableBoolean(false);

    public TermsViewModel(OnTermsListener listener) {
        this.listener = listener;
        this.user.addObserver(this);
    }

    // GETTER & SETTER
    public String getToolBarTitle() {
        return toolBarTitle;
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

    @Bindable
    public ObservableBoolean getEssentialChecked() {
        return essentialChecked;
    }

    @Bindable
    public ObservableBoolean getOptionalChecked() {
        return optionalChecked;
    }

    // CHECK LISTENER
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

    // CLICK LISTENER
    public void onClickSignUp() {
     listener.closeActivity(RESULT_OK);
    }

    public void onClickBack() {
        listener.closeActivity(RESULT_CANCELED);
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
