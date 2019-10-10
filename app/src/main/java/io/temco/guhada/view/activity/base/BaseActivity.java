package io.temco.guhada.view.activity.base;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CustomLog;

public abstract class BaseActivity extends AppCompatActivity {

    private Tracker mTracker;
    private FirebaseAnalytics mFirebaseAnalytics;

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        ActivityManager.getInstance().addActivity(getViewType(), this);

        try{
            // Obtain the shared Tracker instance.
            BaseApplication application = (BaseApplication) getApplication();
            mTracker = application.getDefaultTracker();

            // Obtain the FirebaseAnalytics instance.
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }catch (Exception e){
            if(CustomLog.getFlag())CustomLog.E(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().removeActivity(this);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    protected abstract String getBaseTag();

    protected abstract int getLayoutId();

    protected abstract Type.View getViewType();

    protected abstract void init();

    public Tracker getmTracker() {
        return mTracker;
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    ////////////////////////////////////////////////
}
