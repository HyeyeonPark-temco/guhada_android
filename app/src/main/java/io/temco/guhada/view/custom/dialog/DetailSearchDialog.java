package io.temco.guhada.view.custom.dialog;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnDetailSearchListener;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.databinding.DialogDetailSearchBinding;
import io.temco.guhada.databinding.LayoutDetailSearchTypeBinding;
import io.temco.guhada.view.adapter.BrandListAdapter;
import io.temco.guhada.view.adapter.base.BaseFilterListAdapter;
import io.temco.guhada.view.adapter.filter.FilterColorListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextButtonListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class DetailSearchDialog extends BaseDialog<DialogDetailSearchBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDetailSearchListener mDetailSearchListener;
    private BrandListAdapter mBrandListAdapter;
    private boolean mIsChangeData = false;
    private String mParentDepth;
    private List<Category> mCategoryList;
    private List<Brand> mBrandList;
    private List<Filter> mFilterList;
    // private List<BaseFilterListAdapter> mFilterAdapterList;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_detail_search;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);

        // Data
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                dismiss();
                break;

            ////////////////////////////////////////////////
            // Brand
            case R.id.image_alphabet:
                changeBrandLanguage(true);
                break;

            case R.id.image_hangul:
                changeBrandLanguage(false);
                break;

            // Bottom
            case R.id.layout_reset:
                reset();
                break;

            case R.id.text_result:
                changeDataEvent();
                dismiss();
                break;

            ////////////////////////////////////////////////
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnDetailSearchListener(OnDetailSearchListener listener) {
        mDetailSearchListener = listener;
    }

    public void setCategoryData(String depth, List<Category> categories) {
        mParentDepth = depth;
        mCategoryList = categories;
    }

    public void setBrandData(List<Brand> brands) {
        mBrandList = brands;
    }

    public void setFilterData(List<Filter> filters) {
        mFilterList = filters;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void reset() {
        mBinding.layoutFilter.removeAllViews();
        mBinding.layoutExpandCategoryContents.collapse(true);
        mBinding.layoutExpandBrandContents.collapse(true);
        initData();
    }

    private void initData() {
        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            setCategoryData();
            setBrandData();
            initFilter(mFilterList);
            mBinding.layoutProgress.setVisibility(View.GONE);
        }, 200);
    }

    ////////////////////////////////////////////////

    private void changeDataEvent() {
        if (mIsChangeData && mDetailSearchListener != null) {
            if (mCategoryList != null) mDetailSearchListener.onCategory(mCategoryList);
            if (mBrandList != null) mDetailSearchListener.onBrand(mBrandList);
            if (mFilterList != null) mDetailSearchListener.onFilter(mFilterList);
            mDetailSearchListener.onChange(true);
        }
    }

    // Category
    private void setCategoryData() {
        // Depth
        if (!TextUtils.isEmpty(mParentDepth)) {
            mBinding.layoutHeaderCategory.setDepth(mParentDepth);
        }
        // List
        if (mCategoryList != null && mCategoryList.size() > 0) {
            mBinding.layoutHeaderCategory.imageExpand.setVisibility(View.VISIBLE);
            mBinding.layoutExpandCategoryHeader.setToggleOnClick(true);
            initCategoryList(mCategoryList);
        } else {
            mBinding.layoutHeaderCategory.imageExpand.setVisibility(View.GONE);
            mBinding.layoutExpandCategoryHeader.setToggleOnClick(false);
        }
    }

    private void initCategoryList(List<Category> data) {
        mBinding.listCategory.setLayoutManager(new LinearLayoutManager(getContext()));
//        CategoryDialogExpandFirstListAdapter adapter = new CategoryDialogExpandFirstListAdapter(getContext());
//        adapter.setOnCategoryListener((type, hierarchies) -> {
//            //
//        });
//        adapter.setItems(data);
//        mBinding.listCategory.setAdapter(adapter);
    }

    // Brand
    private void setBrandData() {
        if (mBrandList != null && mBrandList.size() > 0) {
            if (mBrandList.size() == 1) {
                mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.GONE);
                mBinding.layoutExpandBrandHeader.setToggleOnClick(false);
                // Set Title

            } else {
                mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.VISIBLE);
                mBinding.layoutExpandBrandHeader.setToggleOnClick(true);
                //
                initBrandList(mBrandList);
            }
        } else {
            mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.GONE);
            mBinding.layoutExpandBrandHeader.setToggleOnClick(false);
        }
    }

    private void initBrandList(List<Brand> data) {
        mBinding.layoutSearch.setClickListener(this);
        selectBrandLanguage(true);
        // Adapter
        mBrandListAdapter = new BrandListAdapter(getContext(), false);
        mBrandListAdapter.setOnBrandListener(brand -> {
            if (brand != null && brand.type != Type.List.HEADER) {
                boolean result = changeBrandData(brand);
                if (!mIsChangeData) mIsChangeData = result;
            }
        });
        mBrandListAdapter.initBrandData(data);
        // List
        mBinding.listBrand.setHasFixedSize(true);
        mBinding.listBrand.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.listBrand.setAdapter(mBrandListAdapter);
        // EditText
        mBinding.layoutSearch.edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setBrandFilter(s.toString());
            }
        });
    }

    private void changeBrandLanguage(boolean isAlphabet) {
        if (mBrandListAdapter != null) {
            selectBrandLanguage(isAlphabet);
            mBrandListAdapter.changeLanguage(isAlphabet);
            mBinding.layoutSearch.edittextSearch.setText(null);
        }
    }

    private void setBrandFilter(String text) {
        if (mBrandListAdapter != null) {
            mBinding.listBrand.scrollToPosition(0);
            if (TextUtils.isEmpty(text)) {
                mBrandListAdapter.resetFilterToOriginal();
            } else {
                mBrandListAdapter.filter(text);
            }
        }
    }

    private void selectBrandLanguage(boolean isAlphabet) {
        mBinding.layoutSearch.imageAlphabet.setSelected(isAlphabet);
        mBinding.layoutSearch.imageHangul.setSelected(!isAlphabet);
    }

    private boolean changeBrandData(Brand brand) {
        if (mBrandList != null && mBrandList.size() > 0) {
            for (Brand b : mBrandList) {
                if (b.id == brand.id) {
                    b.isSelected = brand.isSelected;
                    return true;
                }
            }
        }
        return false;
    }

    // Filter
    private void initFilter(List<Filter> filters) {
        if (filters != null && filters.size() > 0) {
            for (Filter f : filters) {
                if (!TextUtils.isEmpty(f.viewType)) {
                    switch (Type.ProductOption.getType(f.viewType)) {
                        case COLOR:
                            mBinding.layoutFilter.addView(createColorFilterView(f.id, f.name, f.attributes));
                            break;

                        case TEXT_BUTTON:
                            mBinding.layoutFilter.addView(createTextButtonFilterView(f.id, f.name, f.attributes));
                            break;

                        case TEXT:
                            mBinding.layoutFilter.addView(createTextFilterView(f.id, f.name, f.attributes));
                            break;
                    }
                }
            }
        }
    }

    private View createColorFilterView(int id, String title, List<Attribute> attributes) {
        LayoutDetailSearchTypeBinding b = LayoutDetailSearchTypeBinding.inflate(LayoutInflater.from(getContext()));
        // Title
        b.setTitle(title);
        // Data
        b.listContents.setLayoutManager(new GridLayoutManager(getContext(), 7));
        FilterColorListAdapter adapter = new FilterColorListAdapter(getContext());
        adapter.setOnFilterListener(mFilterListener);
        adapter.setItems(id, attributes);
        b.listContents.setAdapter(adapter);
        return b.getRoot();
    }

    private View createTextButtonFilterView(int id, String title, List<Attribute> attributes) {
        LayoutDetailSearchTypeBinding b = LayoutDetailSearchTypeBinding.inflate(LayoutInflater.from(getContext()));
        // Title
        b.setTitle(title);
        // Data
        b.listContents.setLayoutManager(new GridLayoutManager(getContext(), 2));
        FilterTextButtonListAdapter adapter = new FilterTextButtonListAdapter(getContext());
        adapter.setOnFilterListener(mFilterListener);
        adapter.setItems(id, attributes);
        b.listContents.setAdapter(adapter);
        return b.getRoot();
    }

    private View createTextFilterView(int id, String title, List<Attribute> attributes) {
        LayoutDetailSearchTypeBinding b = LayoutDetailSearchTypeBinding.inflate(LayoutInflater.from(getContext()));
        // Title
        b.setTitle(title);
        // Data
        b.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        FilterTextListAdapter adapter = new FilterTextListAdapter(getContext());
        adapter.setOnFilterListener(mFilterListener);
        adapter.setItems(id, attributes);
        b.listContents.setAdapter(adapter);
        return b.getRoot();
    }

    private boolean changeFilterData(int id, List<Attribute> attributes) {
        if (mFilterList != null && mFilterList.size() > 0) {
            for (Filter f : mFilterList) {
                if (f.id == id) {
                    f.attributes = attributes;
                    return true;
                }
            }
        }
        return false;
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    private OnFilterListener mFilterListener = (id, attributes) -> {
        boolean result = changeFilterData(id, attributes);
        if (!mIsChangeData) mIsChangeData = result;
    };

    ////////////////////////////////////////////////
}