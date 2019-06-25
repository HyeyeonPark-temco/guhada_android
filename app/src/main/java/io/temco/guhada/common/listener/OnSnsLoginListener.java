package io.temco.guhada.common.listener;

import com.kakao.usermgmt.response.model.UserProfile;

import io.temco.guhada.data.model.Token;

public interface OnSnsLoginListener {
    void redirectTermsActivity(int type, Object data);
    void redirectMainActivity(Token data);

    void kakaoLogin(UserProfile result);
    void showMessage(String message);
}
