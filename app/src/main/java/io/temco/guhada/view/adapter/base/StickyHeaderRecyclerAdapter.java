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
    private List<M> mItems;
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
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Type.List.get(mItems.get(position).type);
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    @Override
    public boolean isHeader(int itemPosition) {
        return mItems.get(itemPosition).type == Type.List.HEADER;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return mItems.get(headerPosition).layoutRes;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
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
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.add(mItems.size(), item);
    }

    public void setItems(@NonNull List<M> items) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.addAll(items);
    }

    public M getItem(int position) {
        return mItems.get(position);
    }

    ////////////////////////////////////////////////
}
