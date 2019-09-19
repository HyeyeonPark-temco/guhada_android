package io.temco.guhada.view.adapter.product;

import android.app.Activity;
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
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Deal;
import io.temco.guhada.view.holder.base.BaseProductViewHolder;
import io.temco.guhada.view.holder.product.ProductOneViewHolder;
import io.temco.guhada.view.holder.product.ProductThreeViewHolder;
import io.temco.guhada.view.holder.product.ProductTwoViewHolder;

public class ProductListAdapter extends RecyclerView.Adapter<BaseProductViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private RequestManager mRequestManager;
    private Type.Grid mGridType = Type.Grid.TWO;
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
        return Type.Grid.get(mGridType);
    }

    @NonNull
    @Override
    public BaseProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.Grid.getType(viewType)) {
            case TWO:
                return new ProductTwoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_two, parent, false));

            case THREE:
                return new ProductThreeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_three, parent, false));

            default:
                return new ProductOneViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_product_list_one, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseProductViewHolder holder, int position) {
        holder.init(mContext, mRequestManager, getItem(position), position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof Integer) {
            Deal data = getItem((int) v.getTag());
            if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("ProductListAdapter","onClick dealId",data.dealId);
            CommonUtil.startProductActivity(((Activity)mContext),(long)(data.dealId));
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setSpanCount(Type.Grid type) {
        mGridType = type;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void setItems(List<Deal> items) {
        if (getItemCount() > 0) {
            mItems.addAll(getItemCount(), items);
        } else {
            mItems = items;
        }
        notifyDataSetChanged();
    }

    public void reset() {
        mItems = null;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Deal getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}
