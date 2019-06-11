package io.temco.guhada.view.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.view.adapter.holder.FilterColorListViewHolder;

public class FilterColorListAdapter extends RecyclerView.Adapter<FilterColorListViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private OnFilterListener mFilterListener;
    private int mFilterId;
    private List<Attribute> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterColorListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

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

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(int id, List<Attribute> items) {
        mFilterId = id;
        mItems = items;
        notifyDataSetChanged();
    }

    public void setOnFilterListener(OnFilterListener listener) {
        mFilterListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Attribute getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}