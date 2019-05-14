package io.temco.guhada.common;

import android.app.Application;

public class BaseApplication extends Application {

    private static BaseApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // Preference
        Preferences.init(getApplicationContext());
    }

    public static BaseApplication getInstance() {
        return mApplication;
    }
}
