package io.temco.guhada.view.fragment.product;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.databinding.FragmentProductDetailBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductDetailFragment extends BaseFragment<FragmentProductDetailBinding> {

    // -------- LOCAL VALUE --------
    private OnDrawerLayoutListener mDrawerListener;
    private OnBackPressListener mBackListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return ProductDetailFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_detail;
    }

    @Override
    protected void init() {

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnDrawerLayoutListener(OnDrawerLayoutListener listener) {
        mDrawerListener = listener;
    }

    public void setOnBackPressListener(OnBackPressListener listener) {
        mBackListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}