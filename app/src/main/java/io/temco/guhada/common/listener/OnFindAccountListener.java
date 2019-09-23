package io.temco.guhada.common.listener;

import io.temco.guhada.common.listener.base.OnBaseActivityListener;

public interface OnFindAccountListener extends OnBaseActivityListener {
    void showSnackBar(String message);

    void hideKeyboard();

    void redirectSignUpActivity();

    void redirectSignInActivity();

    void copyToClipboard(String text);

    void redirectVerifyPhoneActivity();

    void showLoadingIndicator();

    void hideLoadingIndicator();

    void onSuccessGetIdentifyVerify();
}
