package io.temco.guhada.view.adapter.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.temco.guhada.databinding.ItemListTestBinding;

public class TestListViewHolder extends RecyclerView.ViewHolder {

    public ItemListTestBinding mBinding;

    public TestListViewHolder(@NonNull View itemView) {
        super(itemView);
        mBinding = ItemListTestBinding.bind(itemView);
    }
}