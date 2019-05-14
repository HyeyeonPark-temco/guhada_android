package io.temco.guhada.view.activity.base;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import io.temco.guhada.common.Type;

public abstract class BaseActivity extends AppCompatActivity {

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

    ////////////////////////////////////////////////
}
