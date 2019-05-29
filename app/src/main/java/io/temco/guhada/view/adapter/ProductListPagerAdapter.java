package io.temco.guhada.view.adapter;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.common.listener.OnAddCategoryListener;
import io.temco.guhada.common.listener.OnChangeTitleListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.fragment.product.ProductListFragment;

public class ProductListPagerAdapter extends FragmentStatePagerAdapter {

    // -------- LOCAL VALUE --------
    private final String TAG = ProductListPagerAdapter.class.getSimpleName();
    private OnChangeTitleListener mChangeTitleListener;
    private OnAddCategoryListener mCategoryListener;
    private List<Category> mCategoryList;
    private List<ProductListFragment> mFragmentList;
    private FragmentManager mFragmentManager;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mCategoryList = new ArrayList<>();
        mFragmentList = new ArrayList<>();
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (object instanceof ProductListFragment) {
            int position = ((ProductListFragment) object).getPosition();
            if (position < mCategoryList.size()) {
                Category data = mCategoryList.get(position);
                if (data != null) {
                    ((ProductListFragment) object).onUpdate(data);
                }
            }
        }
//        return POSITION_NONE;
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }

    @NonNull
    @Override
    public ProductListFragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void addFragment(Category data) {
        // Fragment
        ProductListFragment f = new ProductListFragment();
        f.setCategoryData(data);
        f.setOnAddCategoryListener(mCategoryListener);
        // Add
        mFragmentList.add(f);
        mCategoryList.add(data);
        // Position
        f.setPosition(mFragmentList.size() - 1);
        // Title
        if (mChangeTitleListener != null) {
            mChangeTitleListener.onSet(data.name);
        }
        notifyDataSetChanged();
    }

    public boolean removeFragment() {
        int changeCount = getCount() - 1;
        // Title
        if (mChangeTitleListener != null) {
            String title = mCategoryList.get(changeCount - 1).name;
            mChangeTitleListener.onSet(title);
        }
        // Remove
        if (getCount() > 1) {
            mFragmentList.remove(changeCount);
            mCategoryList.remove(changeCount);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void setOnChangeTitleListener(OnChangeTitleListener listener) {
        mChangeTitleListener = listener;
    }

    public void setOnAddCategoryListener(OnAddCategoryListener listener) {
        mCategoryListener = listener;
    }

    ////////////////////////////////////////////////
}