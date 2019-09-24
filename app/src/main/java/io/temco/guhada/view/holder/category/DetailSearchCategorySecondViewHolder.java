package io.temco.guhada.view.holder.category;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.florent37.expansionpanel.ExpansionLayout;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.ItemDetailSearchCategorySecondBinding;
import io.temco.guhada.view.adapter.category.DetailSearchCategoryThirdListAdapter;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public class DetailSearchCategorySecondViewHolder extends BaseCategoryViewHolder<ItemDetailSearchCategorySecondBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategorySecondViewHolder(@NonNull View itemView) {
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
                mBinding.getRoot().setSelected(data.isSelected);
                mBinding.layoutExpandHeader.setToggleOnClick(false);
            } else {
                mBinding.setExpand(true);
                mBinding.layoutExpandHeader.setToggleOnClick(true);
                // Add All
                if (data.children.get(0).type != Type.Category.ALL) {
                    data.children.add(0, CommonUtil.createAllCategoryData(context.getString(R.string.category_all), data.fullDepthName, data.id, data.hierarchies,data.isSelected,data.id));
                }
                // Adapter
                DetailSearchCategoryThirdListAdapter adapter = new DetailSearchCategoryThirdListAdapter(context);
                adapter.setOnCategoryListener(listener);
                adapter.setmCategoryHeaderListListener(headerListListener);
                adapter.setItems(data.children);
                mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                mBinding.listContents.setAdapter(adapter);
                // Expand
                if (data.isSelected) {
                    headerListListener.onEvent(1,data);
                    mBinding.layoutExpandContents.expand(true);
                }
            }
        }
    }

    ////////////////////////////////////////////////
}