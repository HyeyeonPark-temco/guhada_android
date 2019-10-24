package io.temco.guhada.view.custom.dialog;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.CustomLog;
import io.temco.guhada.data.model.Category;
import io.temco.guhada.databinding.DialogCategoryListBinding;
import io.temco.guhada.view.adapter.category.DialogCategoryFirstListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class CategoryListDialog extends BaseDialog<DialogCategoryListBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnCategoryListener mListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE ListBottomSheetFragment
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_category_list;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
        mBinding.layoutSubMenu.setClickListener(this);

        // List
        CommonUtil.delayRunnable(this::initList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            ////////////////////////////////////////////////
            case R.id.layout_close:
            case R.id.image_close:
                dismiss();
                break;

            // Sub Menu
            case R.id.layout_sale:
                CommonUtil.debug("layout_sale");
                break;

            case R.id.layout_new_product:
                CommonUtil.debug("layout_new_product");
                break;

            case R.id.layout_power_deal:
                CommonUtil.debug("layout_power_deal");
                break;

            case R.id.layout_time_deal:
                CommonUtil.debug("layout_time_deal");
                break;

            case R.id.layout_limit_price:
                CommonUtil.debug("layout_limit_price");
                break;

            case R.id.layout_community:
                CommonUtil.debug("layout_community");
                break;

            ////////////////////////////////////////////////
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnCategoryListener(OnCategoryListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void dissmissWithData(int index, Category data) {
        if (mListener != null) {
            mListener.onEvent(data);
        }
        dismiss();
    }

    private void clickHeaderData(int index, Category data) {
        if(CustomLog.getFlag())CustomLog.L("clickHeaderData","index",index);
    }

    private void initList() {
        // Category
        List<Category> temp = ((BaseApplication)getActivity().getApplicationContext()).getCategoryList();
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        DialogCategoryFirstListAdapter adapter = new DialogCategoryFirstListAdapter(getContext());
        adapter.setOnCategoryListener(this::dissmissWithData);
        adapter.setmCategoryHeaderListListener(this::clickHeaderData);
        adapter.setItems(temp);
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
}