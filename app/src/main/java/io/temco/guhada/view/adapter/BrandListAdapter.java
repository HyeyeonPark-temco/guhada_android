package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ItemBrandListHeaderBinding;
import io.temco.guhada.view.adapter.base.BaseBrandViewHolder;
import io.temco.guhada.view.adapter.base.StickyHeaderRecyclerAdapter;
import io.temco.guhada.view.adapter.holder.BrandListContentsViewHolder;
import io.temco.guhada.view.adapter.holder.BrandListHeaderViewHolder;

public class BrandListAdapter extends StickyHeaderRecyclerAdapter<BaseBrandViewHolder, Brand> {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private boolean mIsAlphabet = true;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BrandListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @NonNull
    @Override
    public BaseBrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.List.getType(viewType)) {
            case HEADER:
                return new BrandListHeaderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_brand_list_header, parent, false));
            default:
                return new BrandListContentsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_brand_list_contents, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBrandViewHolder holder, int position) {
        holder.init(mContext, getItem(position), mIsAlphabet);
    }

    @Override
    public void onBindHeaderData(View header, int headerPosition) {
        // Header
        ItemBrandListHeaderBinding binding = DataBindingUtil.bind(header);
        // Data
        if (binding != null) {
            binding.textTitle.setText(mIsAlphabet ? getItem(headerPosition).nameEn : getItem(headerPosition).nameKo);
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void changeLanguage(boolean isAlphabet) {
        mIsAlphabet = isAlphabet;
    }

    ////////////////////////////////////////////////
}