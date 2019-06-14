package io.temco.guhada.view.holder.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.temco.guhada.databinding.TestExpantionPanelBinding;

public class TestExpandListViewHolder extends RecyclerView.ViewHolder {

    public TestExpantionPanelBinding mBinding;

    public TestExpandListViewHolder(@NonNull View itemView) {
        super(itemView);
        mBinding = TestExpantionPanelBinding.bind(itemView);
    }

    public void bind() {
        mBinding.expansionLayout.collapse(false);
    }
}