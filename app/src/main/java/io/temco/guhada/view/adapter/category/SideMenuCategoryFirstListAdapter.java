package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.ExpansionCollection;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.category.SideMenuFirstViewHolder;

public class SideMenuCategoryFirstListAdapter extends RecyclerView.Adapter<SideMenuFirstViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private ExpansionCollection mExpansionsCollection;
    private List<Category> mItems;
    private OnCategoryListListener mCategoryListener;
    private OnCategoryHeaderListListener mCategoryHeaderListListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuCategoryFirstListAdapter(Context context) {
        mContext = context;
        mExpansionsCollection = new ExpansionCollection();
        mExpansionsCollection.openOnlyOne(true);
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
    public SideMenuFirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SideMenuFirstViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_side_menu_category_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SideMenuFirstViewHolder holder, int position) {
        mExpansionsCollection.add(holder.getBinding().layoutExpandContents);
        holder.init(mContext, null, getItem(position), mCategoryListener, mCategoryHeaderListListener);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<Category> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setOnCategoryListener(OnCategoryListListener listener) {
        mCategoryListener = listener;
    }

    public void setmCategoryHeaderListListener(OnCategoryHeaderListListener mCategoryHeaderListListener) {
        this.mCategoryHeaderListListener = mCategoryHeaderListListener;
    }

    public void collapseAll() {
        mExpansionsCollection.collapseAll();
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Category getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}