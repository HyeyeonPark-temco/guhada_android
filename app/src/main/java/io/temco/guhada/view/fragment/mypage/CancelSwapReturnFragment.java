package io.temco.guhada.view.fragment.mypage;

import io.temco.guhada.R;
import io.temco.guhada.databinding.FragmentMypageCancelSwapReturnBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class CancelSwapReturnFragment extends BaseFragment<FragmentMypageCancelSwapReturnBinding> {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return CancelSwapReturnFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mypage_cancel_swap_return;
    }

    @Override
    protected void init() {

    }

    ////////////////////////////////////////////////
}