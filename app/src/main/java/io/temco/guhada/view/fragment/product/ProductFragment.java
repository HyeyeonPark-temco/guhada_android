package io.temco.guhada.view.fragment.product;

import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.databinding.FragmentProductBinding;
import io.temco.guhada.view.adapter.ProductListPagerAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductFragment extends BaseFragment<FragmentProductBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private final String TAG_PRODUCT_LIST = "product_list";
    private OnDrawerLayoutListener mDrawerListener;
    private OnBackPressListener mBackListener;
    private ProductListPagerAdapter mListPagerAdapter;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return ProductFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product;
    }

    @Override
    protected void init() {
        // Header
        mBinding.layoutHeader.setClickListener(this);

        initListPager();
        addList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Top Menu
            case R.id.image_prev:
                checkBackEvent();
                break;

            case R.id.image_side_menu:
                if (mDrawerListener != null) mDrawerListener.onDrawerEvnet(true);
                break;

            case R.id.image_search:
                addList();
                break;

            case R.id.image_shop_cart:
                if (mDrawerListener != null) mDrawerListener.onDrawerEvnet(false);
                break;
        }
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

    public void addList() {
        if (mListPagerAdapter != null) {
            mListPagerAdapter.addFragment();
            mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount());
            mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount(), true);
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initListPager() {
        // Adapter
        if (mListPagerAdapter == null)
            mListPagerAdapter = new ProductListPagerAdapter(getChildFragmentManager());
        // Pager
        mBinding.layoutPager.setAdapter(mListPagerAdapter);
        mBinding.layoutPager.setSwipeLocked(true);
    }

    private void checkBackEvent() {
        if (mBackListener != null) {
            if (mListPagerAdapter != null && mListPagerAdapter.getCount() > 1) {
                mListPagerAdapter.removeFragment();
                mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount() - 1, true);
                mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount() - 1);
            } else {
                mListPagerAdapter = null;
                mBackListener.onPress();
            }
        }
    }

    ////////////////////////////////////////////////
}
