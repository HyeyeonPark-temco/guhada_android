package io.temco.guhada.view.custom.dialog;

import android.text.TextUtils;
import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.databinding.DialogMessageBinding;
import io.temco.guhada.view.custom.dialog.base.BaseDialog;

public class MessageDialog extends BaseDialog<DialogMessageBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private String mMessage;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_message;
    }

    @Override
    protected void init() {
        mBinding.setClickListener(this);

        // Message
        if (!TextUtils.isEmpty(mMessage)) mBinding.textMessage.setText(mMessage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_confirm:
                dismissAllowingStateLoss();
                break;
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setMessage(String message) {
        mMessage = message;
    }

    ////////////////////////////////////////////////
}