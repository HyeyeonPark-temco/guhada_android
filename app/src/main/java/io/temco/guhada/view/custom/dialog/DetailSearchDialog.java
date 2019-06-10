package io.temco.guhada.view.custom.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.DialogDetailSearchBinding;
import io.temco.guhada.view.adapter.BrandListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class DetailSearchDialog extends BaseDialog<DialogDetailSearchBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private BrandListAdapter mBrandListAdapter;
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

        // List
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
                selectInitial(true);
                break;

            case R.id.image_hangul:
                selectInitial(false);
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    private void setBrandData(Brand data) {
        initBrandList();
        selectInitial(true);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    // Category
    private void initCategoryList() {
        mBinding.textCategoryDepth.setText(">>>>");
        // mBinding.imageCategoryExpand.setVisibility(View.GONE);
    }

    // Brand
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
                setFilter(s.toString());
            }
        });
    }

    private void selectInitial(boolean isAlphabet) {
        if (mBrandListAdapter != null) {
            mBinding.layoutSearch.imageAlphabet.setSelected(isAlphabet);
            mBinding.layoutSearch.imageHangul.setSelected(!isAlphabet);
            mBrandListAdapter.changeLanguage(isAlphabet);
            mBinding.layoutSearch.edittextSearch.setText(null);
        }
    }

    private void setFilter(String text) {
        if (mBrandListAdapter != null) {
            mBinding.listBrand.scrollToPosition(0);
            if (TextUtils.isEmpty(text)) {
                mBrandListAdapter.resetFilterToOriginal();
            } else {
                mBrandListAdapter.filter(text);
            }
        }
    }

    ////////////////////////////////////////////////
}