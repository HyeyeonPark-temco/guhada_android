package io.temco.guhada.view.holder.brand;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ItemBrandListHeaderBinding;
import io.temco.guhada.view.holder.base.BaseBrandViewHolder;

public class BrandListHeaderViewHolder extends BaseBrandViewHolder<ItemBrandListHeaderBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BrandListHeaderViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Brand data, boolean isAlphabet) {
        if (data != null) {
            mBinding.textTitle.setText(isAlphabet ? data.nameEn : data.nameKo);
        }
    }

    ////////////////////////////////////////////////
}