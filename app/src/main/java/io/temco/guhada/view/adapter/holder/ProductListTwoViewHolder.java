package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.Deal;
import io.temco.guhada.databinding.ItemProductListTwoBinding;
import io.temco.guhada.view.adapter.base.BaseProductViewHolder;

public class ProductListTwoViewHolder extends BaseProductViewHolder<ItemProductListTwoBinding> {

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

            // Option
            if (mBinding.layoutColor.getChildCount() > 0) {
                mBinding.layoutColor.removeAllViews();
            }
            if (data.options != null && data.options.size() > 0) {
                for (Deal.Option o : data.options) {
                    switch (Type.ProductOption.getType(o.type)) {
                        case COLOR:
                            addColor(context, mBinding.layoutColor, 5, o.attributes); // 5 Units
                            break;

                        case TEXT:
                            addText(context, o.attributes);
                            break;
                    }
                }
            }

            // Price
            if (data.setDiscount) {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.intValue())));
                mBinding.textPriceSalePer.setText(String.format(context.getString(R.string.product_price_sale_per), data.discountRate));
            } else {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.intValue())));
            }

            // Ship
            // mBinding.textShipFree.setVisibility(View.VISIBLE);
        }
    }

    ////////////////////////////////////////////////
}
