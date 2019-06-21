package io.temco.guhada.view.adapter.mypage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnOrderShipListener;
import io.temco.guhada.view.holder.mypage.OrderShipViewHolder;

public class OrderShipListAdapter extends RecyclerView.Adapter<OrderShipViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private RequestManager mRequestManager;
    private OnOrderShipListener mListener;
    private List mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public OrderShipListAdapter(Context context, RequestManager manager) {
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

    @NonNull
    @Override
    public OrderShipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderShipViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_order_ship_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderShipViewHolder holder, int position) {

    }

    @Override
    public void onClick(View v) {
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void setOnOrderShipListener(OnOrderShipListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private Object getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}