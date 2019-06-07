package io.temco.guhada.view.custom.dialog;

import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnBrandListener;
import io.temco.guhada.databinding.DialogBrandListBinding;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class BrandListDialog extends BaseDialog<DialogBrandListBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnBrandListener mListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_brand_list;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_close:
                dismiss();
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnBrandListener(OnBrandListener listener) {
        mListener = listener;
    }

    ////////////////////////////////////////////////
}