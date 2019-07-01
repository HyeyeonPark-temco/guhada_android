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
import io.temco.guhada.data.model.MyOrderItem;
import io.temco.guhada.view.holder.mypage.OrderShipViewHolder;

public class OrderShipListAdapter extends RecyclerView.Adapter<OrderShipViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private RequestManager mRequestManager;
    private OnOrderShipListener mListener;
    private List<MyOrderItem> mItems;
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
        MyOrderItem data = getItem(position);

        // TEMP
        if (position > 0) {
            holder.getBinding().textReview.setText("확인 후 배송시작");
            holder.getBinding().textviewCertificationResult.setText("제품 감정 결과 '진품'으로 확인 되었습니다.");
        } else {
            holder.getBinding().textReview.setText("확인 후 환불받기");
            holder.getBinding().textviewCertificationResult.setText("제품 감정 결과 '가품'으로 확인 되었습니다.");
        }

        if(position == 0) holder.getBinding().imageThumb.setImageResource(R.drawable.temp_product_thumbnail1);
        if(position == 1) holder.getBinding().imageThumb.setImageResource(R.drawable.temp_product_thumbnail2);
        if(position == 2) holder.getBinding().imageThumb.setImageResource(R.drawable.temp_product_thumbnail3);



        // Review
        holder.getBinding().textReview.setSelected(!data.checkReward);
        if (!data.checkReward) {
            holder.getBinding().textReview.setTag(position);
            holder.getBinding().textReview.setOnClickListener(this);
        }

        holder.init(mContext, data, mListener);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            switch (v.getId()) {
                case R.id.text_review:
                    if (position > 0) {
                        mListener.onReward(this, position, getItem(position), "배송 접수가 완료되었습니다.");
                    } else {
                        mListener.onReward(this, position, getItem(position), "환불 신청이 완료되었습니다.");
                    }

                    break;
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<MyOrderItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public void changeItems(int position, MyOrderItem item) {
        if (getItemCount() > 0) {
            mItems.set(position, item);
            notifyItemChanged(position);
        }
    }

    public void setOnOrderShipListener(OnOrderShipListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private MyOrderItem getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}