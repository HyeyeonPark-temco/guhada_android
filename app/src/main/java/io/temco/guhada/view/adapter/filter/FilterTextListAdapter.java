package io.temco.guhada.view.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.view.adapter.base.BaseFilterViewHolder;
import io.temco.guhada.view.adapter.holder.FilterMoreListViewHolder;
import io.temco.guhada.view.adapter.holder.FilterTextButtonListViewHolder;

public class FilterTextListAdapter extends RecyclerView.Adapter<BaseFilterViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private OnFilterListener mFilterListener;
    private int mFilterId;
    private List<Attribute> mItems;
    private List<Attribute> mOriginalItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterTextListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Type.List.get(getItem(position).type);
    }

    @NonNull
    @Override
    public BaseFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.List.getType(viewType)) {
            case MORE:
                return new FilterMoreListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_type_more, parent, false));
            default:
                return new FilterTextButtonListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_type_text_button, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseFilterViewHolder holder, int position) {
        holder.init(mContext, getItem(position), mFilterListener);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mFilterListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            switch (getItem(position).type) {
                case MORE:
                    changeOriginalItems();
                    break;

                default:
                    // Change
                    getItem(position).selected = !v.isSelected();
                    mOriginalItems.get(position).selected = !v.isSelected();
                    notifyItemChanged(position);
                    // Listener
                    mFilterListener.onFilter(mFilterId, mOriginalItems);
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(int id, List<Attribute> items) {
        mFilterId = id;
        mOriginalItems = items;
        if (items != null && items.size() > 4) {
            mItems = items.subList(0, 3);
            mItems.add(createMoreFilter());
        }
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

    private void changeOriginalItems() {
        mItems = mOriginalItems;
        notifyDataSetChanged();
    }

    private Attribute createMoreFilter() {
        Attribute more = new Attribute();
        more.type = Type.List.MORE;
        return more;
    }

    ////////////////////////////////////////////////
}
