package io.temco.guhada.view.custom.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.databinding.DialogDetailSearchBinding;
import io.temco.guhada.databinding.LayoutDetailSearchTypeBinding;
import io.temco.guhada.view.adapter.BrandListAdapter;
import io.temco.guhada.view.adapter.expand.CategoryDialogExpandFirstListAdapter;
import io.temco.guhada.view.adapter.filter.FilterColorListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextButtonListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class DetailSearchDialog extends BaseDialog<DialogDetailSearchBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private BrandListAdapter mBrandListAdapter;
    private ProductList mProductData;
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

        // Test
        initCategoryList();
        setBrandData(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                dismiss();
                break;

            case R.id.image_alphabet:
                selectBrandInitial(true);
                break;

            case R.id.image_hangul:
                selectBrandInitial(false);
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setProductData(ProductList data) {
        if (data != null) {
            mProductData = data;
            setCategoryData(data.categories);
            setBrandData(data.brands);
            initFilter(data.filters);
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    // Category
    private void setCategoryData(List<Category> data) {
        //
    }

    private void initCategoryList() {
        // mBinding.textCategoryDepth.setText(">>>>");
        // mBinding.imageCategoryExpand.setVisibility(View.GONE);
        mBinding.listCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryDialogExpandFirstListAdapter adapter = new CategoryDialogExpandFirstListAdapter(getContext());
//        adapter.setOnCategoryListener(this::setCategoryData);
        adapter.setItems(Preferences.getCategories());
        mBinding.listCategory.setAdapter(adapter);
    }

    // Brand
    private void setBrandData(List<Brand> data) {
        if (data != null && data.size() > 0) {

        } else {
//            mBinding.layoutExpandBrand.

        }
        initBrandList();
        selectBrandInitial(true);
    }

    private void initBrandList() {
        mBinding.layoutSearch.setClickListener(this);
        // Adapter
        mBrandListAdapter = new BrandListAdapter(getContext(), false);
        mBrandListAdapter.setOnBrandListener(brand -> {
            if (brand != null && brand.type != Type.List.HEADER) {

            }
        });
        mBrandListAdapter.initBrandData(Preferences.getBrands());
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

    private void selectBrandInitial(boolean isAlphabet) {
        if (mBrandListAdapter != null) {
            mBinding.layoutSearch.imageAlphabet.setSelected(isAlphabet);
            mBinding.layoutSearch.imageHangul.setSelected(!isAlphabet);
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

    private boolean setFilterData(int id, List<Attribute> attributes) {
        if (mProductData != null &&
                mProductData.filters != null && mProductData.filters.size() > 0) {
            for (Filter f : mProductData.filters) {
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

    private OnFilterListener mFilterListener = this::setFilterData;

    ////////////////////////////////////////////////
}