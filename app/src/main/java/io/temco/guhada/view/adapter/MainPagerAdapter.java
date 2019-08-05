package io.temco.guhada.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.fragment.mypage.MyPageMainFragment;
import io.temco.guhada.view.fragment.product.ProductFragment;
import io.temco.guhada.view.fragment.main.CommunityFragment;
import io.temco.guhada.view.fragment.main.HomeFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    // -------- LOCAL VALUE --------
    private final String TAG = MainPagerAdapter.class.getSimpleName();
    private final String TAG_PRODUCT = "product";
    private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
    private CommunityFragment mCommunityFragment;
    private MyPageMainFragment mMyPageFragment;
    private ProductFragment mProductFragment;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragmentManager = fm;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Home
                if (mHomeFragment == null) {
                    mHomeFragment = new HomeFragment();
                   // mHomeFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mHomeFragment;

            case 1: // Community
                if (mCommunityFragment == null) {
                    mCommunityFragment = new CommunityFragment();
                    //mCommunityFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mCommunityFragment;

            case 2: // My Page
                if (mMyPageFragment == null) {
                    mMyPageFragment = new MyPageMainFragment();
                    //mMyPageFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mMyPageFragment;
        }
        return new Fragment();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void addProductCategoryData(Type.Category type,int[] hierarchies) {
        Category c = CommonUtil.getCategory(hierarchies);
        if (c != null) {
            addProductFragment(c);
        }
    }

    public void setProductBrandData(Brand brand) { addProductFragment(brand); }
    public void setProductSearchData(String word) { addProductFragment(word); }

    public void removeProduct() {
        removeProductFragment();
    }

    public void removeAll() {
        if (mProductFragment != null) {
            mProductFragment.removeAll();
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void checkProductFragment() {
        if (mProductFragment == null) {
            mProductFragment = new ProductFragment();
            mProductFragment.setOnBackPressListener(this::removeProductFragment);
        }
        // Exist
        if (!existProductFragment()) {
            mFragmentManager
                    .beginTransaction()
                    .add(R.id.layout_container, mProductFragment, TAG_PRODUCT)
                    .commitAllowingStateLoss();
        }
    }

    private void addProductFragment(Category c) {
        checkProductFragment();
        mProductFragment.changeProduct(Type.ProductListViewType.CATEGORY);
        mProductFragment.setCategory(c);
    }

    private void addProductFragment(Brand b) {
        checkProductFragment();
        mProductFragment.changeProduct(Type.ProductListViewType.BRAND);
        mProductFragment.setBrand(b);
    }

    private void addProductFragment(String s) {
        checkProductFragment();
        mProductFragment.changeProduct(Type.ProductListViewType.SEARCH);
        mProductFragment.setSearch(s);
    }

    private void removeProductFragment() {
        if (mProductFragment != null) {
            mFragmentManager
                    .beginTransaction()
                    .remove(mProductFragment)
                    .commitAllowingStateLoss();
        }
    }

    private boolean existProductFragment() {
        return mProductFragment != null && mProductFragment == mFragmentManager.findFragmentByTag(TAG_PRODUCT);
    }

    ////////////////////////////////////////////////
}
