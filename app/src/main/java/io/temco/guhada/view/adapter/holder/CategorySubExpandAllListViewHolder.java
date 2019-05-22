package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.databinding.ItemCategorySubExpandAllBinding;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;

public class CategorySubExpandAllListViewHolder extends BaseCategoryViewHolder<ItemCategorySubExpandAllBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public CategorySubExpandAllListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, CategoryData data, OnCategoryListener listener) {
        // Data
        if (data != null) {
        }
    }

    ////////////////////////////////////////////////
}