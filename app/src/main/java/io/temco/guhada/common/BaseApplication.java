package io.temco.guhada.common;

import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // Preference
        Preferences.init(getApplicationContext());

        // KAKAO
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }
}
