package io.temco.guhada.view.adapter.base;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnTestItemListener;

public class TestListAdapter extends RecyclerView.Adapter<TestListViewHolder> implements View.OnClickListener {

    private List<String> mItem;
    private OnTestItemListener mListener;

    @Override
    public int getItemCount() {
        return mItem == null ? 0 : mItem.size();
    }

    @NonNull
    @Override
    public TestListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_test, parent, false);
        return new TestListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TestListViewHolder holder, int position) {
        String title = getItem(position);
        if (TextUtils.isEmpty(title)) {
            holder.mBinding.buttonTest.setText(null);
            holder.mBinding.buttonTest.setTag(null);
            holder.mBinding.buttonTest.setOnClickListener(null);
        } else {
            holder.mBinding.buttonTest.setText(title);
            holder.mBinding.buttonTest.setTag(position);
            holder.mBinding.buttonTest.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener != null &&
                v.getTag() != null && v.getTag() instanceof Integer) {
            mListener.onItem((int) v.getTag());
        }
    }

    public void setOnTestItemListener(OnTestItemListener listener) {
        mListener = listener;
    }

    public void setItem(List<String> item) {
        mItem = item;
    }

    private String getItem(int position) {
        return getItemCount() > 0 ? mItem.get(position) : null;
    }

}