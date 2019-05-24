package io.temco.guhada.view.fragment;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.databinding.FragmentProductBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductFragment extends BaseFragment<FragmentProductBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDrawerLayoutListener mDrawerListener;
    private OnBackPressListener mBackListener;

    private Type.Grid mCurrentGridType = Type.Grid.TWO;
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
        mBinding.layoutHeader.setClickListener(this);

        setTabLayout();
        changeListType(mCurrentGridType);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Top Menu
            case R.id.image_prev:
                if (mBackListener != null) mBackListener.onPress();
                break;

            case R.id.image_side_menu:
                if (mDrawerListener != null) mDrawerListener.onDrawerEvnet(true);
                break;

            case R.id.image_search:
            case R.id.image_shop_cart:
                if (mDrawerListener != null) mDrawerListener.onDrawerEvnet(false);
                break;

            // List Type
            case R.id.image_list_type_1:
                checkCurrentListType(Type.Grid.ONE);
                break;

            case R.id.image_list_type_2:
                checkCurrentListType(Type.Grid.TWO);
                break;

            case R.id.image_list_type_3:
                checkCurrentListType(Type.Grid.THREE);
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

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setTabLayout() {
        addCustomTabs(1);
    }

    private void addCustomTabs(int current) {
        // Add Custom Tab
        for (int i = 1; i <= 5; i++) {
            View v = getLayoutInflater().inflate(R.layout.layout_tab_category, null);

            // Test
            if (i % 2 == 0) {
                ((TextView) v.findViewById(R.id.text_title)).setText("titield" + i);
            } else {
                ((TextView) v.findViewById(R.id.text_title)).setText("titielasdfasdfasdd" + i);
            }

            // Tab
            TabLayout.Tab tab = mBinding.layoutHeader.layoutTab.newTab().setCustomView(v);
            mBinding.layoutHeader.layoutTab.addTab(tab);
            if (i == current) {
                tab.select();
            }
        }
        // Scroll
        // Not Used
        // setTabLayoutScrollEvent();
    }

    private void setTabLayoutScrollEvent() {
        mBinding.layoutHeader.layoutTab.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (mBinding.layoutHeader.layoutTab.getWidth() < mBinding.layoutHeader.layoutTab.getChildAt(0).getWidth()) {
                mBinding.layoutHeader.layoutTab.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                    int currentScroll = 0;

                    @Override
                    public void onScrollChanged() {
                        if (mBinding.layoutHeader.layoutTab.getScrollX() != 0) {
                            int x = mBinding.layoutHeader.layoutTab.getScrollX();
                            CommonUtil.debug("currentScroll == " + currentScroll);
                            CommonUtil.debug("x == " + x);
                            if (currentScroll < x) {
                                mBinding.layoutHeader.layoutTabLeftDirection.setVisibility(View.VISIBLE);
                                mBinding.layoutHeader.layoutTabRightDirection.setVisibility(View.GONE);
                            } else if (currentScroll > x) {
                                mBinding.layoutHeader.layoutTabLeftDirection.setVisibility(View.GONE);
                                mBinding.layoutHeader.layoutTabRightDirection.setVisibility(View.VISIBLE);
                            }
                            currentScroll = x;
                        }
                    }
                });
            }
        });
    }

    private void checkCurrentListType(Type.Grid type) {
        if (mCurrentGridType != type) {
            changeListType(type);
        }
    }

    private void changeListType(Type.Grid type) {
        mCurrentGridType = type;
        switch (type) {
            case ONE:
                mBinding.layoutHeader.imageListType1.setSelected(true);
                mBinding.layoutHeader.imageListType2.setSelected(false);
                mBinding.layoutHeader.imageListType3.setSelected(false);
                break;

            case TWO:
                mBinding.layoutHeader.imageListType1.setSelected(false);
                mBinding.layoutHeader.imageListType2.setSelected(true);
                mBinding.layoutHeader.imageListType3.setSelected(false);
                break;

            case THREE:
                mBinding.layoutHeader.imageListType1.setSelected(false);
                mBinding.layoutHeader.imageListType2.setSelected(false);
                mBinding.layoutHeader.imageListType3.setSelected(true);
                break;
        }
    }

    ////////////////////////////////////////////////
}
