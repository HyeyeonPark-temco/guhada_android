package io.temco.guhada.view.holder.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.data.model.Brand;

public abstract class BaseBrandViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseBrandViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, Brand data, boolean isAlphabet);

    ////////////////////////////////////////////////
}