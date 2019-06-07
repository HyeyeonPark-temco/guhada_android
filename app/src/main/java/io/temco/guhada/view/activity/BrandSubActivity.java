package io.temco.guhada.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ActivityBrandSubBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.BrandListAdapter;

public class BrandSubActivity extends BindActivity<ActivityBrandSubBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private BrandListAdapter mListAdapter;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return BrandSubActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_brand_sub;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.BRAND;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mBinding.layoutHeader.setClickListener(this);
        mBinding.layoutHeader.setTitle(getString(R.string.common_brand));

        // List
        initList();
        selectInitial(true);

        // EditText
        mBinding.edittextSearch.addTextChangedListener(new TextWatcher() {
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
            case R.id.image_prev:
            case R.id.image_close:
                onBackPressed();
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

    public static void startActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, BrandSubActivity.class), requestCode);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void finishWithData(Brand b) {
        Intent i = new Intent();
        i.putExtra(Info.INTENT_BRAND_DATA, b);
        setResult(RESULT_OK, i);
        onBackPressed();
    }

    private void initList() {
        // Adapter
        mListAdapter = new BrandListAdapter(this);
        mListAdapter.setOnBrandListener(brand -> {
            if (brand != null && brand.type != Type.List.HEADER) {
                finishWithData(brand);
            }
        });
        mListAdapter.initBrandData(Preferences.getBrands());
        // List
        mBinding.listContents.setHasFixedSize(true);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
        mBinding.listContents.setAdapter(mListAdapter);
    }

    private void selectInitial(boolean isAlphabet) {
        if (mListAdapter != null) {
            mBinding.imageAlphabet.setSelected(isAlphabet);
            mBinding.imageHangul.setSelected(!isAlphabet);
            mListAdapter.changeLanguage(isAlphabet);
            mBinding.edittextSearch.setText(null);
        }
    }

    private void setFilter(String text) {
        if (mListAdapter != null) {
            mBinding.listContents.scrollToPosition(0);
            if (TextUtils.isEmpty(text)) {
                mListAdapter.resetFilterToOriginal();
            } else {
                mListAdapter.filter(text);
            }
        }
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}