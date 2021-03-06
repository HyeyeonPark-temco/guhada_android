package io.temco.guhada.view.holder.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.CommonViewUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.Deal;
import io.temco.guhada.databinding.ItemProductListThreeBinding;
import io.temco.guhada.view.holder.base.BaseProductViewHolder;

public class ProductThreeViewHolder extends BaseProductViewHolder<ItemProductListThreeBinding> {

    public int width = 0;
    public int margin = 0;
    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductThreeViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @SuppressLint("StringFormatMatches")
    @Override
    public void init(Context context, RequestManager manager, Deal data, int position) {
        if (data != null) {
            if(position == 0 || position == 1 || position == 2){
                mBinding.paddingTop.setVisibility(View.VISIBLE);
            }else mBinding.paddingTop.setVisibility(View.GONE);

            if(width == 0){
                DisplayMetrics matrix = new DisplayMetrics();
                ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(matrix);
                width = (matrix.widthPixels - CommonViewUtil.dipToPixel(context, 30)) / 3;
                margin = CommonViewUtil.dipToPixel(context, 6);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,width);
            if(position == 0){
                params.leftMargin=0;
                params.rightMargin=margin;
            }else{
                if(position % 3 == 0){
                    params.leftMargin=0;
                    params.rightMargin=margin;
                }else if(position % 3 == 1){
                    params.leftMargin=0;
                    params.rightMargin=0;
                }else if(position % 3 == 2){
                    params.leftMargin=margin;
                    params.rightMargin=margin;
                }
            }
            mBinding.layoutImage.setLayoutParams(params);

            LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(width,width);
            mBinding.layoutImage.setLayoutParams(imageParams);
            // Thumbnail
            ImageUtil.loadImage(manager, mBinding.imageThumb, data.productImage.getUrl());

            // Brand
            mBinding.textBrand.setText(data.brandName);

            // Season
            mBinding.textSeason.setText(data.productSeason);

            // Title
             mBinding.textTitle.setText(data.dealName);
            if(data.isBoldName){
                mBinding.textTitle.setTypeface(null, Typeface.BOLD);
            }else{
                mBinding.textTitle.setTypeface(null, Typeface.NORMAL);
            }

            // Size
            // Empty...

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

            // Price
            if (data.discountRate > 0) {
                mBinding.textPrice.setText(TextUtil.getDecimalFormat(data.discountPrice.intValue()));
            } else {
                mBinding.textPrice.setText(TextUtil.getDecimalFormat(data.sellPrice.intValue()));
            }
            /*if (data.setDiscount) {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.intValue())));
                // mBinding.textPriceSalePer.setText(String.format(context.getString(R.string.product_price_sale_per), data.discountRate));
            } else {
                mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.sellPrice.intValue())));
            }*/

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
