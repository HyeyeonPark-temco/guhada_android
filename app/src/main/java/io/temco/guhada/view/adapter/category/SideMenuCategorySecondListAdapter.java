package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;
import io.temco.guhada.view.holder.category.SideMenuAllViewHolder;
import io.temco.guhada.view.holder.category.SideMenuSecondViewHolder;

public class SideMenuCategorySecondListAdapter extends RecyclerView.Adapter<BaseCategoryViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private List<Category> mItems;
    private OnCategoryListener mCategoryListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuCategorySecondListAdapter(Context context) {
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
                return new SideMenuAllViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_side_menu_category_all, parent, false));

            default:
                return new SideMenuSecondViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_side_menu_category_second, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseCategoryViewHolder holder, int position) {
        holder.init(mContext, null, getItem(position), null);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
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