package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.databinding.ItemCategorySubExpandFirstBinding;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;
import io.temco.guhada.view.adapter.expand.CategorySubExpandSecondListAdapter;

public class CategorySubExpandFirstListViewHolder extends BaseCategoryViewHolder<ItemCategorySubExpandFirstBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public CategorySubExpandFirstListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    public void init(Context context, CategoryData data, Type.CategoryData type) {
        switch (type) {
            case KIDS: // Grid
                mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 2));
                break;

            default:
                mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                break;
        }
        init(context, data);
    }

    @Override
    public void init(Context context, CategoryData data) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }

            // Sub Menu (Second)
            if (data.children != null && data.children.size() > 0) {
                // Add All
                if (data.children.get(0).type != Type.Category.ALL) {
                    CategoryData all = new CategoryData();
                    all.type = Type.Category.ALL;
                    all.name = context.getString(R.string.category_all);
                    data.children.add(0, all);
                }
                // Adapter
                CategorySubExpandSecondListAdapter adapter = new CategorySubExpandSecondListAdapter(context);
                mBinding.listContents.setAdapter(adapter);
                adapter.setItems(data.children);
            }
        }
    }

    ////////////////////////////////////////////////
}