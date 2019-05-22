package io.temco.guhada.view.adapter.expand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.data.model.CategoryData;
import io.temco.guhada.view.adapter.holder.SideMenuExpandFirstListViewHolder;

public class SideMenuExpandFirstListAdapter extends RecyclerView.Adapter<SideMenuExpandFirstListViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private ExpansionLayoutCollection mExpansionsCollection;
    private List<CategoryData> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public SideMenuExpandFirstListAdapter(Context context) {
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
    public SideMenuExpandFirstListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SideMenuExpandFirstListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_side_menu_expand_first, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SideMenuExpandFirstListViewHolder holder, int position) {
        mExpansionsCollection.add(holder.getBinding().layoutContents);
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