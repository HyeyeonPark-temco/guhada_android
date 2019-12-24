package io.temco.guhada.view.custom.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnDetailSearchListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CommonUtilKotlin;
import io.temco.guhada.common.util.CommonViewUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Attribute;
import io.temco.guhada.data.model.Brand;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.data.model.Filter;
import io.temco.guhada.data.model.body.FilterEtcBody;
import io.temco.guhada.data.viewmodel.DetailSearchDialogViewModel;
import io.temco.guhada.databinding.DialogDetailSearchBinding;
import io.temco.guhada.view.activity.base.BaseActivity;
import io.temco.guhada.view.adapter.DetailSearchCategoryListAdapter;
import io.temco.guhada.view.adapter.brand.DetailSearchBrandListAdapter;
import io.temco.guhada.view.adapter.filter.FilterListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class DetailSearchDialog extends BaseDialog<DialogDetailSearchBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDetailSearchListener mDetailSearchListener;
    private DetailSearchBrandListAdapter mBrandListAdapter;
    private boolean mIsChangeData = false;
    private DetailSearchDialogViewModel mViewModel;
    // Category
    private int mParentCategoryId;
    private int[] mParentCategoryHierarchy;
    private String mParentCategoryDepth;
    private List<Category> mCategoryList;
    private List<Category> mCategoryAllList;

    private Map<Integer,Map<Integer, Category>> mDepthTitle;
    private HashMap<Integer,Integer> mDepthIndexMap;

    private int mDepthIndex;
    private int[] mSelectedCategoryHierarchy;
    // Brand
    private List<Brand> mBrandList;
    private List<Brand> mBrandSelectedList;
    // Filter
    private List<Filter> mFilterList;
    private FilterEtcBody mEtcBody;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_detail_search;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mDepthIndexMap = null;
        mViewModel = new DetailSearchDialogViewModel(this.getContext());
        mBinding.setViewModel(mViewModel);
        // Data
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_close:
            case R.id.image_close:
                dismiss();
                break;

            ////////////////////////////////////////////////
            // Brand
            case R.id.image_alphabet:
                changeBrandLanguage(true);
                break;

            case R.id.image_hangul:
                changeBrandLanguage(false);
                break;

            // Bottom
            case R.id.layout_reset:
                //reset();
                mDetailSearchListener.onReset(true);
                dismiss();
                break;

            case R.id.text_result:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() == 5){
                    String min = mBinding.listPriceMin.getText().toString().replace(".","").replace(",","").replace("-","");
                    String max = mBinding.listPriceMax.getText().toString().replace(".","").replace(",","").replace("-","");
                    if(!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max) && min.matches("^[0-9]*$") && max.matches("^[0-9]*$") && Integer.parseInt(max) > Integer.parseInt(min)){
                        if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                        mEtcBody.setPriceConditionIndex(5);
                        mEtcBody.setPriceConditionMin(Integer.parseInt(min));
                        mEtcBody.setPriceConditionMax(Integer.parseInt(max));
                        mIsChangeData = true;
                        changeDataEvent();
                        dismiss();
                    }else{
                        CommonViewUtil.INSTANCE.showDialog((BaseActivity)getContext(), "입력한 검색 가격값을 확인해 주세요.",false,false);
                    }
                }else{
                    changeDataEvent();
                    dismiss();
                }
                break;

            case R.id.list_shipinfo1:
                mViewModel.getDetailSearchDialogShipInfo1().set(!mViewModel.getDetailSearchDialogShipInfo1().get());
                if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                mEtcBody.setShippingConditionFlag1(!mEtcBody.getShippingConditionFlag1());
                mIsChangeData = true;
                break;

            case R.id.list_shipinfo2:
                mViewModel.getDetailSearchDialogShipInfo2().set(!mViewModel.getDetailSearchDialogShipInfo2().get());
                if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                mEtcBody.setShippingConditionFlag2(!mEtcBody.getShippingConditionFlag2());
                mIsChangeData = true;
                break;

            case R.id.list_pdtCdtinfo1:
                mViewModel.getDetailSearchDialogPdtCdt1().set(!mViewModel.getDetailSearchDialogPdtCdt1().get());
                if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                mEtcBody.setProductConditionFlag1(!mEtcBody.getProductConditionFlag1());
                mIsChangeData = true;
                break;

            case R.id.list_pdtCdtinfo2:
                mViewModel.getDetailSearchDialogPdtCdt2().set(!mViewModel.getDetailSearchDialogPdtCdt2().get());
                if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                mEtcBody.setProductConditionFlag2(!mEtcBody.getProductConditionFlag2());
                mIsChangeData = true;
                break;

            case R.id.list_price0:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 0){
                    mViewModel.getDetailSearchDialogPriceInfo().set(0);
                    if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                    mEtcBody.setPriceConditionIndex(0);
                    mEtcBody.setPriceConditionMin(0);
                    mEtcBody.setPriceConditionMax(0);
                    mIsChangeData = true;
                }
                break;
            case R.id.list_price1:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 1){
                    mViewModel.getDetailSearchDialogPriceInfo().set(1);
                    if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                    mEtcBody.setPriceConditionIndex(1);
                    mEtcBody.setPriceConditionMin(0);
                    mEtcBody.setPriceConditionMax(100000);
                    mIsChangeData = true;
                }
                break;
            case R.id.list_price2:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 2){
                    mViewModel.getDetailSearchDialogPriceInfo().set(2);
                    if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                    mEtcBody.setPriceConditionIndex(2);
                    mEtcBody.setPriceConditionMin(0);
                    mEtcBody.setPriceConditionMax(300000);
                    mIsChangeData = true;
                }
                break;
            case R.id.list_price3:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 3){
                    mViewModel.getDetailSearchDialogPriceInfo().set(3);
                    if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                    mEtcBody.setPriceConditionIndex(3);
                    mEtcBody.setPriceConditionMin(0);
                    mEtcBody.setPriceConditionMax(500000);
                    mIsChangeData = true;
                }
                break;
            case R.id.list_price4:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 4){
                    mViewModel.getDetailSearchDialogPriceInfo().set(4);
                    if(mEtcBody == null) mEtcBody = new FilterEtcBody();
                    mEtcBody.setPriceConditionIndex(4);
                    mEtcBody.setPriceConditionMin(0);
                    mEtcBody.setPriceConditionMax(1000000);
                    mIsChangeData = true;
                }
                break;
            case R.id.list_price5:
                if(mViewModel.getDetailSearchDialogPriceInfo().get() != 5){
                    mViewModel.getDetailSearchDialogPriceInfo().set(5);
                }
                break;

            ////////////////////////////////////////////////
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnDetailSearchListener(OnDetailSearchListener listener) {
        mDetailSearchListener = listener;
    }

    public void setCategoryData(String depth, int id, int[] hierarchies, List<Category> categories) {
        mParentCategoryDepth = depth;
        mParentCategoryId = id;
        mParentCategoryHierarchy = hierarchies;
        mCategoryList = categories;
    }

    public void setBrandData(List<Brand> brands) {
        mBrandList = brands;
    }

    public void setmDepthTitle(Map<Integer, Map<Integer, Category>> mDepthTitle) {
        this.mDepthTitle = mDepthTitle;
    }

    public void setFilterData(List<Filter> filters) {
        mFilterList = filters;
    }

    public void setEtcFilterBody(FilterEtcBody mEtcBody) {
        this.mEtcBody = mEtcBody;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void reset() {
        mBinding.layoutExpandCategoryContents.collapse(true);
        mBinding.layoutExpandBrandContents.collapse(true);
        resetData();
        initData();
    }

    private void initData() {
        mBinding.layoutProgress.setVisibility(View.VISIBLE);
        CommonUtil.delayRunnable(() -> {
            setCategoryData();
            setBrandData();
            initFilter(mFilterList);
            setEtcFilterData();
            mBinding.layoutProgress.setVisibility(View.GONE);
            mBinding.viewScrollContents.scrollTo(0, 0);
        });
    }

    private void resetData() {
        if (mCategoryList != null && mCategoryList.size() > 0) {
            for (Category c : mCategoryList) {
                c.isSelected = false;
            }
        }
        if (mBrandList != null && mBrandList.size() > 0) {
            for (Brand b : mBrandList) {
                b.isSelected = false;
            }
        }
        if (mFilterList != null && mFilterList.size() > 0) {
            for (Filter c : mFilterList) {
                if (c.attributes != null && c.attributes.size() > 0) {
                    for (Attribute a : c.attributes) {
                        a.selected = false;
                    }
                }
            }
        }
    }


    private void changeDataEvent() {
        if(CustomLog.getFlag())CustomLog.L("changeDataEvent","mIsChangeData",mIsChangeData ,"mDepthTitle null",(mDepthTitle==null));
        if(!TextUtils.isEmpty(mBinding.listSearchWord.getText().toString())){
            if(mEtcBody == null) mEtcBody = new FilterEtcBody();
            mEtcBody.setSearchWord(mBinding.listSearchWord.getText().toString());
            mIsChangeData = true;
        }
        if (mIsChangeData && mDetailSearchListener != null) {
            if (mCategoryList != null) mDetailSearchListener.onCategory(mCategoryList);
            if (mDepthTitle != null) mDetailSearchListener.onCategoryResult(mDepthTitle);
            if (mBrandList != null) mDetailSearchListener.onBrand(mBrandList);
            if (mFilterList != null) mDetailSearchListener.onFilter(mFilterList);
            if (mEtcBody != null) mDetailSearchListener.onSearchEtc(mEtcBody);
            mDetailSearchListener.onChange(true);
        }
    }

    // Category
    private void setCategoryData() {
        // Depth
        if (!TextUtils.isEmpty(mParentCategoryDepth)) {
            mBinding.layoutHeaderCategory.setDepth(mParentCategoryDepth);
        }
        // List
        if (mCategoryList != null && mCategoryList.size() > 0) {
            mCategoryAllList = new ArrayList<>();
            if(mDepthTitle == null) mDepthTitle = new HashMap<>();
            if(mDepthIndexMap == null) mDepthIndexMap = new HashMap<>();

            mBinding.layoutHeaderCategory.imageExpand.setVisibility(View.VISIBLE);
            mBinding.layoutExpandCategoryHeader.setToggleOnClick(true);
            //mDepthIndex = -1;
            for (int i=0;i<mCategoryList.size();i++) {
                mDepthIndex = 0;
                Category c = mCategoryList.get(i);
                Gson gson = new Gson();
                Category tmp = gson.fromJson(gson.toJson(c), Category.class);
                tmp.depth = 0;
                mDepthIndex++;
                boolean isAllSelect = setDepthList(tmp.depth, tmp, mDepthIndex);
                if(!tmp.children.isEmpty()){
                    tmp.children.add(0, CommonUtil.createAllCategoryData(getContext().getString(R.string.category_all), tmp.fullDepthName, tmp.id, tmp.hierarchies,isAllSelect,-1,1));
                    for (int i1=0;i1<tmp.children.size();i1++){
                        Category c1 = tmp.children.get(i1);
                        c1.parentId = c.id;
                        c1.depth = 1;
                        mDepthIndex++;
                        isAllSelect = setDepthList(c1.depth, c1, mDepthIndex);
                        if(c1.children !=null && !c1.children.isEmpty()){
                            c1.children.add(0, CommonUtil.createAllCategoryData(getContext().getString(R.string.category_all), c1.fullDepthName, c1.id, c1.hierarchies,isAllSelect,tmp.id,2));
                            for (int i2=0;i2<c1.children.size();i2++){
                                Category c2 = c1.children.get(i2);
                                c2.parentId = c1.id;
                                c2.depth = 2;
                                mDepthIndex++;
                                isAllSelect = setDepthList(c2.depth, c2, mDepthIndex);
                                if(c2.children !=null && !c2.children.isEmpty()){
                                    c2.children.add(0, CommonUtil.createAllCategoryData(getContext().getString(R.string.category_all), c2.fullDepthName, c2.id, c2.hierarchies,isAllSelect,c1.id,3));
                                    for (int i3=0;i3<c2.children.size();i3++){
                                        Category c3 = c2.children.get(i3);
                                        c3.parentId = c2.id;
                                        c3.depth = 3;
                                        mDepthIndex++;
                                        isAllSelect = setDepthList(c3.depth, c3, mDepthIndex);
                                        if(c3.children !=null && !c3.children.isEmpty()){
                                            c3.children.add(0, CommonUtil.createAllCategoryData(getContext().getString(R.string.category_all), c3.fullDepthName, c3.id, c3.hierarchies,isAllSelect,c2.id,4));
                                            for (int i4=0;i4<c3.children.size();i4++){
                                                Category c4 = c3.children.get(i4);
                                                c4.parentId = c3.id;
                                                c4.depth = 4;
                                                mDepthIndex++;
                                                isAllSelect = setDepthList(c4.depth, c4, mDepthIndex);
                                                if(c4.children !=null && !c4.children.isEmpty()){
                                                    c4.children.add(0, CommonUtil.createAllCategoryData(getContext().getString(R.string.category_all), c4.fullDepthName, c4.id, c4.hierarchies,isAllSelect,c3.id,5));
                                                    for (int i5=0;i4<c4.children.size();i5++){
                                                        Category c5 = c4.children.get(i5);
                                                        c5.parentId = c5.id;
                                                        c5.depth = 5;
                                                        mDepthIndex++;
                                                        setDepthList(c5.depth, c5, mDepthIndex);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                mCategoryAllList.add(tmp);
            }
            if(CustomLog.getFlag())CustomLog.L("CategoryListViewHolder","mDepthIndexMap 0",mDepthIndexMap.toString());
            // Add All
            initCategoryList(mCategoryAllList);
        } else {
            mBinding.layoutHeaderCategory.imageExpand.setVisibility(View.GONE);
            mBinding.layoutExpandCategoryHeader.setToggleOnClick(false);
        }
    }


    private boolean setDepthList(int depth, Category category,int index){
        boolean isAllSelect = false;
        if(mDepthTitle!=null && mDepthTitle.containsKey(depth) && mDepthTitle.get(depth).containsKey(category.id)){
            category.isSelected = true;
            category.isExpand = true;
            if(category.children!=null && category.children.size() > 1)mDepthIndexMap.put(depth,index);
            if(CustomLog.getFlag())CustomLog.L("CategoryListViewHolder","mDepthIndexMap","depth",depth,"index",index,category);
            if(category.children!=null && category.children.size() > 1 && mDepthTitle.get(depth).size() == category.children.size()) isAllSelect = true;
        }
        return isAllSelect;
    }


    private void initCategoryList(List<Category> data) {
        if(CustomLog.getFlag())CustomLog.L("CategoryListViewHolder initCategoryList",mDepthTitle.toString());
        mBinding.listCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        DetailSearchCategoryListAdapter adapter = new DetailSearchCategoryListAdapter();
        adapter.setMCategoryListener(this::checkSelectedCategoryList);
        adapter.setParentCategoryCount(data.size());
        adapter.setMDepthTitle(mDepthTitle);
        adapter.setMDepthIndex(mDepthIndexMap);
        adapter.setItems(data);
        mBinding.listCategory.setAdapter(adapter);
        refreshCategoryTitle();
    }

    private boolean checkSelectedCategoryHirarchy(int[] hirarchy) {
        if (hirarchy != null && hirarchy.length > 0) {
            if (mSelectedCategoryHierarchy == null) {
                mSelectedCategoryHierarchy = hirarchy;
                return false;
            }
            if (mSelectedCategoryHierarchy.length != hirarchy.length) {
                mSelectedCategoryHierarchy = hirarchy;
                return false;
            }
            //
            int length = hirarchy.length - 1;
            boolean check = true;
            if (length == 0) {
                if (mSelectedCategoryHierarchy[length] != hirarchy[length]) {
                    check = false;
                }
            } else {
                for (int i = 0; i < length; i++) {
                    if (mSelectedCategoryHierarchy[i] != hirarchy[i]) {
                        check = false;
                        break;
                    }
                }
            }
            mSelectedCategoryHierarchy = hirarchy;
            return check;
        } else {
            mSelectedCategoryHierarchy = null;
            return false;
        }
    }

    private void checkSelectedCategoryList(int position, Category category) {
        if(CustomLog.getFlag())CustomLog.L("checkSelectedCategoryList","position",position,"type",category.type.name(), "category",category);
        mIsChangeData = true;
        refreshCategoryTitle();
    }

    private void refreshCategoryTitle() {
        if (mDepthTitle != null && mDepthTitle.size() > 0) {
            String sb = "";
            Iterator<Integer> depthList = mDepthTitle.keySet().iterator();
            while (depthList.hasNext()){
                int depth = depthList.next();
                sb = "";
                Iterator<Integer> idList = mDepthTitle.get(depth).keySet().iterator();
                while (idList.hasNext()) {
                    int id = idList.next();
                    if(TextUtils.isEmpty(sb)) sb += mDepthTitle.get(depth).get(id).fullDepthName;
                    else sb += ", " + mDepthTitle.get(depth).get(id).title;
                }
            }
            mBinding.layoutHeaderCategory.setDepth(sb);
        } else {
            mBinding.layoutHeaderCategory.setDepth(null);
        }
    }

    // Brand
    private void setBrandData() {
        if (mBrandList != null && mBrandList.size() > 0) {
            if (mBrandList.size() == 1) {
                mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.GONE);
                mBinding.layoutExpandBrandHeader.setToggleOnClick(false);
                // Set Title
                mBrandList.get(0).isSelected = true;
                checkSelectedBrandList(mBrandList.get(0));
            } else {
                mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.VISIBLE);
                mBinding.layoutExpandBrandHeader.setToggleOnClick(true);
                // Set Data
                initBrandList(mBrandList);
                initSelectedBrandList(mBrandList);
            }
            refreshBrandTitle();
        } else {
            mBinding.layoutHeaderBrand.imageExpand.setVisibility(View.GONE);
            mBinding.layoutExpandBrandHeader.setToggleOnClick(false);
        }
    }


    private void setEtcFilterData(){
        if(mEtcBody != null){
            mViewModel.getDetailSearchDialogPdtCdt1().set(mEtcBody.getProductConditionFlag1());
            mViewModel.getDetailSearchDialogPdtCdt2().set(mEtcBody.getProductConditionFlag2());

            mViewModel.getDetailSearchDialogShipInfo1().set(mEtcBody.getShippingConditionFlag1());
            mViewModel.getDetailSearchDialogShipInfo2().set(mEtcBody.getShippingConditionFlag2());

            mViewModel.getDetailSearchDialogPriceInfo().set(mEtcBody.getPriceConditionIndex());
            if(mEtcBody.getPriceConditionIndex() == 5 && (mEtcBody.getPriceConditionMin() > 0 || mEtcBody.getPriceConditionMax() > 0)){
                mBinding.listPriceMin.setText((mEtcBody.getPriceConditionMin()+""));
                mBinding.listPriceMax.setText((mEtcBody.getPriceConditionMax()+""));
            }else{
                mBinding.listPriceMin.setText("");
                mBinding.listPriceMax.setText("");
            }
            if(!TextUtils.isEmpty(mEtcBody.getSearchWord())){
                mBinding.listSearchWord.setText(mEtcBody.getSearchWord());
            }
        }
        mBinding.layoutHeaderPrice.imageExpand.setVisibility(View.VISIBLE);
        mBinding.layoutExpandPriceHeader.setToggleOnClick(true);
    }

    private void initBrandList(List<Brand> data) {
        mBinding.layoutSearch.setClickListener(this);
        selectBrandLanguage(true);
        // Adapter
        mBrandListAdapter = new DetailSearchBrandListAdapter(getContext());
        mBrandListAdapter.setOnBrandListener(brand -> {
            if (brand != null && brand.type != Type.List.HEADER) {
                boolean result = changeBrandData(brand);
                if (!mIsChangeData) mIsChangeData = result;
            }
        });
        mBrandListAdapter.initBrandData(data);
        // List
        mBinding.listBrand.setHasFixedSize(true);
        mBinding.listBrand.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.listBrand.setAdapter(mBrandListAdapter);
        // EditText
        mBinding.layoutSearch.edittextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setBrandFilter(s.toString());
            }
        });
    }

    private void changeBrandLanguage(boolean isAlphabet) {
        if (mBrandListAdapter != null) {
            selectBrandLanguage(isAlphabet);
            mBrandListAdapter.changeLanguage(isAlphabet);
            mBinding.layoutSearch.edittextSearch.setText(null);
        }
    }

    private void setBrandFilter(String text) {
        if (mBrandListAdapter != null) {
            mBinding.listBrand.scrollToPosition(0);
            if (TextUtils.isEmpty(text)) {
                CommonUtil.delayRunnable(() -> mBrandListAdapter.resetFilterToOriginal());
            } else {
                mBrandListAdapter.filter(text);
            }
        }
    }

    private void selectBrandLanguage(boolean isAlphabet) {
        mBinding.layoutSearch.imageAlphabet.setSelected(isAlphabet);
        mBinding.layoutSearch.imageHangul.setSelected(!isAlphabet);
    }

    private boolean changeBrandData(Brand brand) {
        if (mBrandList != null && mBrandList.size() > 0) {
            for (Brand b : mBrandList) {
                if (b.id == brand.id) {
                    b.isSelected = brand.isSelected;
                    checkSelectedBrandList(brand);
                    refreshBrandTitle();
                    return true;
                }
            }
        }
        return false;
    }

    private void initSelectedBrandList(List<Brand> brands) {
        if (brands != null && brands.size() > 0) {
            mBrandSelectedList = new ArrayList<>();
            for (Brand b : brands) {
                if (b.isSelected) {
                    mBrandSelectedList.add(b);
                }
            }
        }
    }

    private void checkSelectedBrandList(Brand brand) {
        if (mBrandSelectedList == null) {
            mBrandSelectedList = new ArrayList<>();
        } else {
            if (mBrandSelectedList.size() > 0) {
                for (Brand b : mBrandSelectedList) {
                    if (b.id == brand.id) {
                        if (!brand.isSelected) {
                            mBrandSelectedList.remove(b);
                        }
                        break;
                    }
                }
            }
        }
        if (brand.isSelected) {
            mBrandSelectedList.add(brand);
        }
    }

    private void refreshBrandTitle() {
        if (mBrandSelectedList != null && mBrandSelectedList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mBrandSelectedList.size(); i++) {
                sb.append(mBrandSelectedList.get(i).nameDefault);
                if (i != mBrandSelectedList.size() - 1) sb.append(", ");
            }
            mBinding.layoutHeaderBrand.setDepth(sb.toString());
        } else {
            mBinding.layoutHeaderBrand.setDepth(null);
        }
    }

    // Filter
    private void initFilter(List<Filter> filters) {
        if (filters != null && filters.size() > 0) {
            FilterListAdapter adapter = new FilterListAdapter(getContext());
            adapter.setOnFilterListener((id, attributes) -> {
                boolean result = changeFilterData(id, attributes);
                if (!mIsChangeData) mIsChangeData = result;
            });
            adapter.setItems(filters);
            mBinding.listFilter.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.listFilter.setAdapter(adapter);
        }
    }

    private boolean changeFilterData(int id, List<Attribute> attributes) {
        if (mFilterList != null && mFilterList.size() > 0) {
            for (Filter f : mFilterList) {
                if (f.id == id) {
                    f.attributes = attributes;
                    if(CustomLog.INSTANCE.getFlag())CustomLog.INSTANCE.L("changeFilterData","f.attributes",f.attributes.toString());
                    return true;
                }
            }
        }
        return false;
    }

    ////////////////////////////////////////////////
    ////////////////////////////////////////////////
}