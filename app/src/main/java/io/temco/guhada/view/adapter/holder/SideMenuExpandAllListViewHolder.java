package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.databinding.ItemSideMenuExpandAllBinding;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;

public class SideMenuExpandAllListViewHolder extends BaseCategoryViewHolder<ItemSideMenuExpandAllBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuExpandAllListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, CategoryData data) {
        // Data
        if (data != null) {
        }
    }

    ////////////////////////////////////////////////
}