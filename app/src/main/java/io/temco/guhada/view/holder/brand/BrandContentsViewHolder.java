package io.temco.guhada.view.holder.brand;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ItemBrandListContentsBinding;
import io.temco.guhada.view.holder.base.BaseBrandViewHolder;

public class BrandContentsViewHolder extends BaseBrandViewHolder<ItemBrandListContentsBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BrandContentsViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Brand data, boolean isAlphabet) {
        if (data != null) {
            mBinding.setTitle(isAlphabet ? data.nameEn : data.nameKo);
        }
    }

    ////////////////////////////////////////////////
}