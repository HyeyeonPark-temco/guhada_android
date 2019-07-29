package io.temco.guhada.view.activity;

import android.content.Intent;
import android.widget.Toast;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnTermsListener;
import io.temco.guhada.data.model.user.SnsUser;
import io.temco.guhada.data.viewmodel.account.TermsViewModel;
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
            public void closeActivity(int resultCode) {
//                SnsUser user = (SnsUser) getIntent().getSerializableExtra("user");
//                user.setAgreeCollectPersonalInfoTos(mViewModel.getUser().getAgreeCollectPersonalInfoTos());
//                user.setAgreeEmailReception(mViewModel.getUser().getAgreeEmailReception());
//                user.setAgreePurchaseTos(mViewModel.getUser().getAgreePurchaseTos());
//                user.setAgreeSaleTos(mViewModel.getUser().getAgreeSaleTos());
//                user.setAgreeSmsReception(mViewModel.getUser().getAgreeSmsReception());

                getIntent().putExtra("agreeCollectPersonalInfoTos", mViewModel.getUser().getAgreeCollectPersonalInfoTos());
                getIntent().putExtra("agreeEmailReception", mViewModel.getUser().getAgreeEmailReception());
                getIntent().putExtra("agreePurchaseTos", mViewModel.getUser().getAgreePurchaseTos());
                getIntent().putExtra("agreeSaleTos", mViewModel.getUser().getAgreeSaleTos());
                getIntent().putExtra("agreeSmsReception", mViewModel.getUser().getAgreeSmsReception());

                setResult(resultCode, getIntent());
                finish();
            }

            @Override
            public void redirectJoinActivity() {
                // startActivity(new Intent(TermsActivity.this, JoinActivity.class));
            }
        });
        mBinding.setViewModel(mViewModel);
        mBinding.includeTermsHeader.setViewModel(mViewModel);
        mBinding.executePendingBindings();
    }
}
