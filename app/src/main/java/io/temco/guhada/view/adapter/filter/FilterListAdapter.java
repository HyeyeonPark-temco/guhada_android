package io.temco.guhada.view.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.view.adapter.holder.FilterListViewHolder;

public class FilterListAdapter extends RecyclerView.Adapter<FilterListViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private OnFilterListener mFilterListener;
    private CopyOnWriteArrayList<Filter> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterListAdapter(Context context) {
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
    public FilterListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FilterListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_detail_search_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FilterListViewHolder holder, int position) {
        holder.init(mContext, getItem(position), mFilterListener);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<Filter> items) {
        mItems = new CopyOnWriteArrayList<>();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnFilterListener(OnFilterListener listener) {
        mFilterListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Filter getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}
