package io.temco.guhada.common.sns.kakao;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.IPushConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;

import io.temco.guhada.common.BaseApplication;

public class KakaoSDKAdapter extends KakaoAdapter {
    public KakaoSDKAdapter() {
        super();
    }

    @Override
    public ISessionConfig getSessionConfig() {
        return new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return true;
            }
        };
    }

    @Override
    public IPushConfig getPushConfig() {
        return super.getPushConfig();
    }

    @Override
    public IApplicationConfig getApplicationConfig() {
        return () -> BaseApplication.getInstance().getApplicationContext();
    }
}
