package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnProductListListener;
import io.temco.guhada.data.model.Deal;
import io.temco.guhada.view.adapter.base.BaseProductListViewHolder;
import io.temco.guhada.view.adapter.holder.ProductListOneViewHolder;
import io.temco.guhada.view.adapter.holder.ProductListThreeViewHolder;
import io.temco.guhada.view.adapter.holder.ProductListTwoViewHolder;

public class ProductListAdapter extends RecyclerView.Adapter<BaseProductListViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private RequestManager mRequestManager;
    private OnProductListListener mProductListener;
    private int mSpanCount = 1;
    private List<Deal> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public ProductListAdapter(Context context, RequestManager manager) {
        mContext = context;
        mRequestManager = manager;
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
        return mSpanCount;
    }

    @NonNull
    @Override
    public BaseProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.Grid.getType(viewType)) {
            case TWO:
                return new ProductListTwoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_two, parent, false));

            case THREE:
                return new ProductListThreeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_three, parent, false));

            default:
                return new ProductListOneViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_one, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseProductListViewHolder holder, int position) {
        holder.init(mContext, mRequestManager, getItem(position));
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mProductListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            Deal data = getItem((int) v.getTag());
            mProductListener.onProduct(data.dealId);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setSpanCount(Type.Grid type) {
        mSpanCount = Type.Grid.get(type);
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setItems(List<Deal> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setOnProductListListener(OnProductListListener listener) {
        mProductListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Deal getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}
