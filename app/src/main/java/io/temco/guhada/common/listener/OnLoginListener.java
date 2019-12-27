package io.temco.guhada.common.listener;

import io.temco.guhada.common.listener.base.OnBaseActivityListener;

public interface OnLoginListener extends OnBaseActivityListener {

    void onGoogleLogin();

    void onKakaoLogin();

    void onFacebookLogin();

    void onNaverLogin();

    void redirectJoinActivity();

    void showSnackBar(String message);

    void redirectFindAccountActivity();

    void redirectLuckyDrawJoinActivity();

    void closeActivityAfterLogin(int resultCode, boolean firstAppLogin);
}
