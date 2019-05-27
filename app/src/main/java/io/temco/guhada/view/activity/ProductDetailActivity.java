package io.temco.guhada.view.activity;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.databinding.ActivityProductDetailBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class ProductDetailActivity extends BindActivity<ActivityProductDetailBinding> {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return ProductDetailActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.PRODUCT_DETAIL;
    }

    @Override
    protected void init() {
        if (getIntent() != null) {
            // int id = getIntent().getIntExtra(Info.INTENT_DEAL_ID, 0); // Deal ID
            int id = 10345;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}
