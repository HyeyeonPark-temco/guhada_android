package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemSideMenuExpandFirstBinding;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;
import io.temco.guhada.view.adapter.category.SideMenuExpandSecondListAdapter;

public class SideMenuExpandFirstListViewHolder extends BaseCategoryViewHolder<ItemSideMenuExpandFirstBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuExpandFirstListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void init(Context context, Type.CategoryData type, Category data, OnCategoryListener listener) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }
            // Add All
            if (data.children != null && data.children.size() > 0) {
                if (data.children.get(0).type != Type.Category.ALL) {
                    data.children.add(0, CommonUtil.createAllCategoryData(context, data.id, data.hierarchies));
                }
            } else {
                data.children = new ArrayList<>();
                data.children.add(0, CommonUtil.createAllCategoryData(context, data.id, data.hierarchies));
            }
            // Adapter
            SideMenuExpandSecondListAdapter adapter = new SideMenuExpandSecondListAdapter(context);
            adapter.setOnCategoryListener(listener);
            adapter.setItems(data.children);
            mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
            mBinding.listContents.setAdapter(adapter);
        }
    }

    ////////////////////////////////////////////////
}