package io.temco.guhada.view.holder.mypage;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemOrderShipListBinding;
import io.temco.guhada.view.holder.base.BaseViewHolder;

public class OrderShipViewHolder extends BaseViewHolder<ItemOrderShipListBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public OrderShipViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    public void init(Context context, Category data, OnCategoryListener listener) {
        // Data
        if (data != null) {
            // Title

        }
    }

    ////////////////////////////////////////////////
}