package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.data.model.BlockChain;
import io.temco.guhada.view.holder.mypage.BlockChainViewHolder;

public class BlockChainListAdapter extends RecyclerView.Adapter<BlockChainViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    //    private OnOrderShipListener mListener;
    private List<BlockChain> mItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BlockChainListAdapter(Context context) {
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
    public BlockChainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BlockChainViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_block_chain_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BlockChainViewHolder holder, int position) {
        holder.init(mContext, position + 1, getItem(position), getItemCount() != position + 1);
    }

    @Override
    public void onClick(View v) {
//        if (mListener != null
//                && v.getTag() != null && v.getTag() instanceof Integer) {
//            int position = (int) v.getTag();
//            switch (v.getId()) {
//                case R.id.text_review:
//                    mListener.onReview(this, position, getItem(position));
//                    break;
//            }
//        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<BlockChain> items) {
        mItems = items;
        notifyDataSetChanged();
    }

//    public void setOnOrderShipListener(OnOrderShipListener listener) {
//        mListener = listener;
//    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private BlockChain getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    ////////////////////////////////////////////////
}