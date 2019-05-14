package io.temco.guhada.view.activity;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.databinding.ActivityMainBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class MainActivity extends BindActivity<ActivityMainBinding> {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return MainActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.MAIN;
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
