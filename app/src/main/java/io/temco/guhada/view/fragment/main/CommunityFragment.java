package io.temco.guhada.view.fragment.main;

import io.temco.guhada.R;
import io.temco.guhada.databinding.FragmentMainCommunityBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class CommunityFragment extends BaseFragment<FragmentMainCommunityBinding> {

    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return CommunityFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_community;
    }

    @Override
    protected void init() {

    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
}
