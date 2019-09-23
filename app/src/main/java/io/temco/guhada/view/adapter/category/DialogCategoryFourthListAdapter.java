package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.base.BaseCategoryViewHolder;
import io.temco.guhada.view.holder.category.DetailSearchCategoryFourthViewHolder;
import io.temco.guhada.view.holder.category.DialogCategoryFourthViewHolder;

public class DialogCategoryFourthListAdapter extends RecyclerView.Adapter<DialogCategoryFourthViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private List<Category> mItems;
    private OnCategoryListListener mCategoryListener;
    private OnCategoryHeaderListListener mCategoryHeaderListListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DialogCategoryFourthListAdapter(Context context) {
        mContext = context;
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
    public DialogCategoryFourthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogCategoryFourthViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dialog_category_fourth, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DialogCategoryFourthViewHolder holder, int position) {
        holder.init(mContext, null, getItem(position), mCategoryListener, mCategoryHeaderListListener);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mCategoryListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            mCategoryListener.onEvent(position,getItem((int) v.getTag()));
        }
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

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Category getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}