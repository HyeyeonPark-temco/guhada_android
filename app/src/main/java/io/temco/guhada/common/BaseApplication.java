package io.temco.guhada.common;


import androidx.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.auth.KakaoSDK;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


import io.temco.guhada.R;
import io.temco.guhada.common.sns.kakao.KakaoSDKAdapter;
import io.temco.guhada.common.util.CommonUtil;

/*
    MultiDexApplication 변경
 */
public class BaseApplication extends MultiDexApplication {

    private static BaseApplication mApplication;

    /**
     * @author park jungho
     * 메인으로 이동하기 위한 값 추가
     */
    private ActivityMoveToMain moveToMain = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        moveToMain = new ActivityMoveToMain(0,false);
        getFCMToken();

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

    @Override
    public void onTerminate() {
        super.onTerminate();
        moveToMain = null;
        mApplication = null;
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }

    private void getFCMToken() {
        String FCM_TAG = "FCM_TOKEN";
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        CommonUtil.debug(FCM_TAG, task.getException() != null ? task.getException().getMessage() : "getInstanceId() failed");
                        return;
                    }
                    CommonUtil.debug(FCM_TAG, task.getResult() != null ? task.getResult().getToken() : "Token result is null");
                });
    }

    public ActivityMoveToMain getMoveToMain() {
        return moveToMain;
    }

    public void setMoveToMain(ActivityMoveToMain moveToMain) {
        this.moveToMain = moveToMain;
    }
}