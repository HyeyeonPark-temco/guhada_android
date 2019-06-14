package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.category.DetailSearchCategoryFirstViewHolder;

public class DetailSearchCategoryFirstListAdapter extends RecyclerView.Adapter<DetailSearchCategoryFirstViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private ExpansionLayoutCollection mExpansionsCollection;
    private List<Category> mItems;
    private OnCategoryListener mCategoryListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchCategoryFirstListAdapter(Context context) {
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
    public DetailSearchCategoryFirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DetailSearchCategoryFirstViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_category_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DetailSearchCategoryFirstViewHolder holder, int position) {
        Category data = getItem(position);
        if (data.children == null) {
            holder.getBinding().layoutHeader.setTag(position);
            holder.getBinding().layoutHeader.setOnClickListener(this);
        } else {
            mExpansionsCollection.add(holder.getBinding().layoutExpandContents);
        }
        holder.init(mContext, null, data, mCategoryListener);
    }

    @Override
    public void onClick(View v) {
        if (mCategoryListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            Category data = getItem((int) v.getTag());
            mCategoryListener.onEvent(data.type, data.hierarchies);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<Category> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setOnCategoryListener(OnCategoryListener listener) {
        mCategoryListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Category getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}