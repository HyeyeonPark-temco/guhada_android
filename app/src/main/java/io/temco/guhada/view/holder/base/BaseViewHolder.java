package io.temco.guhada.view.holder.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<B extends ViewDataBinding> extends RecyclerView.ViewHolder {

    // -------- LOCAL VALUE --------
    protected B mBinding;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        mBinding = DataBindingUtil.bind(itemView);
    }

    public B getBinding() {
        return mBinding;
    }

    ////////////////////////////////////////////////
}
