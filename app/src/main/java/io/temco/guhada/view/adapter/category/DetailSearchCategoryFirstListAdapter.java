package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import io.temco.guhada.R;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.adapter.base.BaseCategoryListAdapter;
import io.temco.guhada.view.holder.category.DetailSearchCategoryFirstViewHolder;

public class DetailSearchCategoryFirstListAdapter extends BaseCategoryListAdapter<DetailSearchCategoryFirstViewHolder> implements View.OnClickListener {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategoryFirstListAdapter(Context context) {
        mContext = context;
        mExpansionsCollection = new ExpansionLayoutCollection();
        mExpansionsCollection.openOnlyOne(true);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @NonNull
    @Override
    public DetailSearchCategoryFirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailSearchCategoryFirstViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_category_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailSearchCategoryFirstViewHolder holder, int position) {
        Category data = getItem(position);
        if (data.children == null) {
            holder.getBinding().layoutHeader.setTag(position);
            holder.getBinding().layoutHeader.setOnClickListener(this);
        } else {
            holder.getBinding().layoutExpandContents.setTag(position);
            holder.getBinding().layoutExpandContents.addListener(new ExpansionLayout.Listener() {
                @Override
                public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                    // Data
                    int position = (int) expansionLayout.getTag();
                    Category c = getItem(position);
                    mCategoryHeaderListListener.onEvent(position,c);
                }
            });
            mExpansionsCollection.add(holder.getBinding().layoutExpandContents);
        }
        holder.init(mContext, null, getItem(position), mCategoryListener, mCategoryHeaderListListener);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof Integer) {
            // Data
            int position = (int) v.getTag();
            Category c = getItem(position);
            c.isSelected = !v.isSelected();
            // Listener
            if (mCategoryListener != null) mCategoryListener.onEvent(0, c);
            // Notify
            notifyItemChanged(position);
        }
    }

    ////////////////////////////////////////////////
}