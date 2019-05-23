package io.temco.guhada.view.adapter.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.CategoryData;

public abstract class BaseCategoryViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseCategoryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, CategoryData data, OnCategoryListener listener);

    ////////////////////////////////////////////////

}