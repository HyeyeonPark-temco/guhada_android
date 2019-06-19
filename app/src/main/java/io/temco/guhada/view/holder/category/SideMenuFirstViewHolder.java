package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemSideMenuCategoryFirstBinding;
import io.temco.guhada.view.adapter.category.SideMenuCategorySecondListAdapter;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class SideMenuFirstViewHolder extends BaseCategoryViewHolder<ItemSideMenuCategoryFirstBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuFirstViewHolder(@NonNull View itemView) {
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
                    data.children.add(0, CommonUtil.createAllCategoryData(context.getString(R.string.category_all), data.fullDepthName, data.id, data.hierarchies));
                }
            } else {
                data.children = new ArrayList<>();
                data.children.add(0, CommonUtil.createAllCategoryData(context.getString(R.string.category_all), data.fullDepthName, data.id, data.hierarchies));
            }
            // Adapter
            SideMenuCategorySecondListAdapter adapter = new SideMenuCategorySecondListAdapter(context);
            adapter.setOnCategoryListener(listener);
            adapter.setItems(data.children);
            mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
            mBinding.listContents.setAdapter(adapter);
        }
    }

    ////////////////////////////////////////////////
}