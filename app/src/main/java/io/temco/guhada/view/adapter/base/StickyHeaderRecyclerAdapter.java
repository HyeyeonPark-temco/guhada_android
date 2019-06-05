package io.temco.guhada.view.adapter.base;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.common.Type;
import io.temco.guhada.common.decoration.StickyHeaderItemDecoration;
import io.temco.guhada.common.listener.OnStickyHeaderListener;
import io.temco.guhada.data.model.base.BaseStickyHeaderModel;

public abstract class StickyHeaderRecyclerAdapter<VH extends RecyclerView.ViewHolder, M extends BaseStickyHeaderModel>
        extends RecyclerView.Adapter<VH> implements OnStickyHeaderListener {

    // -------- LOCAL VALUE --------
    private List<M> mOriginalItems;
    private List<M> mFilterItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new StickyHeaderItemDecoration(this));
    }

    @Override
    public int getItemCount() {
        return mFilterItems == null ? 0 : mFilterItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Type.List.get(mFilterItems.get(position).type);
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    @Override
    public boolean isHeader(int itemPosition) {
        return mFilterItems.get(itemPosition).type == Type.List.HEADER;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return mFilterItems.get(headerPosition).layoutRes;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItem(@NonNull M item) {
        if (mFilterItems == null) {
            mFilterItems = new ArrayList<>();
        }
        mFilterItems.add(mFilterItems.size(), item);
    }

    public void setOriginalItems(@NonNull List<M> items) {
        if (mOriginalItems == null) mOriginalItems = new ArrayList<>();
        if (mFilterItems == null) mFilterItems = new ArrayList<>();
        mOriginalItems.clear();
        mOriginalItems.addAll(items);
        mFilterItems.clear();
        mFilterItems.addAll(items);
    }

    public void setFilterItems(@NonNull List<M> items) {
        if (mFilterItems != null) {
            mFilterItems.clear();
            mFilterItems.addAll(items);
        }
    }

    public void resetFilterToOriginal() {
        setFilterItems(mOriginalItems);
    }

    public M getItem(int position) {
        return mFilterItems.get(position);
    }

    public List<M> getItems() {
        return mFilterItems;
    }

    ////////////////////////////////////////////////
}