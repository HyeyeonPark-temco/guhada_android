package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import io.temco.guhada.R;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.adapter.base.BaseCategoryListAdapter;
import io.temco.guhada.view.holder.category.DetailSearchCategoryThirdViewHolder;

public class DetailSearchCategoryThirdListAdapter extends BaseCategoryListAdapter<DetailSearchCategoryThirdViewHolder> implements View.OnClickListener {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategoryThirdListAdapter(Context context) {
        mContext = context;
        mExpansionsCollection = new ExpansionLayoutCollection();
        mExpansionsCollection.openOnlyOne(true);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @NonNull
    @Override
    public DetailSearchCategoryThirdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailSearchCategoryThirdViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_category_third, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailSearchCategoryThirdViewHolder holder, int position) {
        Category data = getItem(position);
        if (data.children == null) {
            holder.getBinding().layoutHeader.setTag(position);
            holder.getBinding().layoutHeader.setOnClickListener(this);
        } else {
            mExpansionsCollection.add(holder.getBinding().layoutExpandContents);
        }
        holder.init(mContext, null, getItem(position), mCategoryListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof Integer) {
            // Data
            int position = (int) v.getTag();
            Category c = getItem(position);
            c.isSelected = !v.isSelected();
            // Listener
            if (mCategoryListener != null) mCategoryListener.onEvent(c);
            // Notify
            notifyItemChanged(position);
        }
    }

    ////////////////////////////////////////////////
}