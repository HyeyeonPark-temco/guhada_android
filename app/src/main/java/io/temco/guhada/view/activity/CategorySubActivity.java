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
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ActivityCategorySubBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.expand.CategorySubExpandFirstListAdapter;

public class CategorySubActivity extends BindActivity<ActivityCategorySubBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Category mCategoryData;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return CategorySubActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category_sub;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.CATEGORY;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mBinding.layoutHeader.setClickListener(this);

        // Data
        if (getIntent() != null) {
            getSearchSecondCategoryData(getIntent().getIntExtra(Info.INTENT_CATEGORY_ID, 0));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_all:
                finishWithData(Type.Category.ALL, mCategoryData.hierarchies);
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

    public static void startActivity(Context context, int id) {
        Intent i = new Intent(context, CategorySubActivity.class);
        i.putExtra(Info.INTENT_CATEGORY_ID, id);
        context.startActivity(i);
    }

    public static void startActivityForResult(Activity activity, int id, int requestCode) {
        Intent i = new Intent(activity, CategorySubActivity.class);
        i.putExtra(Info.INTENT_CATEGORY_ID, id);
        activity.startActivityForResult(i, requestCode);
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void getSearchSecondCategoryData(int id) {
        List<Category> data = Preferences.getCategories();
        if (data != null) {
            for (Category sub : data) {
                if (sub.children != null) {
                    for (Category s : sub.children) {
                        if (s.id == id) {
                            mCategoryData = s;
                            initList();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void initList() {
        // Title
        mBinding.layoutHeader.setTitle(mCategoryData.name);
        // List
        if (mCategoryData.children != null && mCategoryData.children.size() > 0) {
            // Category
            mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
            CategorySubExpandFirstListAdapter adapter = new CategorySubExpandFirstListAdapter(this);
            adapter.setChildType(Type.CategoryData.getType(mCategoryData.hierarchies[0]));
            adapter.setOnCategoryListener(mCategoryListener);
            adapter.setItems(mCategoryData.children);
            mBinding.listContents.setAdapter(adapter);
        }
    }

    private void finishWithData(Type.Category type, int[] hierarchies) {
        Intent i = new Intent();
        i.putExtra(Info.INTENT_CATEGORY_TYPE, type);
        i.putExtra(Info.INTENT_CATEGORY_HIERARCHIES, hierarchies);
        setResult(RESULT_OK, i);
        onBackPressed();
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    private OnCategoryListener mCategoryListener = this::finishWithData;

    ////////////////////////////////////////////////
}