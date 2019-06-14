package io.temco.guhada.view.adapter.base;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.temco.guhada.common.listener.OnFilterListener;
import io.temco.guhada.data.model.Attribute;

public abstract class BaseFilterListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    // -------- LOCAL VALUE --------
    protected Context mContext;
    protected OnFilterListener mFilterListener;
    protected int mFilterId;
    protected CopyOnWriteArrayList<Attribute> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    ////////////////////////////////////////////////
    // ABSTRACT
    ////////////////////////////////////////////////

    public abstract void reset();

    public abstract void setItems(int id, List<Attribute> items);

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnFilterListener(OnFilterListener listener) {
        mFilterListener = listener;
    }

    ////////////////////////////////////////////////
    // PROTECTED
    ////////////////////////////////////////////////

    protected Attribute getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}