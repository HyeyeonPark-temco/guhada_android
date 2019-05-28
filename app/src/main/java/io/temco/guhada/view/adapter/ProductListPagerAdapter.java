package io.temco.guhada.view.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.view.fragment.product.ProductListFragment;

public class ProductListPagerAdapter extends FragmentPagerAdapter {

    // -------- LOCAL VALUE --------
    private final String TAG = ProductListPagerAdapter.class.getSimpleName();
    private List<ProductListFragment> mFragmentList;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @NonNull
    @Override
    public ProductListFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void addFragment() {
        ProductListFragment f = new ProductListFragment();
        mFragmentList.add(f);
        notifyDataSetChanged();
    }

    public boolean removeFragment() {
        if (getCount() > 1) {
            mFragmentList.get(getCount() - 1).reset();
            mFragmentList.remove(getCount() - 1);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}