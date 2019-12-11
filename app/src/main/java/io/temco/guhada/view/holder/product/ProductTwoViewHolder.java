package io.temco.guhada.view.holder.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonViewUtil;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.Deal;
import io.temco.guhada.databinding.ItemProductListTwoBinding;
import io.temco.guhada.view.holder.base.BaseProductViewHolder;

public class ProductTwoViewHolder extends BaseProductViewHolder<ItemProductListTwoBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    int width = 0;
    int margin = 0;

    public ProductTwoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @SuppressLint("StringFormatMatches")
    @Override
    public void init(Context context, RequestManager manager, Deal data, int position) {
        if (data != null) {
            if(width == 0){
                DisplayMetrics matrix = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(matrix);
                width = (matrix.widthPixels - CommonViewUtil.dipToPixel(context, 24)) / 2;
                margin = CommonViewUtil.dipToPixel(context, 10);
            }

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, width);
            if(position%2==0){
                param.leftMargin = 0;
                param.rightMargin = margin;
            }else{
                param.leftMargin = 0;
                param.rightMargin = 0;
            }
            mBinding.relativeImageLayout.setLayoutParams(param);

            RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(width,width);
            mBinding.imageThumb.setLayoutParams(imageParams);

            // Brand
            mBinding.textBrand.setText(data.brandName);

            // Season
            mBinding.textSeason.setText(data.productSeason);

            // Title
            mBinding.textTitle.setText(data.productName);

            // Thumbnail
            ImageUtil.loadImage(manager, mBinding.imageThumb, data.productImage.getUrl());

            /*if(data.isBoldName){
                mBinding.textTitle.setTypeface(null, Typeface.BOLD);
            }else{
                mBinding.textTitle.setTypeface(null, Typeface.NORMAL);
            }*/

            // Option
            if (mBinding.layoutColor.getChildCount() > 0) {
                mBinding.layoutColor.removeAllViews();
            }
            if (data.options != null && data.options.size() > 0) {
                for (Deal.Option o : data.options) {
                    switch (Type.ProductOption.getType(o.type)) {
                        case RGB:
                        case COLOR:
                            addColor(context, mBinding.layoutColor, 5, o.attributes); // 5 Units
                            break;
                        case TEXT:
                            addText(context, o.attributes);
                            break;
                    }
                }
            }

            mBinding.textSellerName.setText(data.sellerName);

            // Price
            if (data.discountRate > 0) {
                mBinding.textPrice.setText(TextUtil.getDecimalFormat(data.discountPrice.intValue()));
                mBinding.textPriceSalePer.setVisibility(View.VISIBLE);
                mBinding.textPriceSalePer.setText(String.format(context.getString(R.string.product_price_sale_per), data.discountRate));
                mBinding.textPricediscount.setVisibility(View.VISIBLE);
                mBinding.textPricediscount.setPaintFlags(mBinding.textPricediscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mBinding.textPricediscount.setText(TextUtil.getDecimalFormat(data.sellPrice.intValue()));
            } else {
                mBinding.textPrice.setText(TextUtil.getDecimalFormat(data.sellPrice.intValue()));
                mBinding.textPriceSalePer.setVisibility(View.GONE);
                mBinding.textPricediscount.setVisibility(View.GONE);
            }
            /*if (data.setDiscount) {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.intValue())));
                mBinding.textPriceSalePer.setText(String.format(context.getString(R.string.product_price_sale_per), data.discountRate));
            } else {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.intValue())));
            }*/

            // Ship
            mBinding.textShipFree.setVisibility(data.freeShipping ? View.VISIBLE : View.GONE);

            /**
             *  중고상품, 해외배송 여부 > data binding으로 처리
             *  @author Hyeyeon Park
             *  @since 2019.12.06
             */
            mBinding.setItem(data);

            mBinding.executePendingBindings();
        }
    }

    ////////////////////////////////////////////////
}
