package io.temco.guhada.view.adapter.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryHeaderListListener;
import io.temco.guhada.common.listener.OnCategoryListListener;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.view.holder.category.DialogCategoryFirstViewHolder;

public class DialogCategoryFirstListAdapter extends RecyclerView.Adapter<DialogCategoryFirstViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private ExpansionLayoutCollection mExpansionsCollection;
    private List<Category> mItems;
    private OnCategoryListListener mCategoryListener;
    private OnCategoryHeaderListListener mCategoryHeaderListListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DialogCategoryFirstListAdapter(Context context) {
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
    public DialogCategoryFirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogCategoryFirstViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_dialog_category_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DialogCategoryFirstViewHolder holder, int position) {
        Category data = getItem(position);
        if (data.children == null) {
            holder.getBinding().layoutHeader.setTag(position);
            holder.getBinding().layoutHeader.setOnClickListener(this);
        } else {
            holder.getBinding().layoutExpandContents.setTag(position);
            holder.getBinding().layoutExpandContents.addListener(new ExpansionLayout.Listener() {
                @Override
                public void onExpansionChanged(ExpansionLayout expansionLayout, boolean expanded) {
                    // Data
                    int position = (int) expansionLayout.getTag();
                    Category c = getItem(position);
                    if(holder.isInit){
                        holder.isInit = false;
                        holder.addChild(mContext, Type.CategoryData.getType(c.hierarchies[0]), c, mCategoryListener, mCategoryHeaderListListener);
                    }
                    mCategoryHeaderListListener.onEvent(position,c);
                }
            });
            mExpansionsCollection.add(holder.getBinding().layoutExpandContents);
        }
        holder.init(mContext, Type.CategoryData.getType(data.hierarchies[0]), data, mCategoryListener, mCategoryHeaderListListener);
    }

    @Override
    public void onClick(View v) {
        if (mCategoryListener != null && v.getTag() != null && v.getTag() instanceof Integer) {
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