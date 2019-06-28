package io.temco.guhada.view.holder.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import io.temco.guhada.data.model.Tag;

public abstract class BaseTagViewHolder<B extends ViewDataBinding> extends BaseViewHolder<B> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseTagViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void init(Context context, int position, Tag data, View.OnClickListener listener);

    ////////////////////////////////////////////////
}