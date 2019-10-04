package io.temco.guhada.data.viewmodel.account;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableBoolean;

import java.util.Observable;
import java.util.Observer;

import io.temco.guhada.BR;
import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnJoinListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.model.user.User;
import io.temco.guhada.data.server.UserServer;
import io.temco.guhada.data.viewmodel.base.BaseObservableViewModel;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

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

    public void setEssentialChecked(boolean flag) {
        this.essentialChecked.set(flag);
        notifyPropertyChanged(BR.essentialChecked);
    }

    @Bindable
    public ObservableBoolean getOptionalChecked() {
        return optionalChecked;
    }


    // CLICK LISTENER
    public void onClickBack() {
        listener.closeActivity(RESULT_CANCELED);
    }

    public void onClickSignUp() {
        if (!CommonUtil.validateEmail(user.getEmail())) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_wrongidformat));
        } else if (user.getPassword().length() < 8 || user.getPassword().length() > 15) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_wrondpwdlength));
        } else if (!CommonUtil.validatePassword(user.getPassword())) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_wrongpwdformat));
        } else if (!user.getConfirmPassword().equals(user.getPassword())) {
            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_notequalpwd));
        } else {
            user.deleteObserver(this);
            UserServer.signUp((success, o) -> {
                if (success) {
                    BaseModel model = ((BaseModel) o);
                    switch (model.resultCode) {
                        case 200:
                            String email = model.data instanceof String ? (String) model.data : user.getEmail();
                            user.setEmail(email);
                            // listener.showMessage(email + " 가입 완료");
                            listener.closeActivity(RESULT_OK);
                            return;
                        case 6001: // ALREADY EXIST EMAIL
                            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_existemail));
                            break;
                        case 6002: // INVALID PASSWORD
                            listener.showSnackBar(BaseApplication.getInstance().getResources().getString(R.string.join_message_wrongpwdformat));
                            break;
                    }
                } else {
                    listener.showSnackBar((String) o);
                }
            }, user);
        }
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
                    if (CustomLog.INSTANCE.getFlag())
                        CustomLog.INSTANCE.L("update", "isEssentialAllChecked", isEssentialAllChecked);
                    if (CustomLog.INSTANCE.getFlag())
                        CustomLog.INSTANCE.L("update", "essentialChecked.get() ", essentialChecked.get());
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
