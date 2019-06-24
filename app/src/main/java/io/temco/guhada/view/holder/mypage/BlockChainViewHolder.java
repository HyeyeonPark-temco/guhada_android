package io.temco.guhada.view.holder.mypage;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.R;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.BlockChain;
import io.temco.guhada.databinding.ItemBlockChainListBinding;
import io.temco.guhada.view.holder.base.BaseViewHolder;

public class BlockChainViewHolder extends BaseViewHolder<ItemBlockChainListBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BlockChainViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    public void init(Context context, int position, BlockChain data, boolean isShowBottomLine) {
        // Data
        if (data != null) {
            // Number
            mBinding.textNumber.setText(String.valueOf(position));

            // Serial ID
            mBinding.textSerialId.setText(data.serialId);

            // Brand
            mBinding.textBrand.setText(data.brandName);

            // Seller
            mBinding.textSeller.setText(data.seller);

            // Product
            mBinding.textProduct.setText(data.productName);

            // Price
            mBinding.textPrice.setText(String.format(context.getString(R.string.product_price), TextUtil.getDecimalFormat(data.price)));

            // Color
            // mBinding.textColor.setText(data.);

            // Certificate
            mBinding.textCertificate.setText(data.certificateName);

            // Link
//            mBinding.imageLink.

            // Line
            mBinding.imageLine.setVisibility(isShowBottomLine ? View.VISIBLE : View.GONE);
        }
    }
}
