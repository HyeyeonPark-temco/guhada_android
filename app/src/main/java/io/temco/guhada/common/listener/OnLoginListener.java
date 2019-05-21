package io.temco.guhada.common.listener;

public interface OnLoginListener extends BaseActivityListener {

    void onGoogleLogin();

    void onKakaoLogin();

    void onFacebookLogin();

    void onNaverLogin();

    void redirectJoinActivity();

    void showSnackBar(String message);

    void redirectFindAccountActivity();
}
