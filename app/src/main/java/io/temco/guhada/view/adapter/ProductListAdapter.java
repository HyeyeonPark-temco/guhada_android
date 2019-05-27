package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.data.model.ProductList;
import io.temco.guhada.view.adapter.base.BaseProductListViewHolder;

public class ProductListAdapter extends RecyclerView.Adapter<BaseProductListViewHolder> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private List<ProductList> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListAdapter(Context context) {
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
    public BaseProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseProductListViewHolder holder, int position) {

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<ProductList> items) {
        mItems = items;
        notifyDataSetChanged();
    }

//    public void setOnCategoryListener(OnCategoryListener listener) {
//        mCategoryListener = listener;
//    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private ProductList getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}
