package io.temco.guhada.view.holder.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.data.model.Category;

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

    public abstract void init(Context context, Type.CategoryData type, Category data, OnCategoryListListener listener, OnCategoryHeaderListListener headerListListener);

    ////////////////////////////////////////////////
}