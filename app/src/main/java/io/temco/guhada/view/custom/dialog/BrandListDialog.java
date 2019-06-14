package io.temco.guhada.view.custom.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnBrandListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.DialogBrandListBinding;
import io.temco.guhada.view.adapter.brand.BrandListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class BrandListDialog extends BaseDialog<DialogBrandListBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnBrandListener mListener;
    private BrandListAdapter mListAdapter;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_brand_list;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mBinding.layoutSearch.setClickListener(this);

        // List
        CommonUtil.delayRunnable(this::initList);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                dismiss();
                break;

            case R.id.image_alphabet:
                changeLanguage(true);
                break;

            case R.id.image_hangul:
                changeLanguage(false);
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnBrandListener(OnBrandListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void dissmissWithData(Brand b) {
        if (mListener != null) {
            mListener.onEvent(b);
        }
        dismiss();
    }

    private void initList() {
        selectLanguage(true);
        // Adapter
        mListAdapter = new BrandListAdapter(getContext(), true);
        mListAdapter.setOnBrandListener(brand -> {
            if (brand != null && brand.type != Type.List.HEADER) {
                dissmissWithData(brand);
            }
        });
        mListAdapter.initBrandData(Preferences.getBrands());
        // List
        mBinding.listContents.setHasFixedSize(true);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.listContents.setAdapter(mListAdapter);
    }

    private void changeLanguage(boolean isAlphabet) {
        if (mListAdapter != null) {
            selectLanguage(isAlphabet);
            mListAdapter.changeLanguage(isAlphabet);
            mBinding.layoutSearch.edittextSearch.setText(null);
        }
    }

    private void setFilter(String text) {
        if (mListAdapter != null) {
            mBinding.listContents.scrollToPosition(0);
            if (TextUtils.isEmpty(text)) {
                CommonUtil.delayRunnable(() -> mListAdapter.resetFilterToOriginal());
            } else {
                mListAdapter.filter(text);
            }
        }
    }

    private void selectLanguage(boolean isAlphabet) {
        mBinding.layoutSearch.imageAlphabet.setSelected(isAlphabet);
        mBinding.layoutSearch.imageHangul.setSelected(!isAlphabet);
    }

    ////////////////////////////////////////////////
}