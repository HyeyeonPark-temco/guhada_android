package io.temco.guhada.view.holder.mypage;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnOrderShipListener;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.MyOrderItem;
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

    public void init(Context context, MyOrderItem data, OnOrderShipListener listener) {
        // Data
        if (data != null) {
            // Order Number
            mBinding.textOrderNumber.setText(String.valueOf(data.orderProdGroupId));

            // Date
            mBinding.textDate.setText(data.orderDate);

            // Brand
            mBinding.textBrand.setText(data.brandName);

            // Product
            mBinding.textProduct.setText(data.season + " " + data.prodName);

            // Option
            mBinding.textOption.setText(createOptions(data.optionAttribute1, data.optionAttribute2, data.optionAttribute3));

            // Price
            mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.discountPrice.intValue())));

            // Status
            mBinding.textStatus.setText(data.purchaseStatusText);

            // Thumbnail
            // mBinding.imageThumb
        }
    }

    private String createOptions(String... options) {
        if (options != null && options.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (String s : options) {
                if (TextUtils.isEmpty(s)) continue;
                if (sb.length() > 0) {
                    sb.append(", ").append(s);
                } else {
                    sb.append(s);
                }
            }
            return sb.toString();
        }
        return null;
    }

    ////////////////////////////////////////////////
}