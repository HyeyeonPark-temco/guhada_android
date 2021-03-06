package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemDialogCategorySecondBinding;
import io.temco.guhada.view.adapter.category.DialogCategorySecondListAdapter;
import io.temco.guhada.view.adapter.category.DialogCategoryThirdListAdapter;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class DialogCategorySecondViewHolder extends BaseCategoryViewHolder<ItemDialogCategorySecondBinding> {
    public boolean isInit = true;

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DialogCategorySecondViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListListener listener, OnCategoryHeaderListListener headerListListener) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.title)) {
                mBinding.setTitle(data.title);
            }
            // Child
            if (data.children == null) {
                mBinding.setExpand(false);
                mBinding.layoutExpandHeader.setToggleOnClick(false);
            } else {
                mBinding.setExpand(true);
                mBinding.layoutExpandHeader.setToggleOnClick(true);
            }
        }
    }

    public void addChild(Context context, Type.CategoryData type, Category data, OnCategoryListListener listener, OnCategoryHeaderListListener headerListListener){
        // Add All
        if (data.children.get(0).type != Type.Category.ALL) {
            data.children.add(0, CommonUtil.createAllCategoryData(context.getString(R.string.category_all), data.fullDepthName, data.id, data.hierarchies));
        }
        // Adapter
        DialogCategoryThirdListAdapter adapter = new DialogCategoryThirdListAdapter(context);
        adapter.setOnCategoryListener(listener);
        adapter.setmCategoryHeaderListListener(headerListListener);
        adapter.setChildType(type);
        adapter.setItems(data.children);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
}