package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import io.temco.guhada.R;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.adapter.base.BaseCategoryListAdapter;
import io.temco.guhada.view.holder.category.DetailSearchCategoryFourthViewHolder;

public class DetailSearchCategoryFourthListAdapter extends BaseCategoryListAdapter<DetailSearchCategoryFourthViewHolder> implements View.OnClickListener {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategoryFourthListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @NonNull
    @Override
    public DetailSearchCategoryFourthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailSearchCategoryFourthViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_category_fourth, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailSearchCategoryFourthViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
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