package io.temco.guhada.view.holder.filter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.databinding.ItemDetailSearchTypeTextButtonBinding;
import io.temco.guhada.view.holder.base.BaseFilterViewHolder;

public class FilterTextButtonViewHolder extends BaseFilterViewHolder<ItemDetailSearchTypeTextButtonBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterTextButtonViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Attribute data, OnFilterListener listener) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }
            // Selected
            mBinding.getRoot().setSelected(data.selected);
        }
    }

    ////////////////////////////////////////////////
}