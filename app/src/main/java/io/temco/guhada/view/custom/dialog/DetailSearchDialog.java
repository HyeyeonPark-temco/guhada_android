package io.temco.guhada.view.custom.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnDetailSearchListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.databinding.DialogDetailSearchBinding;
import io.temco.guhada.view.adapter.brand.DetailSearchBrandListAdapter;
import io.temco.guhada.view.adapter.filter.FilterListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class DetailSearchDialog extends BaseDialog<DialogDetailSearchBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDetailSearchListener mDetailSearchListener;
    private DetailSearchBrandListAdapter mBrandListAdapter;
    private boolean mIsChangeData = false;
    private String mParentDepth;
    private List<Category> mCategoryList;
    private List<Brand> mBrandList;
    private List<Brand> mBrandSelectedList;
    private List<Filter> mFilterList;
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
        mBinding.layoutExpandCategoryContents.collapse(true);
        mBinding.layoutExpandBrandContents.collapse(true);
        resetData();
        initData();
    }

    private void initData() {
        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        CommonUtil.delayRunnable(() -> {
            setCategoryData();
            setBrandData();
            initFilter(mFilterList);
            mBinding.layoutProgress.setVisibility(View.GONE);
            mBinding.viewScrollContents.scrollTo(0, 0);
        });
    }

    private void resetData() {
        if (mCategoryList != null && mCategoryList.size() > 0) {
            for (Category c : mCategoryList) {
                c.isSelected = false;
            }
        }
        if (mBrandList != null && mBrandList.size() > 0) {
            for (Brand b : mBrandList) {
                b.isSelected = false;
            }
        }
        if (mFilterList != null && mFilterList.size() > 0) {
            for (Filter c : mFilterList) {
                if (c.attributes != null && c.attributes.size() > 0) {
                    for (Attribute a : c.attributes) {
                        a.selected = false;
                    }
                }
            }
        }
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
                checkSelectedBrandList(mBrandList.get(0));
            } else {
                mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.VISIBLE);
                mBinding.layoutExpandBrandHeader.setToggleOnClick(true);
                //
                initBrandList(mBrandList);
                initSelectedBrandList(mBrandList);
            }
            refreshBrandTitle();
        } else {
            mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.GONE);
            mBinding.layoutExpandBrandHeader.setToggleOnClick(false);
        }
    }

    private void initBrandList(List<Brand> data) {
        mBinding.layoutSearch.setClickListener(this);
        selectBrandLanguage(true);
        // Adapter
        mBrandListAdapter = new DetailSearchBrandListAdapter(getContext());
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
                CommonUtil.delayRunnable(() -> mBrandListAdapter.resetFilterToOriginal());
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
                    checkSelectedBrandList(brand);
                    refreshBrandTitle();
                    return true;
                }
            }
        }
        return false;
    }

    private void initSelectedBrandList(List<Brand> brands) {
        if (brands != null && brands.size() > 0) {
            mBrandSelectedList = new ArrayList<>();
            for (Brand b : brands) {
                if (b.isSelected) {
                    mBrandSelectedList.add(b);
                }
            }
        }
    }

    private void checkSelectedBrandList(Brand brand) {
        if (mBrandSelectedList == null) {
            mBrandSelectedList = new ArrayList<>();
        } else {
            if (mBrandSelectedList.size() > 0) {
                for (Brand b : mBrandSelectedList) {
                    if (b.id == brand.id) {
                        if (!brand.isSelected) {
                            mBrandSelectedList.remove(b);
                        }
                        break;
                    }
                }
            }
        }
        if (brand.isSelected) {
            mBrandSelectedList.add(brand);
        }
    }

    private void refreshBrandTitle() {
        if (mBrandSelectedList != null && mBrandSelectedList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mBrandSelectedList.size(); i++) {
                sb.append(mBrandSelectedList.get(i).nameDefault);
                if (i != mBrandSelectedList.size() - 1) sb.append(", ");
            }
            mBinding.layoutHeaderBrand.setDepth(sb.toString());
        } else {
            mBinding.layoutHeaderBrand.setDepth(null);
        }
    }

    // Filter
    private void initFilter(List<Filter> filters) {
        if (filters != null && filters.size() > 0) {
            FilterListAdapter adapter = new FilterListAdapter(getContext());
            adapter.setOnFilterListener((id, attributes) -> {
                boolean result = changeFilterData(id, attributes);
                if (!mIsChangeData) mIsChangeData = result;
            });
            adapter.setItems(filters);
            mBinding.listFilter.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.listFilter.setAdapter(adapter);
        }
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
}