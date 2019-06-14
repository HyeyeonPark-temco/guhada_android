package io.temco.guhada.view.holder.filter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.decoration.GridSpacingItemDecoration;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.databinding.LayoutDetailSearchTypeBinding;
import io.temco.guhada.view.holder.base.BaseViewHolder;
import io.temco.guhada.view.adapter.filter.FilterColorListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextButtonListAdapter;
import io.temco.guhada.view.adapter.filter.FilterTextListAdapter;

public class FilterListViewHolder extends BaseViewHolder<LayoutDetailSearchTypeBinding> {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterListViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void init(Context context, Filter data, OnFilterListener listener) {
        if (data != null) {
            if (!TextUtils.isEmpty(data.viewType)) {
                switch (Type.ProductOption.getType(data.viewType)) {
                    case COLOR:
                        createColorFilterLayout(context, listener, data.id, data.name, data.attributes);
                        break;

                    case TEXT_BUTTON:
                        createTextButtonFilterLayout(context, listener, data.id, data.name, data.attributes);
                        break;

                    case TEXT:
                        createTextFilterLayout(context, listener, data.id, data.name, data.attributes);
                        break;
                }
            }
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void createColorFilterLayout(Context context, OnFilterListener listener, int id, String title, List<Attribute> attributes) {
        // Title
        mBinding.setTitle(title);
        // Data
        FilterColorListAdapter adapter = new FilterColorListAdapter(context);
        adapter.setOnFilterListener(listener);
        adapter.setItems(id, attributes);
        int column = context.getResources().getDimensionPixelSize(R.dimen.padding_detail_search_filter_column);
        int row = context.getResources().getDimensionPixelSize(R.dimen.padding_detail_search_filter_row);
        mBinding.listContents.addItemDecoration(new GridSpacingItemDecoration(7, column, row, false));
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_detail_search_filter);
        mBinding.listContents.setPadding(0, padding, 0, padding);
        mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 7));
        mBinding.listContents.setAdapter(adapter);
    }

    private void createTextButtonFilterLayout(Context context, OnFilterListener listener, int id, String title, List<Attribute> attributes) {
        // Title
        mBinding.setTitle(title);
        // Data
        FilterTextButtonListAdapter adapter = new FilterTextButtonListAdapter(context);
        adapter.setOnFilterListener(listener);
        adapter.setItems(id, attributes);
        mBinding.listContents.setLayoutManager(new GridLayoutManager(context, 2));
        mBinding.listContents.setAdapter(adapter);
    }

    private void createTextFilterLayout(Context context, OnFilterListener listener, int id, String title, List<Attribute> attributes) {
        // Title
        mBinding.setTitle(title);
        // Data
        FilterTextListAdapter adapter = new FilterTextListAdapter(context);
        adapter.setOnFilterListener(listener);
        adapter.setItems(id, attributes);
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(context));
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
}