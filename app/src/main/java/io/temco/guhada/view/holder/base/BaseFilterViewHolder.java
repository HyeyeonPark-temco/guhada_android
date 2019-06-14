package io.temco.guhada.view.holder.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;

public abstract class BaseFilterViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseFilterViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, Attribute data, OnFilterListener listener);

    ////////////////////////////////////////////////
}