package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemSubCategoryAllBinding;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class SubCategoryAllViewHolder extends BaseCategoryViewHolder<ItemSubCategoryAllBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SubCategoryAllViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListener listener) {
        // Data
        if (data != null) {
        }
    }

    ////////////////////////////////////////////////
}