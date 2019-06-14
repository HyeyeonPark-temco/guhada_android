package io.temco.guhada.view.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.temco.guhada.R;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.view.adapter.base.BaseFilterListAdapter;
import io.temco.guhada.view.holder.filter.FilterColorListViewHolder;

public class FilterColorListAdapter extends BaseFilterListAdapter<FilterColorListViewHolder> implements View.OnClickListener {

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterColorListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @NonNull
    @Override
    public FilterColorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterColorListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_type_color, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterColorListViewHolder holder, int position) {
        holder.init(mContext, getItem(position), mFilterListener);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mFilterListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            // Change
            int position = (int) v.getTag();
            getItem(position).selected = !v.isSelected();
            notifyItemChanged(position);
            // Listener
            mFilterListener.onFilter(mFilterId, mItems);
        }
    }

    @Override
    public void reset() {
    }

    @Override
    public void setItems(int id, List<Attribute> items) {
        mFilterId = id;
        mItems = new CopyOnWriteArrayList<>();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    ////////////////////////////////////////////////
}