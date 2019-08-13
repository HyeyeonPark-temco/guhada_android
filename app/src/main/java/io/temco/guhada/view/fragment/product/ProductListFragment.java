package io.temco.guhada.view.fragment.product;

import android.app.Activity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnAddCategoryListener;
import io.temco.guhada.common.listener.OnDetailSearchListener;
import io.temco.guhada.common.listener.OnStateFragmentListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.LoadingIndicatorUtil;
import io.temco.guhada.data.model.Tag;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.server.SearchServer;
import io.temco.guhada.databinding.FragmentProductListBinding;
import io.temco.guhada.view.adapter.TagListAdapter;
import io.temco.guhada.view.adapter.product.ProductListAdapter;
import io.temco.guhada.view.custom.dialog.DetailSearchDialog;
import io.temco.guhada.view.custom.dialog.ProductOrderDialog;
import io.temco.guhada.view.fragment.base.BaseFragment;

/**
 *
 * 상품 리스트 ( 카테고리)
 */
public class ProductListFragment extends BaseFragment<FragmentProductListBinding> implements OnStateFragmentListener, View.OnClickListener {

    // -------- LOCAL VALUE --------
    private final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator(); // Button Animation
    private OnAddCategoryListener mCategoryListener;
    private RequestManager mRequestManager; // Glide
    private ProductOrderDialog mOrderDialog;
    private LoadingIndicatorUtil mLoadingIndicator;
    // List
    private ProductListAdapter mListAdapter;
    private GridLayoutManager mGridManager;
    private TagListAdapter mTagAdapter;
    // Value
    private Type.ProductListViewType mIsCategory = Type.ProductListViewType.NONE; // Category/Brand
    private ProductList mProductListData;
    private Category mCategoryData; // Category
    private Type.Grid mCurrentGridType = Type.Grid.TWO;
    private Type.ProductOrder mCurrentOrderType = Type.ProductOrder.NEW_PRODUCT;
    private boolean mIsLoading = false; // Load More
    private int mPosition;
    private int mId;
    private String mText;
    private int mPageNumber = 1;
    private int tabIndex = 0;
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
        mLoadingIndicator = new LoadingIndicatorUtil(getContext());

        // Glide
        mRequestManager = Glide.with(this);

        // Header
        mBinding.layoutHeaderSub.setClickListener(this);
        changeListType(mCurrentGridType);
        changeProductOrder(mCurrentOrderType);
        setTabLayout();

        // List
        initProductList();

