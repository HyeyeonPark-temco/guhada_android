package io.temco.guhada.view.fragment.findaccount;

import io.temco.guhada.R;
import io.temco.guhada.data.viewmodel.account.FindAccountViewModel;
import io.temco.guhada.databinding.FragmentFindidBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

/**
 * 아이디 찾기 Fragment
 * @author Hyeyeon Park
 */
public class FindIdFragment extends BaseFragment<FragmentFindidBinding> {
    private FindAccountViewModel mViewModel;

    public FindIdFragment() {}

    public FindAccountViewModel getmVewModel() {
        return mViewModel;
    }

    public void setmVewModel(FindAccountViewModel mVewModel) {
        this.mViewModel = mVewModel;
    }

    @Override
    protected String getBaseTag() {
        return FindIdFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_findid;
    }

    @Override
    protected void init() {
        if(mViewModel != null){
            mBinding.setViewModel(mViewModel);
            mBinding.includeFindidResult.setViewModel(mViewModel);
            mBinding.includeFindidResult.linearlayoutFindaccountResult.bringToFront();
            mBinding.executePendingBindings();
        }
    }


}
