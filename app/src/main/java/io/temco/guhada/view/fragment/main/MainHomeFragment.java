package io.temco.guhada.view.fragment.main;

import android.view.View;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.databinding.FragmentMainHomeBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class MainHomeFragment extends BaseFragment<FragmentMainHomeBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDrawerLayoutListener mDrawerListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return MainHomeFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected void init() {
        initHeader();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_side_menu:
                if (mDrawerListener != null) {
                    mDrawerListener.onDrawerEvnet(true);
                }
                break;

            case R.id.image_search:
                CommonUtil.debug("image_search");
                break;

            case R.id.image_shop_cart:
                CommonUtil.debug("image_shop_cart");
                break;

        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    public void setOnDrawerLayoutListener(OnDrawerLayoutListener listener) {
        mDrawerListener = listener;
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initHeader() {
        mBinding.layoutHeader.setClickListener(this);

        // Tab
        // mBinding.layoutHeader.viewTabStrip.setTitles("홈", "여성", "남성", "키즈", "신상품");
    }

    ////////////////////////////////////////////////
}
