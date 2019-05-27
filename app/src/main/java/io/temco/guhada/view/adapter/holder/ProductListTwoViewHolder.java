package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.databinding.ItemProductListTwoBinding;
import io.temco.guhada.view.adapter.base.BaseProductListViewHolder;

public class ProductListTwoViewHolder extends BaseProductListViewHolder<ItemProductListTwoBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListTwoViewHolder(@NonNull View itemView) {
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
