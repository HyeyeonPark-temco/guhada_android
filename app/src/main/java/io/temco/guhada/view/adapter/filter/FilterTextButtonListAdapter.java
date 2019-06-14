package io.temco.guhada.view.adapter.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.view.adapter.base.BaseFilterListAdapter;
import io.temco.guhada.view.holder.base.BaseFilterViewHolder;
import io.temco.guhada.view.holder.filter.FilterMoreListViewHolder;
import io.temco.guhada.view.holder.filter.FilterTextButtonListViewHolder;

public class FilterTextButtonListAdapter extends BaseFilterListAdapter<BaseFilterViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Attribute mMore;
    private CopyOnWriteArrayList<Attribute> mOriginalItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public FilterTextButtonListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

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

    @Override
    public void reset() {
    }

    @Override
    public void setItems(int id, List<Attribute> items) {
        mFilterId = id;
        // Original
        // if (mOriginalItems == null) mOriginalItems = new CopyOnWriteArrayList<>();
        // mOriginalItems.removeAll(mOriginalItems);
        mOriginalItems = new CopyOnWriteArrayList<>();
        mOriginalItems.addAll(items);
        // Items
        // if (mItems == null) mItems = new CopyOnWriteArrayList<>();
        // mItems.removeAll(mItems);
        mItems = new CopyOnWriteArrayList<>();
        if (items != null && items.size() > 4) {
            // Sub List
            mItems.addAll(items.subList(0, 3));
            // Add More
            if (mMore == null) {
                mMore = new Attribute();
                mMore.type = Type.List.MORE;
            }
            mItems.add(mMore);
        } else {
            mItems.addAll(items);
        }
        notifyDataSetChanged();

    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void changeOriginalItems() {
        if (mMore != null) mOriginalItems.remove(mMore);
        mItems = mOriginalItems;
        notifyItemRangeChanged(0, mOriginalItems.size());
    }

    ////////////////////////////////////////////////
}