package io.temco.guhada.view.adapter.base;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.List;

import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;

public abstract class BaseCategoryListAdapter<VH extends BaseCategoryViewHolder> extends RecyclerView.Adapter<VH> {

    // -------- LOCAL VALUE --------
    protected Context mContext;
    protected ExpansionLayoutCollection mExpansionsCollection;
    protected List<Category> mItems;
    protected OnCategoryListListener mCategoryListener;
    protected OnCategoryHeaderListListener mCategoryHeaderListListener;

    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<Category> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public List<Category> getmItems() {
        return mItems;
    }

    public void setOnCategoryListener(OnCategoryListListener listener) {
        mCategoryListener = listener;
    }

    public void setmCategoryHeaderListListener(OnCategoryHeaderListListener mCategoryHeaderListListener) {
        this.mCategoryHeaderListListener = mCategoryHeaderListListener;
    }
    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    protected Category getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}