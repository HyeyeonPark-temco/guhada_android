package io.temco.guhada.view.custom.dialog;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import io.temco.guhada.R;
import io.temco.guhada.common.Preferences;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnCategoryListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.databinding.DialogCategoryListBinding;
import io.temco.guhada.view.adapter.expand.CategoryDialogExpandFirstListAdapter;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class CategoryListDialog extends BaseDialog<DialogCategoryListBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnCategoryListener mListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
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

    private void dissmissWithData(Type.Category type, int[] hierarchies) {
        if (mListener != null) {
            mListener.onEvent(type, hierarchies);
        }
        dismiss();
    }

    private void initList() {
        // Category
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        CategoryDialogExpandFirstListAdapter adapter = new CategoryDialogExpandFirstListAdapter(getContext());
        adapter.setOnCategoryListener(this::dissmissWithData);
        adapter.setItems(Preferences.getCategories());
        mBinding.listContents.setAdapter(adapter);
    }

    ////////////////////////////////////////////////
}