        // Data
        if (mIsCategory == Type.ProductListViewType.CATEGORY) {
            getProductListByCategory(true);
        } else if (mIsCategory == Type.ProductListViewType.BRAND)  {
            getProductListByBrand(true);
        } else if (mIsCategory == Type.ProductListViewType.SEARCH)  {
            getProductListBySearch(true);
        }
    }

    @Override
    public void onReset() {
        if (mIsCategory == Type.ProductListViewType.CATEGORY) {
            mCategoryData = null;
            checkCurrentListType(Type.Grid.TWO);
            changeProductOrder(Type.ProductOrder.NEW_PRODUCT);
            resetList(true);
        }
    }

    @Override
    public void onUpdate(Category data) {
        if (mIsCategory == Type.ProductListViewType.CATEGORY) {
            if (mCategoryData == null || mCategoryData.id != data.id) {
                mCategoryData = data;
                mId = data.id;
                setTabLayout();
                getProductListByCategory(false);
            }
        }
    }

    @Override
    public void onDestroy() {
        mLoadingIndicator.dismiss();
        super.onDestroy();
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
                showOrderDialog();
                break;

            case R.id.layout_search_detail:
                 showDetailSearchDialog();
//                Toast.makeText(getContext(), getString(R.string.common_message_ing), Toast.LENGTH_SHORT).show();
                break;

            // Tag
            case R.id.layout_reset:
                resetTagLayout();
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setPosition(int position) {
        mPosition = position;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setCategoryData(Category data) {
        mCategoryData = data;
        mId = data == null ? 0 : data.id;
    }

    public void setOnAddCategoryListener(OnAddCategoryListener listener) {
        mCategoryListener = listener;
    }

    public void setBrandData(Brand data) {
        mId = data == null ? 0 : data.id;
    }

    public void setSearchData(String data) {
        mId = 0;
        mText = data;
    }

    public void setProductListViewType(Type.ProductListViewType type) {
        mIsCategory = type;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void changeLayout() {
        if (mProductListData != null) {
            initTagLayout();
        }
    }

    private void setTabLayout() {
        if (mBinding != null) {
            if (mIsCategory == Type.ProductListViewType.CATEGORY) {
                setCategoryTabLayout();
            } else {
                mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
            }
        }
    }

    private void setCategoryTabLayout() {
        if (mCategoryData != null && mCategoryData.children != null && mCategoryData.children.size() > 0) {
            mBinding.layoutHeader.layoutTabParent.setVisibility(View.VISIBLE);
            // Remove
            if (mBinding.layoutHeader.layoutTab.getChildCount() > 0) {
                mBinding.layoutHeader.layoutTab.removeAllTabs();
            }
            // Add All (전체보기)
            String title = getContext() != null ? getContext().getString(R.string.category_all) : null;
            Category all = CommonUtil.createAllCategoryData(title, mCategoryData.fullDepthName, mCategoryData.id, mCategoryData.hierarchies);
            addCategoryTab(all, mCategoryData.selectId == -1);
            // Add Category
            int i=0;
            for (Category c : mCategoryData.children) {
                if(mCategoryData.selectId != -1 && c.id == mCategoryData.selectId) {
                    tabIndex = i+1;
                    addCategoryTab(c, true);
                    //loadCategory(c, false);
                }else{
                    addCategoryTab(c, false);
                }
                i++;
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
            /*if(mCategoryData.selectId != -1){
                mBinding.layoutHeader.layoutTab.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        mBinding.layoutHeader.layoutTab.setScrollPosition(tabIndex,0f,true);
                        loadCategory(mCategoryData.children.get(tabIndex-1), false);
                    }
                },150);
            }*/
            // Scroll // Not Used
            // setTabLayoutScrollEvent();
        } else {
            mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
        }
    }

    private void addCategoryTab(Category data, boolean isSelect) {
        if (getContext() != null) {
            View v = getLayoutInflater().inflate(R.layout.layout_tab_category, null);
            // ((TextView) v.findViewById(R.id.text_title)).setText(data.name);
            ((TextView) v.findViewById(R.id.text_title)).setText(data.title);
            TabLayout.Tab tab = mBinding.layoutHeader.layoutTab.newTab().setCustomView(v);
            tab.setTag(data); // Tag
            mBinding.layoutHeader.layoutTab.addTab(tab);
            if (isSelect) tab.select();
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
        if (mIsCategory == Type.ProductListViewType.CATEGORY) {
            getProductListByCategory(true);
        } else  if (mIsCategory == Type.ProductListViewType.BRAND) {
            getProductListByBrand(true);
        }else  if (mIsCategory == Type.ProductListViewType.SEARCH) {
            getProductListBySearch(true);
        }
    }

    private void changeProductOrder(Type.ProductOrder type) {
        dismissOrderDialog();
        mCurrentOrderType = type;
        if (mBinding != null && getContext() != null) {
            switch (type) {
                case NEW_PRODUCT:
                    mBinding.layoutHeaderSub.setOrder(getString(R.string.product_order_new_product));
                    break;

                case MARKS:
                    mBinding.layoutHeaderSub.setOrder(getString(R.string.product_order_marks));
                    break;

                case LOW_PRICE:
                    mBinding.layoutHeaderSub.setOrder(getString(R.string.product_order_low_price));
                    break;

                case HIGH_PRICE:
                    mBinding.layoutHeaderSub.setOrder(getString(R.string.product_order_high_price));
                    break;
            }
        }
    }

    // List
    private void initProductList() {
        // Adapter
        mListAdapter = new ProductListAdapter(getContext(), mRequestManager);

        // [2019.06.26] 임시 브릿지 ; 상품 리스트 아이템 클릭
        //mListAdapter.setOnProductListListener(ProductBridge.Companion::addProductDetailView);
//        ProductDetailActivity.startActivity(getContext(), id)

        // List
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
                    if (mIsCategory==Type.ProductListViewType.CATEGORY) {
                        getProductListByCategory(false);
                    } else if (mIsCategory==Type.ProductListViewType.BRAND) {
                        getProductListByBrand(false);
                    } else if (mIsCategory==Type.ProductListViewType.SEARCH) {
                        getProductListBySearch(false);
                    }
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
                    mBinding.layoutHeaderSub.imageListType1.setSelected(true);
                    mBinding.layoutHeaderSub.imageListType2.setSelected(false);
                    mBinding.layoutHeaderSub.imageListType3.setSelected(false);
                    break;

                case TWO:
                    mBinding.layoutHeaderSub.imageListType1.setSelected(false);
                    mBinding.layoutHeaderSub.imageListType2.setSelected(true);
                    mBinding.layoutHeaderSub.imageListType3.setSelected(false);
                    break;

                case THREE:
                    mBinding.layoutHeaderSub.imageListType1.setSelected(false);
                    mBinding.layoutHeaderSub.imageListType2.setSelected(false);
                    mBinding.layoutHeaderSub.imageListType3.setSelected(true);
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
        mBinding.layoutAppbar.setExpanded(true);
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
      //  changeScaleView(mBinding.buttonFloatingItem.getRoot(), isShow, animate);
    }

    private void changeTopFloatingButton(boolean isShow, boolean animate) {
        changeScaleView(mBinding.buttonFloatingTop.getRoot(), isShow, animate);
    }

    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     *
     */
    private void changeScaleView(View v, boolean isShow, boolean animate) {
        if (isShow) {
            if (v.getVisibility() != View.VISIBLE) {
                v.setOnClickListener(view -> scrollToTop(false));
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

    // Dialog
    private void showOrderDialog() {
        if (getFragmentManager() != null) {
            if (mOrderDialog == null) {
                mOrderDialog = new ProductOrderDialog();
                mOrderDialog.setOnProductOrderListener(this::changeProductOrderWithLoadList);
            }
            mOrderDialog.show(getFragmentManager(), getBaseTag());
        }
    }

    private void dismissOrderDialog() {
        if (mOrderDialog != null) {
            mOrderDialog.dismiss();
        }
    }

    private void showDetailSearchDialog() {
        if (getFragmentManager() != null && mProductListData != null) {
            DetailSearchDialog d = new DetailSearchDialog();
            d.setCategoryData(
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.fullDepthName : null,
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.id : 0,
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.hierarchies : null,
                    mProductListData.categories);
            d.setBrandData(mProductListData.brands);
            d.setFilterData(mProductListData.filters);
            d.setOnDetailSearchListener(new OnDetailSearchListener() {

                @Override
                public void onChange(boolean change) {
                    if (change) changeLayout();
                }

                @Override
                public void onCategory(List<Category> categories) {
                    if (mProductListData != null) {
                        mProductListData.categories = categories;
                    }
                }

                @Override
                public void onBrand(List<Brand> brands) {
                    if (mProductListData != null) {
                        mProductListData.brands = brands;
                    }
                }

                @Override
                public void onFilter(List<Filter> filters) {
                    if (mProductListData != null) {
                        mProductListData.filters = filters;
                    }
                }
            });
            d.show(getFragmentManager(), getBaseTag());
        }
    }

    // TAG
    private void initTagLayout() {
        if (mProductListData != null) {
            mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
            mBinding.layoutHeaderSub.layoutFilterParent.setVisibility(View.VISIBLE);
            mBinding.layoutHeaderSub.layoutReset.setOnClickListener(this);
            //
            initTagList();
            addCategoryTag(mProductListData.categories);
            addBrandTag(mProductListData.brands);
            addFilterTag(mProductListData.filters);
            mTagAdapter.notifyDataSetChanged();
        }
    }

    private void initTagList() {
        // Adapter
        if (mTagAdapter == null) {
            mTagAdapter = new TagListAdapter(getContext());
            mTagAdapter.setOnTagListener(tagData -> {
                if (tagData != null) {
                    if (tagData instanceof Category) {
                        changeCategoryData((Category) tagData, false);
                        // refresh list

                    } else if (tagData instanceof Brand) {
                        changeBrandData((Brand) tagData, false);
                        // refresh list

                    } else if (tagData instanceof Attribute) {
                        changeFilterData((Attribute) tagData, false);
                        // refresh list

                    }
                }
            });
        } else {
            mTagAdapter.removeAll();
        }

        // List
        mBinding.layoutHeaderSub.listTag.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        mBinding.layoutHeaderSub.listTag.setAdapter(mTagAdapter);
    }

    private void addCategoryTag(List<Category> categories) {
        if (categories != null && categories.size() > 0) {
            for (Category c : categories) {
                if (c.isSelected) {
                    addTagTypeFull(c.title, c);
                }
                addCategoryTag(c.children);
            }
        }
    }

    private void addBrandTag(List<Brand> brands) {
        for (Brand b : brands) {
            if (b.isSelected) {
                addTagTypeNormal(b.nameKo, b);
            }
        }
    }

    private void addFilterTag(List<Filter> filters) {
        for (Filter f : filters) {
            for (Attribute a : f.attributes) {
                if (a.selected) {
                    addTagTypeNormal(a.name, a);
                }
            }
        }
    }

    private void addTagTypeFull(String title, Object tagData) {
        Tag t = new Tag();
        t.type = Type.Tag.TYPE_FULL;
        t.title = title;
        t.tagData = tagData;
        mTagAdapter.addItem(t);
    }

    private void addTagTypeNormal(String title, Object tagData) {
        Tag t = new Tag();
        t.type = Type.Tag.TYPE_NORMAL;
        t.title = title;
        t.tagData = tagData;
        mTagAdapter.addItem(t);
    }

    //
    private void changeCategoryData(Category target, boolean isSelected) {
        if (mProductListData != null) {
            changeCategoryData(target, isSelected, mProductListData.categories);
        }
    }

    private void changeCategoryData(Category target, boolean isSelected, List<Category> categories) {
        if (categories != null && categories.size() > 0) {
            for (Category c : categories) {
                if (c.id == target.id) {
                    c.isSelected = isSelected;
                    break;
                }
                changeCategoryData(target, isSelected, c.children);
            }
        }
    }

    private void changeBrandData(Brand target, boolean isSelected) {
        if (mProductListData != null) {
            for (Brand b : mProductListData.brands) {
                if (b.id == target.id) {
                    b.isSelected = isSelected;
                    break;
                }
            }
        }
    }

    private void changeFilterData(Attribute target, boolean isSelected) {
        if (mProductListData != null) {
            for (Filter f : mProductListData.filters) {
                for (Attribute a : f.attributes) {
                    if (a.id == target.id) {
                        a.selected = isSelected;
                        break;
                    }
                }
            }
        }
    }

    //
    private void resetTagLayout() {
        if (mProductListData != null) {
            mBinding.layoutHeader.layoutTabParent.setVisibility(View.VISIBLE);
            mBinding.layoutHeaderSub.layoutFilterParent.setVisibility(View.GONE);
            mBinding.layoutHeaderSub.layoutReset.setOnClickListener(null);
            //
            mTagAdapter.removeAll();
            resetCategoryData(mProductListData.categories);
            resetBrandData(mProductListData.brands);
            resetFilterData(mProductListData.filters);
        }
    }

    private void resetCategoryData(List<Category> categories) {
        if (categories != null && categories.size() > 0) {
            for (Category c : categories) {
                c.isSelected = false;
                resetCategoryData(c.children);
            }
        }
    }

    private void resetBrandData(List<Brand> brands) {
        if (brands != null && brands.size() > 0) {
            for (Brand b : brands) {
                b.isSelected = false;
            }
        }
    }

    private void resetFilterData(List<Filter> filters) {
        if (filters != null && filters.size() > 0) {
            for (Filter f : filters) {
                if (f.attributes != null && f.attributes.size() > 0) {
                    for (Attribute a : f.attributes) {
                        a.selected = false;
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////
    // SERVER
    ////////////////////////////////////////////////

    /**
     * @author park jungho
     * 상품 리스트에서 카테고리 별 상품 리스트 데이터 가져오는 부분
     *
     * @param reset 초기화 여부
     */
    private void getProductListByCategory(boolean reset) {
        if (mIsLoading) return;
        mIsLoading = true;
        if (reset) {
            resetList(false);
        }
        mLoadingIndicator.show();
        SearchServer.getProductListByCategory(mCurrentOrderType, mId, mPageNumber, (success, o) -> {
            if (mListAdapter != null) {
                if (success) {
                    mPageNumber++;
                    mListAdapter.setItems(((ProductList) o).deals);
                    if (mProductListData == null) mProductListData = (ProductList) o;
                } else {
                    ;
                }
            }
            mIsLoading = false;
            mLoadingIndicator.hide();
        });
    }

    /**
     * @author park jungho
     * 상품 리스트에서 브랜드 별 상품 리스트 데이터 가져오는 부분
     *
     * @param reset 초기화 여부
     */
    private void getProductListByBrand(boolean reset) {
        if (mIsLoading) return;
        mIsLoading = true;
        if (reset) {
            resetList(false);
        }
        mLoadingIndicator.show();
        SearchServer.getProductListByBrand(mCurrentOrderType, mId, mPageNumber, (success, o) -> {
            if (mListAdapter != null) {
                if (success) {
                    mPageNumber++;
                    mListAdapter.setItems(((ProductList) o).deals);
                    if (mProductListData == null) mProductListData = (ProductList) o;
                } else {
                    ;
                }
            }
            mIsLoading = false;
            mLoadingIndicator.hide();
        });
    }


    /**
     * @author park jungho
     * 상품 리스트에서 브랜드 별 상품 리스트 데이터 가져오는 부분
     *
     * @param reset 초기화 여부
     */
    private void getProductListBySearch(boolean reset) {
        if (mIsLoading) return;
        mIsLoading = true;
        if (reset) {
            resetList(false);
        }
        mLoadingIndicator.show();
        SearchServer.getProductListBySearch(mCurrentOrderType, mText, mPageNumber, (success, o) -> {
            if (mListAdapter != null) {
                if (success) {
                    mPageNumber++;
                    mListAdapter.setItems(((ProductList) o).deals);
                    if (mProductListData == null) mProductListData = (ProductList) o;
                } else {
                    ;
                }
            }
            mIsLoading = false;
            mLoadingIndicator.hide();
        });
    }

    ////////////////////////////////////////////////
}