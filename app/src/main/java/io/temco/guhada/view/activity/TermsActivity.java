package io.temco.guhada.view.activity;

import android.widget.Toast;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnTermsListener;
import io.temco.guhada.data.viewmodel.TermsViewModel;
import io.temco.guhada.databinding.ActivityTermsBinding;
import io.temco.guhada.view.activity.base.BindActivity;

public class TermsActivity extends BindActivity<ActivityTermsBinding> {
    private TermsViewModel mViewModel;

    @Override
    protected String getBaseTag() {
        return TermsActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.TERMS;
    }

    @Override
    protected void init() {
        mViewModel = new TermsViewModel(new OnTermsListener() {
            @Override
            public void showMessage(String message) {
                Toast.makeText(TermsActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity() {
                finish();
            }
        });
        mBinding.setViewModel(mViewModel);
        mBinding.includeTermsHeader.setViewModel(mViewModel);
        mBinding.executePendingBindings();
    }
}
