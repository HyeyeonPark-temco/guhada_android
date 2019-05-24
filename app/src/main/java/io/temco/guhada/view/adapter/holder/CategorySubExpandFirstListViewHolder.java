package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.florent37.expansionpanel.ExpansionLayout;

import java.util.ArrayList;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
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

    public void init(Context context, CategoryData data, OnCategoryListener listener, Type.CategoryData type) {
        switch (type) {
            case KIDS:
                mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                break;

            default: // Grid
                mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 2));
                break;
        }
        init(context, data, listener);
    }

    @Override
    public void init(Context context, CategoryData data, OnCategoryListener listener) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }
            // Child
            if (data.children == null) {
                mBinding.setExpand(false);
                mBinding.layoutExpandHeader.setToggleOnClick(false);
            } else {
                mBinding.setExpand(true);
                mBinding.layoutExpandHeader.setToggleOnClick(true);
                // Add All
                if (data.children.get(0).type != Type.Category.ALL) {
                    data.children.add(0, createAllCategoryData(context, data.hierarchies));
                }
                // Adapter
                CategorySubExpandSecondListAdapter adapter = new CategorySubExpandSecondListAdapter(context);
                adapter.setOnCategoryListener(listener);
                adapter.setItems(data.children);
                mBinding.listContents.setAdapter(adapter);
            }
        }
    }

    private CategoryData createAllCategoryData(Context context, int[] hierarchies) {
        CategoryData all = new CategoryData();
        all.type = Type.Category.ALL;
        all.name = context.getString(R.string.category_all);
        all.hierarchies = hierarchies;
        return all;
    }

    ////////////////////////////////////////////////
}