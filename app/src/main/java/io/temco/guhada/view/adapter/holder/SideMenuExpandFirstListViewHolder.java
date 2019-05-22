package io.temco.guhada.view.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.databinding.ItemSideMenuExpandFirstBinding;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;
import io.temco.guhada.view.adapter.expand.SideMenuExpandSecondListAdapter;

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
    public void init(Context context, CategoryData data) {
        // Data
        if (data != null) {
            // Title
            if (!TextUtils.isEmpty(data.name)) {
                mBinding.setTitle(data.name);
            }

            // Sub Menu (Second)
            if (data.children != null && data.children.size() > 0) {
                // Add All
                CategoryData all = new CategoryData();
                all.type = Type.Category.ALL;
                all.name = context.getString(R.string.category_all);
                data.children.add(0, all);
                // Adapter
                mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
                SideMenuExpandSecondListAdapter adapter = new SideMenuExpandSecondListAdapter(context);
                mBinding.listContents.setAdapter(adapter);
                adapter.setItems(data.children);
            }
        }
    }

    ////////////////////////////////////////////////
}