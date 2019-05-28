package io.temco.guhada.common.listener;

public interface OnFindPasswordListener {
    void showSnackBar(String message);

    void hideKeyboard();

    void startTimer(String minute, String second);

    void showMessage(String string);

    void closeActivity();

    void redirectVerifyPhoneActivity();

    void setVerifyNumberViewEmpty();
}
