package io.temco.guhada.view.fragment.product;

import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.FragmentProductBinding;
import io.temco.guhada.view.adapter.ProductListPagerAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductFragment extends BaseFragment<FragmentProductBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDrawerLayoutListener mDrawerListener;
    private OnBackPressListener mBackListener;
    private FragmentManager mFragmentManager;
    private ProductListPagerAdapter mListPagerAdapter;
    //
    private boolean mIsCategory = true;
    private Category mCategoryData;
    private Brand mBrandData;
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
        // Manager
        mFragmentManager = getChildFragmentManager();

        // Header
        mBinding.layoutHeader.setClickListener(this);

        // List
        initList();
        if (mIsCategory) {
            addCategoryList();
        } else {
            addBrandList();
        }
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
                //
                break;

            case R.id.image_shop_cart:
                if (mDrawerListener != null) mDrawerListener.onDrawerEvnet(false);
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void changeProduct(boolean isCategory) {
        mIsCategory = isCategory;
    }

    public void setCategory(Category data) {
        mCategoryData = data;
        addCategoryList();
    }

    public void setBrand(Brand data) {
        mBrandData = data;
        addBrandList();
    }

    public void setOnDrawerLayoutListener(OnDrawerLayoutListener listener) {
        mDrawerListener = listener;
    }

    public void setOnBackPressListener(OnBackPressListener listener) {
        mBackListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initList() {
        if (mBinding != null) {
            // Adapter
            mListPagerAdapter = new ProductListPagerAdapter(mFragmentManager);
            mListPagerAdapter.setOnChangeTitleListener(this::setTitle);
            // Pager
            mBinding.layoutPager.setAdapter(mListPagerAdapter);
            mBinding.layoutPager.setSwipeLocked(true);
        }
    }

    private void addCategoryList() {
        if (mBinding != null && mCategoryData != null) {
            mListPagerAdapter.setOnAddCategoryListener(this::addCategoryChildFragment);
            setTitle(mCategoryData.name);
            addCategoryChildFragment(mCategoryData);
        }
    }

    private void addCategoryChildFragment(Category data) {
        mListPagerAdapter.addCategoryFragment(data);
        mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount());
        mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount(), true);
    }

    private void addBrandList() {
        if (mBinding != null && mBrandData != null) {
            setTitle(mBrandData.nameDefault);
            addBrandChildFragment(mBrandData);
        }
    }

    private void addBrandChildFragment(Brand data) {
        mListPagerAdapter.addBrandFragment(data);
        mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount());
        mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount(), true);
    }

//    private void addCategoryChildFragment(Category data) {
//        mListPagerAdapter.addFragment(data);
//        mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount());
//        mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount(), true);
//    }

    private void checkBackEvent() {
        if (mBackListener != null) {
            if (mListPagerAdapter != null && mListPagerAdapter.getCount() > 1) {
                mListPagerAdapter.removeFragment();
                mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount() - 1, true);
                mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount() - 1);
            } else {
                mBackListener.onPress();
            }
        }
    }

    private void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mBinding.layoutHeader.setTitle(title);
        }
    }

    ////////////////////////////////////////////////
}
