package io.temco.guhada.view.holder.filter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.common.util.ImageUtil;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.databinding.ItemDetailSearchTypeColorBinding;
import io.temco.guhada.view.holder.base.BaseFilterViewHolder;

public class FilterColorViewHolder extends BaseFilterViewHolder<ItemDetailSearchTypeColorBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterColorViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Attribute data, OnFilterListener listener) {
        // Data
        if (data != null) {
            // Color
            if (!TextUtils.isEmpty(data.name)) {
                ImageUtil.setOvalView(context, mBinding.layoutColor);
                mBinding.imageColor.setBackgroundColor(Color.parseColor(data.name));
                if (data.name.toLowerCase().equals(context.getString(R.string.common_color_white))) {
                    mBinding.imageSelected.setBackgroundResource(R.drawable.filter_btn_check_b);
                } else {
                    mBinding.imageSelected.setBackgroundResource(R.drawable.filter_btn_check_w);
                }
            }
            // Selected
            mBinding.imageSelected.setVisibility(data.selected ? View.VISIBLE : View.GONE);
            mBinding.getRoot().setSelected(data.selected);
        }
    }

    ////////////////////////////////////////////////
}