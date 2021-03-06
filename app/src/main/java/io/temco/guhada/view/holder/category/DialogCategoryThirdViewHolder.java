package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemDialogCategoryThirdBinding;
import io.temco.guhada.view.adapter.category.DialogCategoryFourthListAdapter;
import io.temco.guhada.view.adapter.category.DialogCategoryThirdListAdapter;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class DialogCategoryThirdViewHolder extends BaseCategoryViewHolder<ItemDialogCategoryThirdBinding> {
    public boolean isInit = true;

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DialogCategoryThirdViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListListener listener, OnCategoryHeaderListListener headerListListener) {
        // Type
        if (type != null) {
            switch (type) {
                case KIDS:
                    mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                    break;

                default: // Grid
                    mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 2));
                    break;
            }
        }
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
        DialogCategoryFourthListAdapter adapter = new DialogCategoryFourthListAdapter(context);
        adapter.setOnCategoryListener(listener);
        adapter.setmCategoryHeaderListListener(headerListListener);
        adapter.setItems(data.children);
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
}