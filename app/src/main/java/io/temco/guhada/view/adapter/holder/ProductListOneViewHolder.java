package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.databinding.ItemProductListOneBinding;
import io.temco.guhada.view.adapter.base.BaseProductListViewHolder;

public class ProductListOneViewHolder extends BaseProductListViewHolder<ItemProductListOneBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListOneViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, ProductList data) {

    }

    ////////////////////////////////////////////////
}
