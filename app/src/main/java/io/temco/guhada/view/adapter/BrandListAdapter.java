package io.temco.guhada.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.decoration.FastScrollItemDecoration;
import io.temco.guhada.common.listener.OnBrandListener;
import io.temco.guhada.common.listener.OnFastScrollListener;
import io.temco.guhada.common.util.CustomComparator;
import io.temco.guhada.common.util.TextSearcher;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ItemBrandListHeaderBinding;
import io.temco.guhada.view.adapter.base.BaseBrandViewHolder;
import io.temco.guhada.view.adapter.base.StickyHeaderRecyclerAdapter;
import io.temco.guhada.view.adapter.holder.BrandListContentsViewHolder;
import io.temco.guhada.view.adapter.holder.BrandListHeaderViewHolder;

public class BrandListAdapter extends StickyHeaderRecyclerAdapter<BaseBrandViewHolder, Brand> implements OnFastScrollListener, View.OnClickListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private boolean mIsAlphabet = true;
    private OnBrandListener mBrandListener;
    //
    private Map<String, Integer> mIndex;
    private String[] mSections;
    private String mCurrentSection;
    private boolean mIsShowSection = false;
    private int mHeaderHeight;
    private float mTopPadding;
    private float mPointTextSize;
    private float mLetterTextSize;
    private float mSectionThumbTextSize;
    private float mSectionThumbSize;
    private float mSectionThumbPadding;
    @ColorInt
    private int mLetterTextNormal;
    @ColorInt
    private int mLetterTextSelect;
    @ColorInt
    private int mSectionText;
    @ColorInt
    private int mSectionBackground;
    // -----------------------------

    ////////////////////////////////////////////////
    // CONSTRUCTOR
    ////////////////////////////////////////////////

    public BrandListAdapter(Context context) {
        mContext = context;
        // Size
        mHeaderHeight = context.getResources().getDimensionPixelSize(R.dimen.view_fast_scroll_brand_header_row);
        mTopPadding = context.getResources().getDimension(R.dimen.padding_fast_scroll_brand_top);
        mPointTextSize = context.getResources().getDimension(R.dimen.text_8);
        mLetterTextSize = context.getResources().getDimension(R.dimen.text_10);
        mSectionThumbTextSize = context.getResources().getDimension(R.dimen.text_30);
        mSectionThumbSize = context.getResources().getDimension(R.dimen.view_fast_scroll_brand_thumb);
        mSectionThumbPadding = context.getResources().getDimension(R.dimen.padding_fast_scroll_brand_section);
        // Color
        mLetterTextNormal = ContextCompat.getColor(context, R.color.text_4);
        mLetterTextSelect = ContextCompat.getColor(context, R.color.common_blue_purple);
        mSectionText = ContextCompat.getColor(context, R.color.common_white);
        mSectionBackground = ContextCompat.getColor(context, R.color.background_1);
    }

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new FastScrollItemDecoration(this));
    }

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
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (mBrandListener != null
                && v.getTag() != null && v.getTag() instanceof Integer) {
            mBrandListener.onEvent(getItem((int) v.getTag()));
        }
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    @Override
    public Map<String, Integer> getIndex() {
        return mIndex;
    }

    // Section
    @Override
    public String[] getSections() {
        return mSections;
    }

    @Override
    public void setCurrentSection(String section) {
        mCurrentSection = section;
    }

    @Override
    public String getCurrentSection() {
        return mCurrentSection;
    }

    @Override
    public void setShowSection(boolean isShow) {
        mIsShowSection = isShow;
    }

    @Override
    public boolean getShowSection() {
        return mIsShowSection;
    }

    @Override
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    @Override
    public float getTopPadding() {
        return mTopPadding;
    }

    @Override
    public boolean isAddPoint() {
        return true;
    }

    // Size
    @Override
    public float getLetterTextSize() {
        return mLetterTextSize;
    }

    @Override
    public float getPointTextSize() {
        return mPointTextSize;
    }

    @Override
    public float getSectionTextSize() {
        return mSectionThumbTextSize;
    }

    @Override
    public float getSectionViewSize() {
        return mSectionThumbSize;
    }

    @Override
    public float getSectionViewPadding() {
        return mSectionThumbPadding;
    }

    // Color
    @Override
    public int getColorLetterTextNormal() {
        return mLetterTextNormal;
    }

    @Override
    public int getColorLetterTextSelect() {
        return mLetterTextSelect;
    }

    @Override
    public int getColorSectionText() {
        return mSectionText;
    }

    @Override
    public int getColorSectionBackground() {
        return mSectionBackground;
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
                if (t.toLowerCase().contains(text)) {
                    f.add(b);
                }
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
            setScrollIndex(index);
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

    private void setScrollIndex(LinkedHashMap<String, Integer> index) {
        // Index
        mIndex = index;
        // Sections
        ArrayList<String> list = new ArrayList<>(index.keySet());
        mSections = new String[list.size()];
        int i = 0;
        for (String c : list) {
            mSections[i++] = c;
        }
        notifyDataSetChanged();
    }

    ////////////////////////////////////////////////
}