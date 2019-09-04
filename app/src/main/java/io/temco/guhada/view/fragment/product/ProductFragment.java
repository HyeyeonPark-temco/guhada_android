package io.temco.guhada.view.fragment.product;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.FragmentProductBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.ProductListPagerAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

/**
 * parkjungho
 * 카테고리, 브랜드 클릭후 넘어오는 값의 productList의 ViewPager를 담고 있는 fragment
 * 내부에 표시 해야할 카테고리나 브랜드의 숫자만큼 productListFragment의 상품리스트 객채를 만들고 보여줌
 * 상품 리스트의 껍데기 viewPager를 담고 있는 Fragment
 */
public class ProductFragment extends BaseFragment<FragmentProductBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnBackPressListener mBackListener;
    private FragmentManager mFragmentManager;
    private ProductListPagerAdapter mListPagerAdapter;
    //
    private Type.ProductListViewType mType;
    private Category mCategoryData;
    private Brand mBrandData;
    private String mSearchData;


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
        mBinding.layoutHeaderSearch.setClickListener(this);

        // List
        CommonUtil.delayRunnable(() -> {
            initList();
            if (mType == Type.ProductListViewType.CATEGORY) {
                addCategoryList();
                mBinding.layoutHeader.layoutHeader.setVisibility(View.VISIBLE);
                mBinding.layoutHeaderSearch.layoutHeaderSearch.setVisibility(View.GONE);
            } else if (mType == Type.ProductListViewType.BRAND) {
                addBrandList();
                mBinding.layoutHeader.layoutHeader.setVisibility(View.VISIBLE);
                mBinding.layoutHeaderSearch.layoutHeaderSearch.setVisibility(View.GONE);
            }else if (mType == Type.ProductListViewType.SEARCH) {
                addSearchList();
                mBinding.layoutHeader.layoutHeader.setVisibility(View.GONE);
                mBinding.layoutHeaderSearch.layoutHeaderSearch.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Top Menu
            case R.id.image_prev:
                checkBackEvent();
                break;

            case R.id.image_side_menu:
                CommonUtil.startMenuActivity((BindActivity) getContext(), Flag.RequestCode.SIDE_MENU_FROM_PRODUCT_FILTER);
                break;

            case R.id.image_delete:
            case R.id.image_search:
                CommonUtil.startSearchWordActivity((Activity) getContext(),null, mType != Type.ProductListViewType.SEARCH);
                break;

            case R.id.image_shop_cart:
                CommonUtil.startCartActivity((Activity) getContext());
                break;

            case R.id.text_search_headertitle:
                CommonUtil.startSearchWordActivity((Activity) getContext(),mBinding.layoutHeaderSearch.textSearchHeadertitle.getText().toString(), mType != Type.ProductListViewType.SEARCH);
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void changeProduct(Type.ProductListViewType type) {
        mType = type;
    }
    public void setCategory(Category data) {
        mCategoryData = data;
        addCategoryList();
    }

    public void setBrand(Brand data) {
        mBrandData = data;
        addBrandList();
    }

    public void setSearch(String data) {
        mSearchData = data;
        addSearchList();
    }


    public void removeAll() {
        if (mFragmentManager != null) {
//            if (mFragmentManager.getBackStackEntryCount() > 0) {
//                mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            }
            for (int i = 0; i < mFragmentManager.getBackStackEntryCount(); i++) {
                mFragmentManager.popBackStackImmediate();
            }
            if (mListPagerAdapter != null) mListPagerAdapter.removeAll();
        }
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
            mBinding.layoutPager.setSwipeEnabled(true);
        }
    }

    private void addCategoryList() {
        if (mBinding != null && mCategoryData != null) {
            mListPagerAdapter.setOnAddCategoryListener(this::addCategoryChildFragment);
            // setTitle(mCategoryData.name);
            setTitle(mCategoryData.title);
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

    private void addSearchList() {
        if (mBinding != null && mSearchData != null) {
            setTitle(mSearchData);
            addSearchFragment(mSearchData);
        }
    }

    private void addBrandChildFragment(Brand data) {
        mListPagerAdapter.addBrandFragment(data);
        mBinding.layoutPager.setOffscreenPageLimit(mListPagerAdapter.getCount());
        mBinding.layoutPager.setCurrentItem(mListPagerAdapter.getCount(), true);
    }


    private void addSearchFragment(String data) {
        mListPagerAdapter.addSearchFragment(data);
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
                mFragmentManager.popBackStack();
            } else {
                ((Activity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                ((Activity)getContext()).finish();
                ((Activity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
            }
        }
    }

    private void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            if (mType == Type.ProductListViewType.SEARCH) {
                mBinding.layoutHeaderSearch.setTitle(title);
            }else{
                mBinding.layoutHeader.setTitle(title);
            }
        }
    }

    ////////////////////////////////////////////////
}
