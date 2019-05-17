package io.temco.guhada.common;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import io.temco.guhada.common.util.CommonUtil;

public class KakaoSessionCallback implements ISessionCallback {
    @Override
    public void onSessionOpened() {
        UserManagement.getInstance().requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                CommonUtil.debug("[KAKAO] " + errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                CommonUtil.debug("[KAKAO] Not Signed Up");
            }

            @Override
            public void onSuccess(UserProfile result) {
                CommonUtil.debug("[KAKAO] " + result.getEmail());
            }
        });
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        CommonUtil.debug(exception.getMessage());
    }
}
