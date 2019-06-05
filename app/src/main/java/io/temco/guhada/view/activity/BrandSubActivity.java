package io.temco.guhada.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    private boolean mIsAlphabet = true;
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
        //
        mBinding.setClickListener(this);
        mBinding.layoutHeader.setClickListener(this);
        mBinding.layoutHeader.setTitle(getString(R.string.common_brand));

        //
        initList();
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

    private List<Brand> testData() {
        List<Brand> data = new ArrayList<>();
        data.addAll(testSection("A"));
        data.addAll(testSection("B"));
        data.addAll(testSection("C"));
        data.addAll(testSection("D"));
        data.addAll(testSection("E"));
        data.addAll(testSection("F"));
        data.addAll(testSection("G"));
        data.addAll(testSection("H"));
        data.addAll(testSection("I"));
        data.addAll(testSection("J"));
        return data;
    }

    private List<Brand> testSection(String title) {
        List<Brand> data = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Brand b = new Brand();
            b.nameEn = title;
            data.add(b);
        }

        return data;
    }


    private void initList() {
        List<Brand> data = Preferences.getBrands();
        // List
        mBinding.listContents.setHasFixedSize(true);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
//        mBinding.listContents.setLayoutManager(new SmoothScrollerWithLinearLayoutManager(this));

        BrandListAdapter adapter = new BrandListAdapter(this);
        mBinding.listContents.setAdapter(adapter);


//        List<Brand> data = testData();

        if (data != null && data.size() > 0) {
            // Sort
            Collections.sort(data, CustomComparator.getBrandComparator(true, mIsAlphabet));
            //
            HashMap<String, Integer> index = new LinkedHashMap<>();


            List<Brand> re = new ArrayList<>();
            List<Brand> side = new ArrayList<>();

            CommonUtil.debug("" + data.size());


            for (int i = 0; i < data.size(); i++) {
                if (mIsAlphabet) {
                    String en = data.get(i).nameEn;
                    if (TextUtils.isEmpty(en)) {
                        continue;
                    }
                    char key = en.toUpperCase().charAt(0);
                    if (!TextSearcher.isAlphabet(key)) {
                        side.add(data.get(i));
                        continue;
                    }
                    String keyString = String.valueOf(key);
                    if (!index.containsKey(keyString)) {
                        // Header
                        Brand h = new Brand();
                        h.type = Type.List.HEADER;
                        h.nameEn = keyString;
                        h.nameKo = data.get(i).nameKo;
                        h.nameDefault = data.get(i).nameDefault;
                        h.layoutRes = R.layout.item_brand_list_header;
                        // Index
                        // index.put(keyString, re.size() - index.size() - 1);
                        index.put(keyString, i);
                        //
                        re.add(h);
                    }
                    re.add(data.get(i));
                } else {
                    String ko = data.get(i).nameKo;
                    if (TextUtils.isEmpty(ko)) {
                        continue;
                    }
                    char key = ko.charAt(0);
                    if (!TextSearcher.isHangul(key)) {
                        side.add(data.get(i));
                        continue;
                    }
                    String keyString = String.valueOf(TextSearcher.getInitialHangulSound(key));
                    if (!index.containsKey(keyString)) {
                        // Header
                        Brand h = new Brand();
                        h.type = Type.List.HEADER;
                        h.nameEn = data.get(i).nameEn;
                        h.nameKo = String.valueOf(key);
                        h.nameDefault = data.get(i).nameDefault;
                        h.layoutRes = R.layout.item_brand_list_header;
                        re.add(h);
                        // Index
                        index.put(keyString, re.size() - 1);
                    }
                    re.add(data.get(i));
                }
            }

            //
            if (side.size() > 0) {
                String s = "#";
                // Side
                Brand h = new Brand();
                h.type = Type.List.HEADER;
                h.nameEn = s;
                h.nameKo = s;
                h.nameDefault = s;
                h.layoutRes = R.layout.item_brand_list_header;
                re.add(h);
                // Index
                index.put(s, re.size());

                //
                re.addAll(re.size(), side);
                CommonUtil.debug("" + side.size());
            }

            //
            adapter.setScrollIndex(index);
            adapter.setItems(re);


            //
            if (re.size() > 0) {
                CommonUtil.debug("" + re.size());
            }


            /*
            if (!index.isEmpty()) {
                Iterator<String> keys = index.keySet().iterator();
                Iterator<Integer> values = index.values().iterator();

                StringBuilder sb = new StringBuilder();

                int c = 0;
                while (keys.hasNext()) {
                    sb.append(keys.next());
                    c++;
                }

                StringBuilder bs = new StringBuilder();
                while (values.hasNext()) {
                    bs.append(values.next()).append(" / ");
                }

                CommonUtil.debug("" + c);
                CommonUtil.debug("" + sb.toString());
                CommonUtil.debug("" + bs.toString());
            }
            */

        }
    }

    private void finishWithData(int id) {
        Intent i = new Intent();
        i.putExtra(Info.INTENT_BRAND_ID, id);
        setResult(RESULT_OK, i);
        onBackPressed();
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}