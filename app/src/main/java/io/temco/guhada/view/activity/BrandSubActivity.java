package io.temco.guhada.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Info;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ActivityBrandSubBinding;
import io.temco.guhada.databinding.ActivityCategorySubBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.expand.CategorySubExpandFirstListAdapter;

public class BrandSubActivity extends BindActivity<ActivityBrandSubBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
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

    private void initList() {
        List<Brand> data = Preferences.getBrands();


        // List
//        mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
//        CategorySubExpandFirstListAdapter adapter = new CategorySubExpandFirstListAdapter(this);
//        adapter.setChildType(Type.CategoryData.getType(mCategoryData.hierarchies[0]));
//        adapter.setOnCategoryListener(mCategoryListener);
//        adapter.setItems(mCategoryData.children);
//        mBinding.listContents.setAdapter(adapter);
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