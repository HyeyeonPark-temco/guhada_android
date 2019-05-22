package io.temco.guhada.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.view.fragment.ProductFragment;
import io.temco.guhada.view.fragment.main.MainCommunityFragment;
import io.temco.guhada.view.fragment.main.MainHomeFragment;
import io.temco.guhada.view.fragment.main.MainMyPageFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {

    // -------- LOCAL VALUE --------
    private final String TAG = MainPagerAdapter.class.getSimpleName();
    private FragmentManager mFragmentManager;
    private OnDrawerLayoutListener mDrawerListener;
    private MainHomeFragment mHomeFragment;
    private MainCommunityFragment mCommunityFragment;
    private MainMyPageFragment mMyPageFragment;
    private ProductFragment mProductFragment;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public MainPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
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
                    mHomeFragment = new MainHomeFragment();
                    mHomeFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mHomeFragment;

            case 1: // Community
                if (mCommunityFragment == null) {
                    mCommunityFragment = new MainCommunityFragment();
                    mCommunityFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mCommunityFragment;

            case 2: // My Page
                if (mMyPageFragment == null) {
                    mMyPageFragment = new MainMyPageFragment();
                    mMyPageFragment.setOnDrawerLayoutListener(mDrawerListener);
                }
                return mMyPageFragment;
        }
        return new Fragment();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnDrawerLayoutListener(OnDrawerLayoutListener listener) {
        mDrawerListener = listener;
    }

    public void addProduct() {
        addProductFragment();
    }

    public void removeProduct() {
        removeProductFragment();
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void addProductFragment() {
        if (mProductFragment == null) {
            mProductFragment = new ProductFragment();
            mProductFragment.setOnDrawerLayoutListener(new OnDrawerLayoutListener() {
                @Override
                public void onDrawerEvnet(boolean isOpen) {
                    removeProduct();
                }
            });
        }
        mFragmentManager.beginTransaction().add(R.id.layout_container, mProductFragment).commitAllowingStateLoss();
    }

    private void removeProductFragment() {
        if (mProductFragment != null) {
            mFragmentManager.beginTransaction().remove(mProductFragment).commitAllowingStateLoss();
        }
    }

    ////////////////////////////////////////////////
}
