package io.temco.guhada.view.activity;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;

import io.temco.guhada.R;
import io.temco.guhada.common.BaseApplication;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnJoinListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.viewmodel.JoinViewModel;
import io.temco.guhada.databinding.ActivityJoinBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class JoinActivity extends BindActivity<ActivityJoinBinding> {
    private JoinViewModel mViewModel;

    @Override
    protected String getBaseTag() {
        return JoinActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_join;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.JOIN;
    }

    @Override
    protected void init() {
        mViewModel = new JoinViewModel(new OnJoinListener() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(JoinActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity() {
                finish();
            }

            @Override
            public void showSnackBar(String message) {
                Resources resources = BaseApplication.getInstance().getResources();
                CommonUtil.showSnackBar(mBinding.linearlayoutJoinForm, message);
            }
        });
        mViewModel.setToolBarTitle(getResources().getString(R.string.join_title));
        mBinding.includeJoinHeader.setViewModel(mViewModel);
        mBinding.setViewModel(mViewModel);
        setTextWatchers();
        mBinding.executePendingBindings();
    }

    private void setTextWatchers() {
        mBinding.edittextJoinEmail.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextJoinPassword.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.edittextJoinConfirmpassword.setTextWatcher(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setConfirmPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
