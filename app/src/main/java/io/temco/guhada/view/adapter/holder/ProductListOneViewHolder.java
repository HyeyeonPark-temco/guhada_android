package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.common.util.TextUtil;
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

            // Option
            if (mBinding.layoutColor.getChildCount() > 0) {
                mBinding.layoutColor.removeAllViews();
            }
            if (data.options != null && data.options.size() > 0) {
                for (Deal.Option o : data.options) {
                    switch (Type.ProductOption.getType(o.type)) {
                        case COLOR:
                            addColor(context, 5, o.attributes); // 5 Units
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

    private void addColor(Context context, int unit, String[] colors) {
        if (colors != null && colors.length > 0) {
            int point = colors.length / unit;
            int re = colors.length % unit;

            String[] s;
            if (point > 0) {
                for (int i = 0; i < point; i++) {
                    s = new String[unit];
                    System.arraycopy(colors, unit * i, s, 0, s.length);
                    mBinding.layoutColor.addView(createColorLayout(context, s));
                }
            }
            if (re > 0) {
                s = new String[re];
                System.arraycopy(colors, unit * point, s, 0, s.length);
                mBinding.layoutColor.addView(createColorLayout(context, s));
            }
        }
    }

    private LinearLayout createColorLayout(Context context, String[] colors) {
        if (colors != null && colors.length > 0) {
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            for (String c : colors) {
                layout.addView(createColorView(context, c));
            }
            return layout;
        }
        return null;
    }

    private View createColorView(Context context, String color) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_color_dot, null);
        ImageUtil.setOvalView(context, v.findViewById(R.id.layout_color));
        v.findViewById(R.id.image_color).setBackgroundColor(Color.parseColor(color));
        return v;
    }

    private void addText(Context context, String[] texts) {
        // Not..
//        if (texts != null && texts.length > 0) {
//            for (String t : texts) {
//            }
//        }
    }

    ////////////////////////////////////////////////
}