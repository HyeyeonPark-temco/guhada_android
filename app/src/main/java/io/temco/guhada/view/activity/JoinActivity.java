package io.temco.guhada.view.activity;

import android.widget.Toast;

import io.temco.guhada.R;
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
            public void closeActivity(int resultCode) {
                finish();
            }

            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutJoinForm, message);
            }
        });
        mViewModel.setToolBarTitle(getResources().getString(R.string.join_title));
        mBinding.includeJoinHeader.setViewModel(mViewModel);
        mBinding.setViewModel(mViewModel);
        mBinding.executePendingBindings();
    }

}
