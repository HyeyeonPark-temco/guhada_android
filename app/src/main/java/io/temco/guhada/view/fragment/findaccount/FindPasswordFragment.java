package io.temco.guhada.view.fragment.findaccount;

import io.temco.guhada.R;
import io.temco.guhada.common.util.CountTimer;
import io.temco.guhada.data.viewmodel.FindPasswordViewModel;
import io.temco.guhada.databinding.FragmentFindpasswordBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class FindPasswordFragment extends BaseFragment<FragmentFindpasswordBinding> {

    private FindPasswordViewModel mViewModel;

    public FindPasswordFragment() {
    }

    public FindPasswordViewModel getmViewModel() {
        return mViewModel;
    }

    public void setmViewModel(FindPasswordViewModel mViewModel) {
        this.mViewModel = mViewModel;
    }

    @Override
    protected String getBaseTag() {
        return FindPasswordFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_findpassword;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CountTimer.stopTimer();
    }

    @Override
    protected void init() {
        mBinding.setViewModel(mViewModel);
        mBinding.constraintlayoutFindpwdResult.bringToFront();
        mBinding.executePendingBindings();
    }

    public void clearVerifyNumber() {
        mBinding.edittextFindpwdPhoneverify.setText("");
    }
}
