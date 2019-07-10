package io.temco.guhada.view.fragment.main;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

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
        setTabLayout();
    }

    private void setTabLayout() {
        // Remove
        if (mBinding.layoutTab.getChildCount() > 0) {
            mBinding.layoutTab.removeAllTabs();
        }
        // Tab
        // Dummy
        String[] titles = getResources().getStringArray(R.array.main_titles);
        for (String t : titles) {
            addCustomTabs(t, false);
        }
    }

    private void addCustomTabs(String title, boolean isSelect) {
        if (getContext() != null) {
            View v = getLayoutInflater().inflate(R.layout.layout_tab_category, null);
            ((TextView) v.findViewById(R.id.text_title)).setText(title);
            TabLayout.Tab tab = mBinding.layoutTab.newTab().setCustomView(v);
            mBinding.layoutTab.addTab(tab);
            if (isSelect) tab.select();
        }
    }

    private void setLinkText() {
        // Sales Number
        mBinding.layoutInformation.textInformationSalesNumber.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationSalesNumber.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_company_sales_number_en, R.string.information_confirm_company_en,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Company Info!");
                    }
                }), TextView.BufferType.SPANNABLE);
        // Description
        mBinding.layoutInformation.textInformationDescription.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.layoutInformation.textInformationDescription.setText(TextUtil.createTextWithLink(getContext(),
                R.string.information_description_en, R.string.information_confirm_service_en,
                R.dimen.text_11, true,
                () -> {
                    if (true) {
                        CommonUtil.debug("Sevice Confirm!");
                    }
                }), TextView.BufferType.SPANNABLE);
    }

    ////////////////////////////////////////////////
}
