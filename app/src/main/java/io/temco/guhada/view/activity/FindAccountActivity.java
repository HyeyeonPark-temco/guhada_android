package io.temco.guhada.view.activity;

import android.widget.Toast;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnFindAccountListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.data.viewmodel.FindAccountViewModel;
import io.temco.guhada.databinding.ActivityFindaccountBinding;
import io.temco.guhada.view.fragment.findaccount.FindIdFragment;
import io.temco.guhada.view.activity.base.BindActivity;
import io.temco.guhada.view.adapter.FindAccountPagerAdapter;

public class FindAccountActivity extends BindActivity<ActivityFindaccountBinding> {

    @Override
    protected String getBaseTag() {
        return FindAccountActivity.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_findaccount;
    }

    @Override
    protected Type.View getViewType() {
        return Type.View.FIND_ACCOUNT;
    }

    @Override
    protected void init() {
        FindAccountViewModel viewModel = new FindAccountViewModel();
        viewModel.setFindAccountListener(new OnFindAccountListener() {
            @Override
            public void showSnackBar(String message) {
                CommonUtil.showSnackBar(mBinding.linearlayoutFiindaccountContainer, message, getResources().getColor(R.color.colorPrimary), (int) getResources().getDimension(R.dimen.height_header));
            }

            @Override
            public void showMessage(String message) {
                Toast.makeText(FindAccountActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeActivity() {
                finish();
            }

            @Override
            public void hideKeyboard() {
                CommonUtil.hideKeyboard(getApplicationContext(), mBinding.linearlayoutFiindaccountContainer);
            }
        });
        mBinding.setViewModel(viewModel);
        mBinding.includeFindaccountHeader.setViewModel(viewModel);

        // PAGER
        FindAccountPagerAdapter adapter = new FindAccountPagerAdapter(getSupportFragmentManager());
//        FindIdFragment findIdFragment = new FindIdFragment(viewModel);

        adapter.addFragment(new FindIdFragment(viewModel));
//        adapter.addFragment(new FindIdFragment(viewModel));
        mBinding.viewpagerFindaccount.setAdapter(adapter);

        mBinding.executePendingBindings();
    }

}
