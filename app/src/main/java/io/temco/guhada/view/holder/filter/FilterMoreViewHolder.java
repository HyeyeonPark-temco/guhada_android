package io.temco.guhada.view.holder.filter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.databinding.ItemDetailSearchTypeColorBinding;
import io.temco.guhada.view.holder.base.BaseFilterViewHolder;

public class FilterMoreViewHolder extends BaseFilterViewHolder<ItemDetailSearchTypeColorBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterMoreViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Attribute data, OnFilterListener listener) {
        //
    }

    ////////////////////////////////////////////////
}