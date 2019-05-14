package io.temco.guhada.view.activity;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.databinding.ActivitySplashBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class SplashActivity extends BindActivity<ActivitySplashBinding> {

    // -------- LOCAL VALUE --------
    // -----------------------------

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

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}
