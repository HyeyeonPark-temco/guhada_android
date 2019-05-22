package io.temco.guhada.view.adapter.expand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.view.adapter.holder.CategorySubExpandFirstListViewHolder;

public class CategorySubExpandFirstListAdapter extends RecyclerView.Adapter<CategorySubExpandFirstListViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private ExpansionLayoutCollection mExpansionsCollection;
    private List<CategoryData> mItems;
    private Type.CategoryData mChildType;
    private OnCategoryListener mCategoryListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public CategorySubExpandFirstListAdapter(Context context) {
        mContext = context;
        mExpansionsCollection = new ExpansionLayoutCollection();
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
    public CategorySubExpandFirstListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategorySubExpandFirstListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category_sub_expand_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategorySubExpandFirstListViewHolder holder, int position) {
        mExpansionsCollection.add(holder.getBinding().layoutContents);
        holder.init(mContext, getItem(position), mCategoryListener, mChildType);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<CategoryData> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setChildType(Type.CategoryData type) {
        mChildType = type;
    }

    public void setOnCategoryListener(OnCategoryListener listener) {
        mCategoryListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private CategoryData getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}