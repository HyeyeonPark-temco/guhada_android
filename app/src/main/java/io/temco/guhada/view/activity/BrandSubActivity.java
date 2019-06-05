package io.temco.guhada.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomComparator;
import io.temco.guhada.common.util.TextSearcher;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ActivityBrandSubBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.BrandListAdapter;

public class BrandSubActivity extends BindActivity<ActivityBrandSubBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private BrandListAdapter mListAdapter;
    private List<Brand> mBrandList;
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
                if (mListAdapter != null) {
                    if (TextUtils.isEmpty(s)) {
                        mListAdapter.resetFilterToOriginal();
                    } else {
                        mListAdapter.filter(s.toString());
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_all:
//                finishWithData(Type.Category.ALL, mCategoryData.hierarchies);
                break;

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

    private void finishWithData(int id) {
        Intent i = new Intent();
        i.putExtra(Info.INTENT_BRAND_ID, id);
        setResult(RESULT_OK, i);
        onBackPressed();
    }

    private void initList() {
        // Adapter
        mListAdapter = new BrandListAdapter(this);
        // List
        mBinding.listContents.setHasFixedSize(true);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
        mBinding.listContents.setAdapter(mListAdapter);
    }

    private void selectInitial(boolean isAlphabet) {
        mBinding.imageAlphabet.setSelected(isAlphabet);
        mBinding.imageHangul.setSelected(!isAlphabet);
        resetData(isAlphabet);
    }

    private void resetData(boolean isAlphabet) {
        if (mBrandList == null) mBrandList = Preferences.getBrands();
        initBrandData(mBrandList, isAlphabet);
    }

    private void initBrandData(List<Brand> data, boolean isAlphabet) {
        if (mListAdapter != null &&
                data != null && data.size() > 0) {
            // Sort
            Collections.sort(data, CustomComparator.getBrandComparator(true, isAlphabet));
            List<Brand> sort = new ArrayList<>();
            List<Brand> side = new ArrayList<>();
            // Index
            LinkedHashMap<String, Integer> index = new LinkedHashMap<>();
            for (Brand b : data) {
                if (isAlphabet) {
                    setIndexBrandAlphabetData(b, index, sort, side);
                } else {
                    setIndexBrandHangulData(b, index, sort, side);
                }
            }
            // Side (#)
            if (side.size() > 0) {
                String s = "#";
                sort.add(createBrandHeaderData(s)); // Header
                index.put(s, sort.size()); // Index
                sort.addAll(sort.size(), side); // Row
            }
            // Adapter
            mBinding.listContents.scrollToPosition(0);
            mListAdapter.changeLanguage(isAlphabet);
            mListAdapter.setOriginalItems(sort);
            mListAdapter.setScrollIndex(index);
        }
    }

    private void setIndexBrandAlphabetData(Brand data,
                                           LinkedHashMap<String, Integer> index,
                                           List<Brand> sort,
                                           List<Brand> side) {
        if (TextUtils.isEmpty(data.nameEn)) {
            return;
        }
        char key = data.nameEn.toUpperCase().charAt(0);
        if (!TextSearcher.isAlphabet(key)) {
            side.add(data);
            return;
        }
        String keyString = String.valueOf(key);
        if (!index.containsKey(keyString)) {
            sort.add(createBrandHeaderData(keyString)); // Header
            index.put(keyString, sort.size()); // Index
        }
        sort.add(data); // Row
    }

    private void setIndexBrandHangulData(Brand data,
                                         LinkedHashMap<String, Integer> index,
                                         List<Brand> sort,
                                         List<Brand> side) {
        if (TextUtils.isEmpty(data.nameKo)) {
            return;
        }
        char key = data.nameKo.charAt(0);
        if (!TextSearcher.isHangul(key)) {
            side.add(data);
            return;
        }
        String keyString = String.valueOf(TextSearcher.getInitialHangulSound(key));
        if (!index.containsKey(keyString)) {
            sort.add(createBrandHeaderData(keyString)); // Header
            index.put(keyString, sort.size()); // Index
        }
        sort.add(data); // Row
    }

    private Brand createBrandHeaderData(String title) {
        Brand b = new Brand();
        b.type = Type.List.HEADER;
        b.nameEn = title;
        b.nameKo = title;
        b.nameDefault = title;
        b.layoutRes = R.layout.item_brand_list_header;
        return b;
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}