package io.temco.guhada.view.custom.dialog;

import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnProductOrderListener;
import io.temco.guhada.databinding.DialogProductOrderBinding;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class ProductOrderDialog extends BaseDialog<DialogProductOrderBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnProductOrderListener mOrderListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_product_order;
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

            // Order
            case R.id.text_new_product:
                setType(Type.ProductOrder.NEW_PRODUCT);
                break;

            case R.id.text_marks:
                setType(Type.ProductOrder.MARKS);
                break;

            case R.id.text_low_price:
                setType(Type.ProductOrder.LOW_PRICE);
                break;

            case R.id.text_high_price:
                setType(Type.ProductOrder.HIGH_PRICE);
                break;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnProductOrderListener(OnProductOrderListener listener) {
        mOrderListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void setType(Type.ProductOrder type) {
        if (mOrderListener != null) mOrderListener.onOrder(type);
        dismiss();
    }

    ////////////////////////////////////////////////
}