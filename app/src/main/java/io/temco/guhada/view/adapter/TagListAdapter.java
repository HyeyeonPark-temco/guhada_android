package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnTagListener;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Tag;
import io.temco.guhada.view.holder.base.BaseTagViewHolder;
import io.temco.guhada.view.holder.tag.TagTypeFullViewHolder;
import io.temco.guhada.view.holder.tag.TagTypeNormalViewHolder;

public class TagListAdapter extends RecyclerView.Adapter<BaseTagViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private List<Tag> mItems;
    private OnTagListener mTagListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public TagListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemViewType(int position) {
        return Type.Tag.get(getItem(position).type);
    }

    @Override
    public int getItemCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @NonNull
    @Override
    public BaseTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.Tag.getType(viewType)) {
            case TYPE_FULL:
                return new TagTypeFullViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tag_type_full, null));
            default:
                return new TagTypeNormalViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_tag_type_normal, null));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseTagViewHolder holder, int position) {
        holder.init(mContext, position, mItems.size()-1, getItem(position), this);
    }

    @Override
    public void onClick(View v) {
        if (mTagListener != null && v.getTag() != null && v.getTag() instanceof Integer) {
            if(CustomLog.getFlag())CustomLog.L("TagListAdapter","tagData",getItem((int) v.getTag()).tagData);
            mTagListener.onClose((int) v.getTag(), getItem((int) v.getTag()).tagData);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<Tag> items) {
        mItems = new ArrayList<>();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(Tag item) {
        if (mItems == null) mItems = new ArrayList<>();
        mItems.add(item);
    }

    public void removeAll() {
        if (getItemCount() > 0) mItems.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        if (getItemCount() > 0) mItems.remove(position);
        notifyItemRemoved(position);
    }

    public void setOnTagListener(OnTagListener listener) {
        mTagListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Tag getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}
