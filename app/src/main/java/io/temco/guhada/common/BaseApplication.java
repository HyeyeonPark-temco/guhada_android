package io.temco.guhada.common;


import androidx.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.KakaoSDK;

import io.temco.guhada.common.sns.kakao.KakaoSDKAdapter;

/*
    MultiDexApplication 변경
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // Preference
        Preferences.init(getApplicationContext());

        // KAKAO
        KakaoSDK.init(new KakaoSDKAdapter());

        // FACEBOOK
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }
}