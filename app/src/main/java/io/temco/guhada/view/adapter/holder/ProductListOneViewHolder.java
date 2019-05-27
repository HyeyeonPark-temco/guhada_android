package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.R;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.data.model.Deal;
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
    public void init(Context context, RequestManager manager, Deal data) {
        if (data != null) {
            // Thumbnail
            ImageUtil.loadImage(manager, mBinding.imageThumb, data.imageUrl);

            // Brand
            mBinding.textBrand.setText(data.brandName);

            // Season
            mBinding.textSeason.setText(data.productSeason);

            // Title
            mBinding.textTitle.setText(data.dealName);

            // Size
            // Empty...

            // Color

            // Price
            if (data.setDiscount) {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), data.discountPrice.intValue()));
                mBinding.textPriceSalePer.setText(String.format(context.getString(R.string.product_price_sale_per), data.discountRate));
            } else {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), data.sellPrice.intValue()));
            }

            // Ship
            // mBinding.textShipFree.setVisibility(View.VISIBLE);
        }
    }

    ////////////////////////////////////////////////
}
