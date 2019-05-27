package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemSideMenuExpandSecondBinding;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;

public class SideMenuExpandSecondListViewHolder extends BaseCategoryViewHolder<ItemSideMenuExpandSecondBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuExpandSecondListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Category data, OnCategoryListener listener) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }
        }
    }

    ////////////////////////////////////////////////
}