package io.temco.guhada.view.adapter.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.data.model.ProductList;

public abstract class BaseProductListViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseProductListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, ProductList data);

    ////////////////////////////////////////////////

}
