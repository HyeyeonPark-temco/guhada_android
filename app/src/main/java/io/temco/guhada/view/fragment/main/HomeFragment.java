package io.temco.guhada.view.fragment.main;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import io.temco.guhada.R;
import io.temco.guhada.common.listener.OnDrawerLayoutListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.databinding.FragmentMainHomeBinding;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class HomeFragment extends BaseFragment<FragmentMainHomeBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private OnDrawerLayoutListener mDrawerListener;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return HomeFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_home;
    }

    @Override
    protected void init() {
        initHeader();
        setLinkText();
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

    private void setLinkText() {
        // Sales Number
        mBinding.layoutInformation.textInformationSalesNumber.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationSalesNumber.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_company_sales_number, R.string.information_confirm_company,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Company Info!");
                    }
                }), TextView.BufferType.SPANNABLE);
        // Description
        mBinding.layoutInformation.textInformationDescription.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationDescription.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_description, R.string.information_confirm_service,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Sevice Confirm!");
                    }
                }), TextView.BufferType.SPANNABLE);
    }

    ////////////////////////////////////////////////
}
