package io.temco.guhada.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.server.ProductServer;
import io.temco.guhada.databinding.ActivitySplashBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class SplashActivity extends BindActivity<ActivitySplashBinding> {

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return SplashActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.SPLASH;
    }

    @Override
    protected void init() {
        if(CustomLog.INSTANCE.getFlag()) CustomLog.INSTANCE.L(this.getClass().getSimpleName(),"init");
        BaseApplication.getInstance().setMoveToMain(null);
        getCategories();
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void getCategories() {
        ProductServer.getCategories((success, o) -> {
            if (success) {
                Preferences.setCategories((List<Category>) o);
            }
            getAllBrands();
        });
    }

    private void getAllBrands() {
        ProductServer.getAllBrands((success, o) -> {
            if (success) {
                Preferences.setBrands((List<Brand>) o);
            }
            check();
        });
    }

    private void check() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }, 1000);
    }

    ////////////////////////////////////////////////
}