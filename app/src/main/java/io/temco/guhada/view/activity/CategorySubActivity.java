package io.temco.guhada.view.activity;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.databinding.ActivityCategorySubBinding;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.expand.CategorySubExpandFirstListAdapter;

public class CategorySubActivity extends BindActivity<ActivityCategorySubBinding> {

    // -------- LOCAL VALUE --------
    public static final String INTENT_ID = "id";
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
        if (getIntent() != null) {
            int id = getIntent().getIntExtra(INTENT_ID, 0);
            // CategoryData data = getSearchSecondCategoryData(childId);
            CommonUtil.debug("" + id);

            // Test
            CategoryData data = getSearchSecondCategoryData(13);

            // Set Data
            if (data != null) {
                // Title
                mBinding.layoutHeader.setTitle(data.name);
                // Category
                initList(Type.CategoryData.getType(data.hierarchies[0]), data);
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private CategoryData getSearchSecondCategoryData(int id) {
        List<CategoryData> data = Preferences.getCategoryData();
        if (data != null) {
            for (CategoryData sub : data) {
                if (sub.children != null) {
                    for (CategoryData s : sub.children) {
                        if (s.id == id) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void initList(Type.CategoryData type, CategoryData data) {
        if (data.children != null && data.children.size() > 0) {
            // Category
            mBinding.listContents.setLayoutManager(new LinearLayoutManager(this));
            CategorySubExpandFirstListAdapter adapter = new CategorySubExpandFirstListAdapter(this);
            adapter.setChildType(type);
            adapter.setItems(data.children);
            mBinding.listContents.setAdapter(adapter);
        }
    }

    ////////////////////////////////////////////////
}