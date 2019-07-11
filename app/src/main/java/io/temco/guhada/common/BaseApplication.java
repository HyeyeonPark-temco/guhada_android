package io.temco.guhada.common;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.kakao.auth.KakaoSDK;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import io.temco.guhada.R;
import io.temco.guhada.common.sns.kakao.KakaoSDKAdapter;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // Preference
        Preferences.init(getApplicationContext());

        // APP CENTER
        AppCenter.start(this, getResources().getString(R.string.appcenter_dev_secret), Analytics.class, Crashes.class);

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