package io.temco.guhada.view.adapter.brand;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnBrandListener;
import io.temco.guhada.common.util.CustomComparator;
import io.temco.guhada.common.util.TextSearcher;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.view.holder.base.BaseBrandViewHolder;
import io.temco.guhada.view.holder.brand.DetailSearchBrandContentsViewHolder;
import io.temco.guhada.view.holder.brand.DetailSearchBrandHeaderViewHolder;

public class DetailSearchBrandListAdapter extends RecyclerView.Adapter<BaseBrandViewHolder> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private OnBrandListener mBrandListener;
    private boolean mIsAlphabet = true;
    private List<Brand> mOriginalItems;
    private List<Brand> mFilterItems;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public DetailSearchBrandListAdapter(Context context) {
        mContext = context;
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public int getItemCount() {
        return mFilterItems == null ? 0 : mFilterItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return Type.List.get(mFilterItems.get(position).type);
    }

    @NonNull
    @Override
    public BaseBrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (Type.List.getType(viewType)) {
            case HEADER:
                return new DetailSearchBrandHeaderViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_brand_list_header, parent, false));
            default:
                return new DetailSearchBrandContentsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_detail_search_brand_list_contents, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBrandViewHolder holder, int position) {
        holder.init(mContext, getItem(position), mIsAlphabet);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mBrandListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            int position = (int) v.getTag();
            if (getItem(position).type != Type.List.HEADER) {
                selectItem(position, !v.isSelected());
                // Listener
                mBrandListener.onEvent(getItem(position));
            }
        }
    }

    private void selectItem(int position, boolean selected) {
        // Change
        Brand select = getItem(position);
        select.isSelected = selected;
        // Original
        for (Brand b : mOriginalItems) {
            if (b.id == select.id) {
                b.isSelected = selected;
                break;
            }
        }
        notifyItemChanged(position);
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void changeLanguage(boolean isAlphabet) {
        mIsAlphabet = isAlphabet;
    }

    public void initBrandData(List<Brand> data) {
        setBrandData(data, mIsAlphabet, true);
    }

    public void filter(String text) {
        if (getOriginalItemCount() > 0) {
            text = text.toLowerCase();
            List<Brand> f = new ArrayList<>();
            for (Brand b : getOriginalItems()) {
                String t = mIsAlphabet ? b.nameEn : b.nameKo;
                if (TextUtils.isEmpty(t) && t.toLowerCase().contains(text))
                    f.add(b);
            }
            if (f.size() > 0) {
                setBrandData(f, mIsAlphabet, false);
            } else {
                setFilterItems(f);
            }
        }
    }

    public void resetFilterToOriginal() {
        if (getOriginalItemCount() > 0) {
            setBrandData(getOriginalItems(), mIsAlphabet, true);
        }
    }

    public void setOnBrandListener(OnBrandListener listener) {
        mBrandListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setBrandData(List<Brand> data, boolean isAlphabet, boolean isReset) {
        if (data != null && data.size() > 0) {
            // Sort
            Collections.sort(data, CustomComparator.getBrandComparator(true, isAlphabet));
            List<Brand> sort = new ArrayList<>();
            List<Brand> side = new ArrayList<>();
            // Index
            LinkedHashMap<String, Integer> index = new LinkedHashMap<>();
            for (Brand b : data) {
                if (isAlphabet) {
                    setIndexBrandAlphabetData(b, index, sort, side);
                } else {
                    setIndexBrandHangulData(b, index, sort, side);
                }
            }
            // Side (#)
            if (side.size() > 0) {
                String s = "#";
                sort.add(createBrandHeaderData(s)); // Header
                index.put(s, sort.size()); // Index
                sort.addAll(sort.size(), side); // Row
            }
            // Adapter
            changeLanguage(isAlphabet);
            if (isReset) {
                setOriginalItems(data, sort);
            } else {
                setFilterItems(sort);
            }
            notifyDataSetChanged();
        }
    }

    private void setIndexBrandAlphabetData(Brand data,
                                           LinkedHashMap<String, Integer> index,
                                           List<Brand> sort,
                                           List<Brand> side) {
        if (TextUtils.isEmpty(data.nameEn)) {
            return;
        }
        char key = data.nameEn.toUpperCase().charAt(0);
        if (!TextSearcher.isAlphabet(key)) {
            side.add(data);
            return;
        }
        String keyString = String.valueOf(key);
        if (!index.containsKey(keyString)) {
            sort.add(createBrandHeaderData(keyString)); // Header
            index.put(keyString, sort.size()); // Index
        }
        sort.add(data); // Row
    }

    private void setIndexBrandHangulData(Brand data,
                                         LinkedHashMap<String, Integer> index,
                                         List<Brand> sort,
                                         List<Brand> side) {
        if (TextUtils.isEmpty(data.nameKo)) {
            return;
        }
        char key = data.nameKo.charAt(0);
        if (!TextSearcher.isHangul(key)) {
            side.add(data);
            return;
        }
        String keyString = String.valueOf(TextSearcher.getInitialHangulSound(key));
        if (!index.containsKey(keyString)) {
            sort.add(createBrandHeaderData(keyString)); // Header
            index.put(keyString, sort.size()); // Index
        }
        sort.add(data); // Row
    }

    private Brand createBrandHeaderData(String title) {
        Brand b = new Brand();
        b.type = Type.List.HEADER;
        b.nameEn = title;
        b.nameKo = title;
        b.nameDefault = title;
        b.layoutRes = R.layout.item_brand_list_header;
        return b;
    }

    // Original
    private int getOriginalItemCount() {
        return mOriginalItems == null ? 0 : mOriginalItems.size();
    }

    private void setOriginalItems(@NonNull List<Brand> original, List<Brand> filter) {
        // Original
        if (getOriginalItemCount() <= 0) {
            mOriginalItems = new ArrayList<>();
            mOriginalItems.addAll(original);
        }
        // Filter
        if (getItemCount() > 0) {
            mFilterItems.clear();
        } else {
            mFilterItems = new ArrayList<>();
        }
        mFilterItems.addAll(filter);
    }

    private List<Brand> getOriginalItems() {
        return mOriginalItems;
    }

    // Filter
    private void setFilterItems(@NonNull List<Brand> items) {
        if (mFilterItems != null) {
            mFilterItems.clear();
            mFilterItems.addAll(items);
        }
    }

    private void setItem(@NonNull Brand item) {
        if (mFilterItems == null) {
            mFilterItems = new ArrayList<>();
        }
        mFilterItems.add(mFilterItems.size(), item);
    }

    private Brand getItem(int position) {
        return mFilterItems.get(position);
    }

    private List<Brand> getItems() {
        return mFilterItems;
    }

    ////////////////////////////////////////////////
}