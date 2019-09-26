package io.temco.guhada.view.fragment.product;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.temco.guhada.R;
import io.temco.guhada.common.ActivityMoveToMain;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Flag;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnAddCategoryListener;
import io.temco.guhada.common.listener.OnBrandListener;
import io.temco.guhada.common.listener.OnCallBackListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.listener.OnDetailSearchListener;
import io.temco.guhada.common.listener.OnStateFragmentListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CommonUtilKotlin;
import io.temco.guhada.common.util.CommonViewUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.LoadingIndicatorUtil;
import io.temco.guhada.data.db.GuhadaDB;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.data.model.Tag;
import io.temco.guhada.data.model.body.FilterBody;
import io.temco.guhada.data.server.SearchServer;
import io.temco.guhada.databinding.FragmentProductListBinding;
import io.temco.guhada.view.activity.ProductFilterListActivity;
import io.temco.guhada.view.adapter.CategoryTitle;
import io.temco.guhada.view.adapter.TagListAdapter;
import io.temco.guhada.view.adapter.product.ProductListAdapter;
import io.temco.guhada.view.custom.dialog.BrandListDialog;
import io.temco.guhada.view.custom.dialog.CategoryListDialog;
import io.temco.guhada.view.custom.dialog.DetailSearchDialog;
import io.temco.guhada.view.custom.dialog.ProductOrderDialog;
import io.temco.guhada.view.fragment.base.BaseFragment;
import io.temco.guhada.view.fragment.mypage.MyPageTabType;

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
    private Map<Integer,Map<Integer, Category>> mDepthTitle;
    private Map<Integer,Map<Integer, Category>> mDepthOldeTitle; // backup
    private Category mCategoryData; // Category
    private Type.Grid mCurrentGridType = Type.Grid.TWO;
    private Type.ProductOrder mCurrentOrderType = Type.ProductOrder.NEW_PRODUCT;
    private boolean mIsLoading = false; // Load More
    private int mPosition;
    private int mId;
    private String mText;
    private int mPageNumber = 1;
    private int tabIndex = 0;
    private int tabWidth = 0;
    private int recentViewCount = -1;
    private boolean scrollviewOnTop = true;

    private FilterBody filterBody = null;

    private CompositeDisposable disposable;
    private GuhadaDB db;

    private CategoryListDialog mCategoryListDialog = null;
    private BrandListDialog mBrandListDialog = null;

    private View layout_tab_category;
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
        disposable = new CompositeDisposable();
        db = GuhadaDB.Companion.getInstance(getContext());
        // Glide
        mRequestManager = Glide.with(this);

        // Header
        mBinding.layoutHeaderSub.setClickListener(this);
        mBinding.setClickListener(this);

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
        scrollviewOnTop = true;
        /* 상품 목록의 하단 select ui
        if(((ProductFilterListActivity)getContext()).getType() ==  Type.ProductListViewType.CATEGORY) {
            mBinding.imageviewMaintabIcon1.setBackgroundResource(R.drawable.tool_icon_category_on);
            mBinding.textviewMaintabTitle1.setTextColor(Color.parseColor("#5d2ed1"));
        }else if(((ProductFilterListActivity)getContext()).getType() ==  Type.ProductListViewType.BRAND) {
            mBinding.imageviewMaintabIcon2.setBackgroundResource(R.drawable.tool_icon_brand_on);
            mBinding.textviewMaintabTitle2.setTextColor(Color.parseColor("#5d2ed1"));
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        if(scrollviewOnTop) getRecentProductCount();
        else setRecentProductCount();
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
        try {
            disposable.dispose();
            disposable = null;
            GuhadaDB.Companion.destroyInstance();
            db = null;
        }catch (Exception e){
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
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

            case R.id.layout_maintab_layout1:
                showCategoryListDialog();
                break;

            case R.id.layout_maintab_layout2:
                showBrandListDialog();
                break;

            case R.id.layout_maintab_layout3:
                BaseApplication.getInstance().setMoveToMain(new ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_HOME, true));
                ((ProductFilterListActivity)getContext()).setResult(Flag.ResultCode.GO_TO_MAIN_HOME);
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                ((ProductFilterListActivity)getContext()).finish();
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                break;


            case R.id.layout_maintab_layout4:
                BaseApplication.getInstance().setMoveToMain(new ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_COMUNITY, true));
                ((ProductFilterListActivity)getContext()).setResult(Flag.ResultCode.GO_TO_MAIN_COMUNITY);
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                ((ProductFilterListActivity)getContext()).finish();
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                break;

            case R.id.layout_maintab_layout5:
                BaseApplication.getInstance().setMoveToMain(new ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, true));
                ((ProductFilterListActivity)getContext()).setResult(Flag.ResultCode.GO_TO_MAIN_MYPAGE);
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
                ((ProductFilterListActivity)getContext()).finish();
                ((ProductFilterListActivity)getContext()).overridePendingTransition(R.anim.fade, R.anim.fade);
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
        if (mProductListData != null || mDepthTitle != null) {
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
            tabWidth = 0;
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
                    TextView text = tab.getCustomView().findViewById(R.id.text_title);
                    text.setTypeface(null, Typeface.BOLD);
                    if (tab.getTag() != null && tab.getTag() instanceof Category) {
                        loadCategory((Category) tab.getTag(), false);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    TextView text = tab.getCustomView().findViewById(R.id.text_title);
                    text.setTypeface(null, Typeface.NORMAL);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getTag() != null && tab.getTag() instanceof Category) {
                        loadCategory((Category) tab.getTag(), true);
                    }
                }
            });
            if(mCategoryData.selectId != -1){
                mBinding.layoutHeader.layoutTab.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        mBinding.layoutHeader.layoutTab.setScrollPosition(tabIndex,0f,true);
                        loadCategory(mCategoryData.children.get(tabIndex-1), false);
                    }
                },150);
            }
            // Scroll // Not Used
            //setTabLayoutScrollEvent();
        } else {
            mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
        }
    }

    private void addCategoryTab(Category data, boolean isSelect) {
        if (getContext() != null) {
            layout_tab_category = getLayoutInflater().inflate(R.layout.layout_tab_category, null);
            TextView text_title = layout_tab_category.findViewById(R.id.text_title);
            text_title.setText(data.title);
            TabLayout.Tab tab = mBinding.layoutHeader.layoutTab.newTab().setCustomView(layout_tab_category);
            tab.setTag(data); // Tag
            mBinding.layoutHeader.layoutTab.addTab(tab);
            if (isSelect) {
                text_title.setTypeface(null, Typeface.BOLD);
                tab.select();
            }else{
                text_title.setTypeface(null, Typeface.NORMAL);
            }
            /*Rect bounds = new Rect();
            Paint textPaint = text_title.getPaint();
            textPaint.getTextBounds(data.title, 0, data.title.length(), bounds);*/

            /*tabWidth += data.title.length() * CommonViewUtil.INSTANCE.convertDpToPixel(9,getContext());
            tabWidth += CommonViewUtil.INSTANCE.convertDpToPixel(2,getContext());*/
            //tabWidth += bounds.width();
            /*layout_tab_category.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tabWidth += layout_tab_category.getMeasuredWidth();
                    layout_tab_category.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });*/
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

    // 카테고리 탭 사이드 화살표에 대한 로직 이였던것.....
    /*private void setTabLayoutScrollEvent() {
        mBinding.layoutHeader.layoutTab.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (mBinding.layoutHeader.layoutTab.getWidth() < mBinding.layoutHeader.layoutTab.getChildAt(0).getWidth()) {
                mBinding.layoutHeader.layoutTab.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    int currentScroll = 0;
                    @Override
                    public void onScrollChanged() {
                        if (mBinding.layoutHeader.layoutTab.getScrollX() != 0) {
                            int x = mBinding.layoutHeader.layoutTab.getScrollX();
                            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("setTabLayoutScrollEvent 2","getScrollX",x);
                            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("setTabLayoutScrollEvent 2","tabWidth",tabWidth);
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
    }*/

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

        // List
        mGridManager = new GridLayoutManager(getContext(), Type.Grid.get(mCurrentGridType));
        mBinding.listContents.setLayoutManager(mGridManager);
        mBinding.listContents.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                // super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Top
                    scrollviewOnTop = true;
                    changeFloatingButtonLayout(false);
                } else if (!recyclerView.canScrollVertically(1)) {
                    // Bottom
                } else {
                    // Idle
                    scrollviewOnTop = false;
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
        if (CommonUtil.checkToken()) {
            if(recentViewCount > 0)
                changeLastView(mBinding.buttonFloatingItem.getRoot(), isShow, animate);
        }else{
            changeLastView(mBinding.buttonFloatingItem.getRoot(), false, false);
        }
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


    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     *
     */
    private void changeLastView(View v, boolean isShow, boolean animate) {
        if (isShow) {
            if (v.getVisibility() != View.VISIBLE) {
                v.setOnClickListener(view -> {
                    BaseApplication.getInstance().setMoveToMain(new ActivityMoveToMain(Flag.ResultCode.GO_TO_MAIN_MYPAGE, MyPageTabType.LAST_VIEW.ordinal(), true));
                    ((Activity)getContext()).setResult(Flag.ResultCode.GO_TO_MAIN_HOME);
                    ((Activity)getContext()).onBackPressed();
                });
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

    private void setRecentProductCount(){
        try {
            if (!CommonUtil.checkToken()) return;
            CommonUtilKotlin.INSTANCE.recentProductCount(disposable, db, new OnCallBackListener() {
                @Override
                public void callBackListener(boolean resultFlag, @NotNull Object value) {
                    try {
                        recentViewCount = Integer.parseInt(value.toString());
                        mBinding.buttonFloatingItem.textviewFloatingCount.setText(value.toString());
                        if(recentViewCount == 0){
                            changeLastView(mBinding.buttonFloatingItem.getRoot(), false, false);
                        }else{
                            changeLastView(mBinding.buttonFloatingItem.getRoot(), true, false);
                        }
                    }catch (Exception e){
                        changeLastView(mBinding.buttonFloatingItem.getRoot(), false, false);
                        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
                    }
                }
            });
        }catch (Exception e){
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
        }
    }


    private void getRecentProductCount(){
        try {
            if (!CommonUtil.checkToken()) return;
            CommonUtilKotlin.INSTANCE.recentProductCount(disposable, db, new OnCallBackListener() {
                @Override
                public void callBackListener(boolean resultFlag, @NotNull Object value) {
                    try {
                        recentViewCount = Integer.parseInt(value.toString());
                        mBinding.buttonFloatingItem.textviewFloatingCount.setText(value.toString());
                    }catch (Exception e){
                        if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
                    }
                }
            });
        }catch (Exception e){
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.E(e);
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
            if(mDepthTitle == null) {
                mDepthTitle = new HashMap<>();
            }
            if((mIsCategory==Type.ProductListViewType.CATEGORY)){
                if(CustomLog.getFlag())CustomLog.L("showDetailSearchDialog","mCategoryData.fullDepthName",mCategoryData.fullDepthName);
                if(CustomLog.getFlag())CustomLog.L("showDetailSearchDialog","mCategoryData.id",mCategoryData.id);
                if(CustomLog.getFlag())CustomLog.L("showDetailSearchDialog","mCategoryData.hierarchies", Arrays.toString(mCategoryData.hierarchies));
                setDepthTitle(0, mProductListData.categories, mCategoryData.hierarchies);
            }

            if(CustomLog.getFlag())CustomLog.L("showDetailSearchDialog","mDepthTitle", mDepthTitle.toString());
            DetailSearchDialog d = new DetailSearchDialog();
            d.setCategoryData(
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.fullDepthName : null,
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.id : 0,
                    (mIsCategory==Type.ProductListViewType.CATEGORY) ? mCategoryData.hierarchies : null,
                    mProductListData.categories);
            d.setBrandData(mProductListData.brands);
            d.setmDepthTitle(mDepthTitle);
            d.setFilterData(mProductListData.filters);
            d.setOnDetailSearchListener(new OnDetailSearchListener() {
                @Override
                public void onChange(boolean change) {
                    if (change) changeLayout();
                }

                @Override
                public void onCategory(List<Category> categories) {
                    /*if (mProductListData != null) {
                        mProductListData.categories = categories;
                    }*/
                }

                @Override
                public void onCategoryResult(Map<Integer, Map<Integer, Category>> map) {
                    if (map != null) {
                        if(CustomLog.getFlag())CustomLog.L("onCategoryResult","mDepthTitle",mDepthTitle);
                        mDepthTitle = map;
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


    private void setDepthTitle(int depth, List<Category> parent, int[] hierarchies){
        for (Category children : parent){
            if(children.id == mCategoryData.hierarchies[depth]){
                HashMap<Integer, Category> map = new HashMap<>();
                map.put(children.id, children);
                mDepthTitle.put(depth,map);
                if(mCategoryData.hierarchies.length > (depth+1) && (children.children != null && children.children.size() > 0)){
                    setDepthTitle(depth+1, children.children, hierarchies);
                }
            }
        }
    }

    // TAG
    private void initTagLayout() {
        filterBody = null;
        mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
        mBinding.layoutHeaderSub.layoutFilterParent.setVisibility(View.VISIBLE);
        mBinding.layoutHeaderSub.layoutReset.setOnClickListener(this);
        //filterBody = new FilterBody();
        if (mProductListData != null) {
            initTagList();
            addBrandTag(mProductListData.brands);
            addFilterTag(mProductListData.filters);
        }

        if (mDepthTitle != null) {
            if(CustomLog.getFlag())CustomLog.L("initTagLayout","mDepthTitle",mDepthTitle.toString());
            initTagList();
            Iterator<Integer> depthList = mDepthTitle.keySet().iterator();
            int depth = -1;
            while (depthList.hasNext()){
                int i = depthList.next();
                if(i > depth) depth = i;
            }
            Iterator<Integer> idList = mDepthTitle.get(depth).keySet().iterator();
            while (idList.hasNext()) {
                int id = idList.next();
                if(CustomLog.getFlag())CustomLog.L("initTagLayout","mDepthTitle title",mDepthTitle.get(depth).get(id).title);
                addTagTypeFull(mDepthTitle.get(depth).get(id).title, mDepthTitle.get(depth).get(id));
            }
        }

        mTagAdapter.notifyDataSetChanged();
    }

    private void initTagList() {
        // Adapter
        if (mTagAdapter == null) {
            mTagAdapter = new TagListAdapter(getContext());
            mTagAdapter.setOnTagListener((index, tagData)  -> {
                if (tagData != null) {
                    if (tagData instanceof Category) {
                        changeCategoryData((Category) tagData, false);
                        if(CustomLog.getFlag())CustomLog.L("initTagList","index", index,"Category", tagData);
                        mTagAdapter.remove(index);
                        if(mTagAdapter.getItemCount() == 0){
                            resetTagLayout();
                        }else{
                            mTagAdapter.notifyDataSetChanged();
                        }
                        // refresh list

                    } else if (tagData instanceof Brand) {
                        changeBrandData((Brand) tagData, false);
                        if(CustomLog.getFlag())CustomLog.L("initTagList","index", index,"Brand", tagData);
                        mTagAdapter.remove(index);
                        if(mTagAdapter.getItemCount() == 0){
                            resetTagLayout();
                        }else{
                            mTagAdapter.notifyDataSetChanged();
                        }
                        // refresh list

                    } else if (tagData instanceof Attribute) {
                        changeFilterData((Attribute) tagData, false);
                        if(CustomLog.getFlag())CustomLog.L("initTagList","index", index,"Attribute", tagData);
                        mTagAdapter.remove(index);
                        if(mTagAdapter.getItemCount() == 0){
                            resetTagLayout();
                        }else{
                            mTagAdapter.notifyDataSetChanged();
                        }
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
            if(CustomLog.getFlag())CustomLog.L("addCategoryTag","addCategoryTag",categories);
            for (Category c : categories) {
                if (c.isSelected) {
                    addTagTypeFull(c.title, c);
                }
                addCategoryTag(c.children);
            }
        }
    }

    private void addBrandTag(List<Brand> brands) {
        if (brands != null && brands.size() > 0) {
            if(CustomLog.getFlag())CustomLog.L("addBrandTag","addBrandTag",brands);
            for (Brand b : brands) {
                if (b.isSelected) {
                    addTagTypeNormal(b.nameKo, b);
                }
            }
        }
    }

    private void addFilterTag(List<Filter> filters) {
        if (filters != null && filters.size() > 0) {
            if(CustomLog.getFlag())CustomLog.L("addFilterTag","addFilterTag",filters);
            for (Filter f : filters) {
                for (Attribute a : f.attributes) {
                    if (a.selected) {
                        if(TextUtils.isEmpty(a.colorName)) addTagTypeNormal(a.name, a);
                        else addTagTypeNormal(a.colorName, a);
                    }
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
            if(mIsCategory == Type.ProductListViewType.CATEGORY){
                mBinding.layoutHeader.layoutTabParent.setVisibility(View.VISIBLE);
            }else{
                mBinding.layoutHeader.layoutTabParent.setVisibility(View.GONE);
            }
            mBinding.layoutHeaderSub.layoutFilterParent.setVisibility(View.GONE);
            mBinding.layoutHeaderSub.layoutReset.setOnClickListener(null);
            //
            filterBody = null;
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
                    if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("getProductListByCategory mProductListData",mProductListData.toString());
                }
                emptyView("검색결과가 없습니다.");
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
                }
                emptyView("검색결과가 없습니다.");
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
                }
                emptyView("'"+mText+"'에 대한검색결과가 없습니다.");
            }
            mIsLoading = false;
            mLoadingIndicator.hide();
        });
    }

    private void emptyView(String msg){
        if(mListAdapter.getItemCount() == 0){
            mBinding.listContentsEmpty.setVisibility(View.VISIBLE);
            mBinding.listContents.setVisibility(View.GONE);
            String txt = msg;
            mBinding.textviewProductlistEmpty.setText(txt);
        }else{
            mBinding.listContentsEmpty.setVisibility(View.GONE);
            mBinding.listContents.setVisibility(View.VISIBLE);
        }
    }

    // Dialog
    private void showCategoryListDialog() {
        if (((AppCompatActivity)getContext()).getSupportFragmentManager() != null) {
            if (mCategoryListDialog == null) {
                mCategoryListDialog = new CategoryListDialog();
                mCategoryListDialog.setOnCategoryListener(new OnCategoryListener() {
                    @Override
                    public void onEvent(Category category) {
                        CommonUtil.startCategoryScreen(((AppCompatActivity)getContext()), category.type, category.hierarchies, true);
                    }
                });
            }
            mCategoryListDialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "ProductFilterListActivity");
        }
    }

    private void showBrandListDialog() {
        if (((AppCompatActivity)getContext()).getSupportFragmentManager() != null) {
            if (mBrandListDialog == null) {
                mBrandListDialog = new BrandListDialog();
                mBrandListDialog.setOnBrandListener(new OnBrandListener() {
                        @Override
                        public void onEvent(Brand brand) {
                            CommonUtil.startBrandScreen(((AppCompatActivity)getContext()), brand, true);
                        }
                    }
                );
            }
            mBrandListDialog.show(((AppCompatActivity)getContext()).getSupportFragmentManager(), "ProductFilterListActivity");
        }
    }
    ////////////////////////////////////////////////
}