package io.temco.guhada.common.sns.kakao;

import android.widget.Toast;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.listener.OnSnsLoginListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.base.BaseModel;
import io.temco.guhada.data.server.LoginServer;

public class KakaoSessionCallback implements ISessionCallback {
    private OnSnsLoginListener mListener;

    public KakaoSessionCallback(OnSnsLoginListener mListener) {
        this.mListener = mListener;
    }

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

                LoginServer.checkExistSnsUser((success, o) -> {
                    if (success) {
                        BaseModel model = (BaseModel) o;
                        if (model.resultCode == Flag.ResultCode.SUCCESS) {
                            mListener.kakaoLogin(result);
                        } else {
                            mListener.redirectTermsActivity(Flag.RequestCode.KAKAO_LOGIN, result);
                        }
                    } else {
                        String message = (String) o;
                        Toast.makeText(BaseApplication.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }, "KAKAO", String.valueOf(result.getId()));
            }
        });
    }

    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        CommonUtil.debug(exception.getMessage());
    }


}
