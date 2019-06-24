package io.temco.guhada.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
        // Certificate (PDF)
        holder.getBinding().imageCertificate.setTag(position);
        holder.getBinding().imageCertificate.setOnClickListener(this);

        // Link
        holder.getBinding().imageLink.setTag(position);
        holder.getBinding().imageLink.setOnClickListener(this);

        // Data
        holder.init(mContext, position + 1, getItem(position), getItemCount() != position + 1);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() != null && v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            switch (v.getId()) {
                case R.id.image_certificate:
                    showUrlView(getItem(position).certificateUrl);
                    break;

                case R.id.image_link:
                    showUrlView("https://ropsten.etherscan.io/tx/" + getItem(position).contractAddress);
                    break;
            }
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setItems(List<BlockChain> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private BlockChain getItem(int position) {
        return mItems == null ? null : mItems.get(position);
    }

    private void showUrlView(String url) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    ////////////////////////////////////////////////
}