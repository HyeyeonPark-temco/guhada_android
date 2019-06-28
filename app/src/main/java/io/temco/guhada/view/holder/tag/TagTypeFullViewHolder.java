package io.temco.guhada.view.holder.tag;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import io.temco.guhada.data.model.Tag;
import io.temco.guhada.databinding.ItemTagTypeFullBinding;
import io.temco.guhada.view.holder.base.BaseTagViewHolder;

public class TagTypeFullViewHolder extends BaseTagViewHolder<ItemTagTypeFullBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public TagTypeFullViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, int position, Tag data, View.OnClickListener listener) {
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.title)) {
                mBinding.setTitle(data.title);
            }
            // Close
            mBinding.imageClose.setTag(position);
            mBinding.setClickListener(listener);
        }
    }

    ////////////////////////////////////////////////
}