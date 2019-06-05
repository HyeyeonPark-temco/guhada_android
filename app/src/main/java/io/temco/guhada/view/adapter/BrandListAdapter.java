package io.temco.guhada.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.decoration.FastScrollItemDecoration;
import io.temco.guhada.common.listener.OnFastScrollListener;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.databinding.ItemBrandListHeaderBinding;
import io.temco.guhada.view.adapter.base.BaseBrandViewHolder;
import io.temco.guhada.view.adapter.base.StickyHeaderRecyclerAdapter;
import io.temco.guhada.view.adapter.holder.BrandListContentsViewHolder;
import io.temco.guhada.view.adapter.holder.BrandListHeaderViewHolder;

public class BrandListAdapter extends StickyHeaderRecyclerAdapter<BaseBrandViewHolder, Brand> implements OnFastScrollListener {

    // -------- LOCAL VALUE --------
    private Context mContext;
    private boolean mIsAlphabet = true;
    //
    private Map<String, Integer> mIndex;
    private String[] mSections;
    private String mCurrentSection;
    private boolean mIsShowSection = false;
    private int mChildHeight;
    private float mPointTextSize;
    private float mLetterTextSize;
    private float mLetterTextPadding;
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
        mChildHeight = context.getResources().getDimensionPixelSize(R.dimen.view_fast_scroll_brand_row);
        mPointTextSize = context.getResources().getDimension(R.dimen.text_8);
        mLetterTextSize = context.getResources().getDimension(R.dimen.text_10);
        mLetterTextPadding = context.getResources().getDimension(R.dimen.padding_fast_scroll_brand_text);
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
    }

    @Override
    public void onBindHeaderData(View header, int headerPosition) {
        // Header
        ItemBrandListHeaderBinding binding = DataBindingUtil.bind(header);
        // Data

        Brand b = getItem(headerPosition);

        if (binding != null) {
            binding.textTitle.setText(mIsAlphabet ? getItem(headerPosition).nameEn : getItem(headerPosition).nameKo);
        }
    }

    ////////////////////////////////////////////////
    // LISTENER
    ////////////////////////////////////////////////

    @Override
    public Map<String, Integer> getIndex() {
        return mIndex;
    }

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
    public int getChildHeight() {
        return mChildHeight;
    }

    @Override
    public float getTopPadding() {
        return mLetterTextPadding;
    }

    @Override
    public boolean isAddPoint() {
        return true;
    }

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

    public void setScrollIndex(Map<String, Integer> index) {
        // Index
        mIndex = index;
        // Sections
        ArrayList<String> list = new ArrayList<>(index.keySet());
        mSections = new String[list.size()];
        int i = 0;
        for (String c : list) {
            mSections[i++] = c;
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}