package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemCategoryDialogExpandThirdBinding;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;
import io.temco.guhada.view.adapter.category.CategoryDialogExpandFourthListAdapter;

public class CategoryDialogExpandThirdListViewHolder extends BaseCategoryViewHolder<ItemCategoryDialogExpandThirdBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public CategoryDialogExpandThirdListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListener listener) {
        // Type
        if (type != null) {
            switch (type) {
                case KIDS:
                    mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                    break;

                default: // Grid
                    mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 2));
                    break;
            }
        }
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
                    data.children.add(0, CommonUtil.createAllCategoryData(context, data.id, data.hierarchies));
                }
                // Adapter
                CategoryDialogExpandFourthListAdapter adapter = new CategoryDialogExpandFourthListAdapter(context);
                adapter.setOnCategoryListener(listener);
                adapter.setItems(data.children);
                mBinding.listContents.setAdapter(adapter);
            }
        }
    }

    ////////////////////////////////////////////////
}