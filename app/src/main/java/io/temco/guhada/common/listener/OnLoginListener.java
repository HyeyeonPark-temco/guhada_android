package io.temco.guhada.common.listener;

public interface OnLoginListener {

    void onGoogleLogin();

    void onKakaoLogin();

    void onFacebookLogin();

    void onNaverLogin();

    void redirectJoinActivity();

    void showMessage(String message);
}
