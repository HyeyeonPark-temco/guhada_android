package io.temco.guhada.view.fragment.product;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.decoration.EqualSpacingItemDecoration;
import io.temco.guhada.common.listener.OnBackPressListener;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.server.SearchServer;
import io.temco.guhada.databinding.FragmentProductListBinding;
import io.temco.guhada.view.activity.ProductDetailActivity;
import io.temco.guhada.view.adapter.ProductListAdapter;
import io.temco.guhada.view.custom.dialog.ProductOrderBottomDialog;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductListFragment extends BaseFragment<FragmentProductListBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private RequestManager mRequestManager;
    private OnDrawerLayoutListener mDrawerListener;
    private OnBackPressListener mBackListener;
    private ProductListAdapter mListAdapter;
    private GridLayoutManager mGridManager;
    private ProductOrderBottomDialog mOrderBottomDialog;
    private Type.ProductOrder mCurrentOrderType = Type.ProductOrder.NEW_PRODUCT;
    private Type.Grid mCurrentGridType = Type.Grid.TWO;
    private int mPageNumber = 1;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return ProductListFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_product_list;
    }

    @Override
    protected void init() {
        // Glide
        mRequestManager = Glide.with(this);

        // Header
        mBinding.layoutHeader.setClickListener(this);
        setTabLayout();
        changeListType(mCurrentGridType);
        changeProductOrder(Type.ProductOrder.NEW_PRODUCT);

        // List
        initProductList();

        // Data
        getProductListByCategory(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

            // Option
            case R.id.layout_order:
                showOrderBottomDialog();
                break;

            case R.id.layout_search_detail:
                showOrderBottomDialog();
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

    // Order
    private void changeProductOrder(Type.ProductOrder type) {
        dismissOrderBottomDialog();
        mCurrentOrderType = type;
        switch (type) {
            case NEW_PRODUCT:
                mBinding.layoutHeader.setOrder(getString(R.string.product_order_new_product));
                break;

            case MARKS:
                mBinding.layoutHeader.setOrder(getString(R.string.product_order_marks));
                break;

            case LOW_PRICE:
                mBinding.layoutHeader.setOrder(getString(R.string.product_order_low_price));
                break;

            case HIGH_PRICE:
                mBinding.layoutHeader.setOrder(getString(R.string.product_order_high_price));
                break;
        }
    }

    private void showOrderBottomDialog() {
        if (getFragmentManager() != null) {
            if (mOrderBottomDialog == null) {
                mOrderBottomDialog = new ProductOrderBottomDialog();
                mOrderBottomDialog.setOnProductOrderListener(this::changeProductOrder);
            }
            mOrderBottomDialog.show(getFragmentManager(), getBaseTag());
        }
    }

    private void dismissOrderBottomDialog() {
        if (mOrderBottomDialog != null) {
            mOrderBottomDialog.dismiss();
        }
    }

    // List
    private void initProductList() {
        // Adapter
        if (mListAdapter == null)
            mListAdapter = new ProductListAdapter(getContext(), mRequestManager);
        mListAdapter.setOnProductListListener(id -> ProductDetailActivity.startActivity(getContext(), id));

        // List
        if (mGridManager == null)
            mGridManager = new GridLayoutManager(getContext(), Type.Grid.get(mCurrentGridType));
        mBinding.listContents.setLayoutManager(mGridManager);
        mBinding.listContents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Top
                } else if (!recyclerView.canScrollVertically(1)) {
                    // Bottom
                } else {
                    // Idle
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mBinding.listContents.setAdapter(mListAdapter);
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
        if (mGridManager != null && mListAdapter != null) {
            mGridManager.setSpanCount(Type.Grid.get(type));
            mListAdapter.setSpanCount(type);
        }
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    private void getProductListByCategory(int id) {
        SearchServer.getProductListByCategory(mCurrentOrderType, id, mPageNumber, (success, o) -> {
            if (success && mListAdapter != null) {
                mListAdapter.setItems(((ProductList) o).deals);
            } else {

            }
        });
    }

    ////////////////////////////////////////////////
}
