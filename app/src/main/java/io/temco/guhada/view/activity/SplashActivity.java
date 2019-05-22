package io.temco.guhada.view.activity;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.BrandData;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.data.server.ProductServer;
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
        getCategories();
        getAllBrands();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void getCategories() {
        ProductServer.getCategories((success, o) -> {
            if (success) {
                Preferences.setCategories((List<CategoryData>) o);
            }
        });
    }

    private void getAllBrands() {
        ProductServer.getAllBrands((success, o) -> {
            if (success) {
                Preferences.setBrands((List<BrandData>) o);
            }
        });
    }

    ////////////////////////////////////////////////
}
