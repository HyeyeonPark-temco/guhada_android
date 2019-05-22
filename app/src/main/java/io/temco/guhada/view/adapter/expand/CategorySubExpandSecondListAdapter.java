package io.temco.guhada.view.adapter.expand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.view.adapter.base.BaseCategoryViewHolder;
import io.temco.guhada.view.adapter.holder.CategorySubExpandAllListViewHolder;
import io.temco.guhada.view.adapter.holder.CategorySubExpandSecondListViewHolder;

public class CategorySubExpandSecondListAdapter extends RecyclerView.Adapter<BaseCategoryViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private List<CategoryData> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public CategorySubExpandSecondListAdapter(Context context) {
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
        if (getItem(position) != null) {
            return Type.Category.get(getItem(position).type);
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public BaseCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.Category.getType(viewType)) {
            case ALL:
                return new CategorySubExpandAllListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category_sub_expand_all, parent, false));

            default:
                return new CategorySubExpandSecondListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_category_sub_expand_second, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseCategoryViewHolder holder, int position) {
        holder.init(mContext, getItem(position));
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<CategoryData> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private CategoryData getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}