package io.temco.guhada.view.fragment.product;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.tabs.TabLayout;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnAddCategoryListener;
import io.temco.guhada.common.listener.OnStateFragmentListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.server.SearchServer;
import io.temco.guhada.databinding.FragmentProductListBinding;
import io.temco.guhada.view.activity.ProductDetailActivity;
import io.temco.guhada.view.adapter.ProductListAdapter;
import io.temco.guhada.view.custom.dialog.ProductOrderBottomDialog;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class ProductListFragment extends BaseFragment<FragmentProductListBinding> implements OnStateFragmentListener, View.OnClickListener {

    // -------- LOCAL VALUE --------
    private final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator(); // Button Animation
    private OnAddCategoryListener mCategoryListener;
    private RequestManager mRequestManager; // Glide
    private ProductOrderBottomDialog mOrderBottomDialog;
    // List
    private ProductListAdapter mListAdapter;
    private GridLayoutManager mGridManager;
    // Value
    private Category mCategoryData;
    private Type.ProductOrder mCurrentOrderType = Type.ProductOrder.NEW_PRODUCT;
    private Type.Grid mCurrentGridType = Type.Grid.TWO;
    private int mPosition;
    private boolean mIsLoading = false; // Load More
    private int mId;
    private int mPageNumber = 1;
    // -----------------------------

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

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
        changeListType(mCurrentGridType);
        changeProductOrder(mCurrentOrderType);
        setTabLayout();

        // List
        initProductList();

        // Data
        getProductListByCategory(true);
    }

    @Override
    public void onReset() {
        mCategoryData = null;
        checkCurrentListType(Type.Grid.TWO);
        changeProductOrder(Type.ProductOrder.NEW_PRODUCT);
        resetList(true);
    }

    @Override
    public void onUpdate(Category data) {
        if (mCategoryData == null || mCategoryData.id != data.id) {
            mCategoryData = data;
            mId = data.id;
            setTabLayout();
            getProductListByCategory(false);
        }
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

    public void setCategoryData(Category data) {
        mCategoryData = data;
        mId = data.id;
    }

    public void setOnAddCategoryListener(OnAddCategoryListener listener) {
        mCategoryListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setTabLayout() {
        if (mBinding != null
                && mCategoryData != null && mCategoryData.children != null) {
            // Remove
            if (mBinding.layoutHeader.layoutTab.getChildCount() > 0) {
                mBinding.layoutHeader.layoutTab.removeAllTabs();
            }
            // Add All
            Category all = CommonUtil.createAllCategoryData(getContext(), mCategoryData.id, mCategoryData.hierarchies);
            addCategoryTab(all, true);
            // Add Category
            for (Category c : mCategoryData.children) {
                addCategoryTab(c, false);
            }
            // Select Event
            mBinding.layoutHeader.layoutTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getTag() != null && tab.getTag() instanceof Category) {
                        loadCategory((Category) tab.getTag(), false);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getTag() != null && tab.getTag() instanceof Category) {
                        loadCategory((Category) tab.getTag(), true);
                    }
                }
            });

            // Scroll // Not Used
            // setTabLayoutScrollEvent();
        }
    }

    private void loadCategory(Category data, boolean isReselected) {
        if (data.children != null && data.children.size() > 0) {
            if (mCategoryListener != null) {
                mCategoryListener.onAdd(data);
            }
        } else {
            if (!isReselected) {
                mId = data.id;
                getProductListByCategory(true);
            }
        }
    }

    private void addCategoryTab(Category data, boolean isSelect) {
        View v = getLayoutInflater().inflate(R.layout.layout_tab_category, null);
        ((TextView) v.findViewById(R.id.text_title)).setText(data.name);
        TabLayout.Tab tab = mBinding.layoutHeader.layoutTab.newTab().setCustomView(v);
        tab.setTag(data); // Tag
        mBinding.layoutHeader.layoutTab.addTab(tab);
        if (isSelect) tab.select();
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
    private void changeProductOrderWithLoadList(Type.ProductOrder type) {
        changeProductOrder(type);
        getProductListByCategory(true);
    }

    private void changeProductOrder(Type.ProductOrder type) {
        dismissOrderBottomDialog();
        mCurrentOrderType = type;
        if (mBinding != null) {
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
    }

    private void showOrderBottomDialog() {
        if (getFragmentManager() != null) {
            if (mOrderBottomDialog == null) {
                mOrderBottomDialog = new ProductOrderBottomDialog();
                mOrderBottomDialog.setOnProductOrderListener(this::changeProductOrderWithLoadList);
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
        // if (mListAdapter == null)
        mListAdapter = new ProductListAdapter(getContext(), mRequestManager);
        mListAdapter.setOnProductListListener(id -> ProductDetailActivity.startActivity(getContext(), id));

        // List
        // if (mGridManager == null)
        mGridManager = new GridLayoutManager(getContext(), Type.Grid.get(mCurrentGridType));
        mBinding.listContents.setLayoutManager(mGridManager);
        mBinding.listContents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Top
                    changeFloatingButtonLayout(false);
                } else if (!recyclerView.canScrollVertically(1)) {
                    // Bottom
                } else {
                    // Idle
                    changeFloatingButtonLayout(true);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                // super.onScrolled(recyclerView, dx, dy);
                if (mListAdapter.getItemCount() - mGridManager.findLastVisibleItemPosition() <= Info.LIST_PAGE_THRESHOLD) {
                    getProductListByCategory(false);
                }
            }
        });
        mBinding.listContents.setAdapter(mListAdapter);
    }

    private void resetList(boolean notify) {
        mPageNumber = 1;
        scrollToTop(false);
        if (mListAdapter != null) {
            mListAdapter.reset();
            if (notify) mListAdapter.notifyDataSetChanged();
        }
        changeFloatingButtonLayout(false);
    }

    private void checkCurrentListType(Type.Grid type) {
        if (mCurrentGridType != type) {
            changeListType(type);
        }
    }

    private void changeListType(Type.Grid type) {
        mCurrentGridType = type;

        if (mBinding != null) {
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
        changeListSpanCount(type);
    }

    private void changeListSpanCount(Type.Grid type) {
        if (mGridManager != null && mListAdapter != null) {
            mGridManager.setSpanCount(Type.Grid.get(type));
            mListAdapter.setSpanCount(type);
        }
    }

    private void scrollToTop(boolean isSmooth) {
        if (isSmooth) {
            mBinding.listContents.smoothScrollToPosition(0);
        } else {
            mBinding.listContents.scrollToPosition(0);
        }
    }

    // Floating Button
    private void changeFloatingButtonLayout(boolean isShow) {
        changeTopFloatingButton(isShow);
        changeItemFloatingButton(isShow);
    }

    private void changeItemFloatingButton(boolean isShow) {
        changeItemFloatingButton(isShow, false);
    }

    private void changeTopFloatingButton(boolean isShow) {
        changeTopFloatingButton(isShow, false);
    }

    private void changeItemFloatingButton(boolean isShow, boolean animate) {
        changeScaleView(mBinding.buttonFloatingItem.getRoot(), isShow, animate);
    }

    private void changeTopFloatingButton(boolean isShow, boolean animate) {
        changeScaleView(mBinding.buttonFloatingTop.getRoot(), isShow, animate);
    }

    private void changeScaleView(View v, boolean isShow, boolean animate) {
        if (isShow) {
            if (v.getVisibility() != View.VISIBLE) {
                v.setOnClickListener(view -> scrollToTop(true));
                v.setVisibility(View.VISIBLE);
                if (animate) {
                    showScaleAnimation(v);
                }
            }
        } else {
            if (v.getVisibility() == View.VISIBLE) {
                v.setOnClickListener(null);
                if (animate) {
                    hideScaleAnimation(v);
                } else {
                    v.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showScaleAnimation(View v) {
        ViewCompat.animate(v)
                .scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start();
    }

    private void hideScaleAnimation(View v) {
        ViewCompat.animate(v)
                .scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(new ViewPropertyAnimatorListener() {
                    public void onAnimationStart(View view) {
                    }

                    public void onAnimationCancel(View view) {
                    }

                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    private void getProductListByCategory(boolean reset) {
        if (mIsLoading) return;
        mIsLoading = true;
        if (reset) {
            resetList(false);
        }
        SearchServer.getProductListByCategory(mCurrentOrderType, mId, mPageNumber, (success, o) -> {
            if (mListAdapter != null) {
                if (success) {
                    mPageNumber++;
                    mListAdapter.setItems(((ProductList) o).deals);
                } else {
                    ;
                }
            }
            mIsLoading = false;
        });
    }

    ////////////////////////////////////////////////
}