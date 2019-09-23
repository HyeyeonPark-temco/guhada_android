package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemDetailSearchCategoryFourthBinding;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class DetailSearchCategoryFourthViewHolder extends BaseCategoryViewHolder<ItemDetailSearchCategoryFourthBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategoryFourthViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListListener listener, OnCategoryHeaderListListener headerListListener) {
        // Data
        if (data != null) {
            if(CustomLog.getFlag())CustomLog.L("DetailSearchCategoryFourthViewHolder","data",data.toString());
            // Title
            if (!TextUtils.isEmpty(data.title)) {
                mBinding.setTitle(data.title);
            }
            // Selected
            mBinding.getRoot().setSelected(data.isSelected);
        }
    }

    ////////////////////////////////////////////////
}