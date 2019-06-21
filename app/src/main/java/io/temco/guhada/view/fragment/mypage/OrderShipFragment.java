package io.temco.guhada.view.fragment.mypage;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.databinding.FragmentMypageOrderShipBinding;
import io.temco.guhada.view.adapter.mypage.OrderShipListAdapter;
import io.temco.guhada.view.fragment.base.BaseFragment;

public class OrderShipFragment extends BaseFragment<FragmentMypageOrderShipBinding> implements View.OnClickListener {

    // -------- LOCAL VALUE --------
    private RequestManager mRequestManager;
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    @Override
    protected String getBaseTag() {
        return OrderShipFragment.class.getSimpleName();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mypage_order_ship;
    }

    @Override
    protected void init() {
        mRequestManager = Glide.with(this);
        mBinding.layoutCalendar.setClickListener(this);
        //
        initList();
        initCalendar();
        setLinkText();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            ////////////////////////////////////////////////
            // Period
            case R.id.text_week:
                setPeriod(0);
                break;

            case R.id.text_month:
                setPeriod(1);
                break;

            case R.id.text_month_three:
                setPeriod(2);
                break;

            case R.id.text_year:
                setPeriod(3);
                break;

            ////////////////////////////////////////////////
            // Calendar
            case R.id.layout_date_from:
                CommonUtil.debug("Date_From");
                break;

            case R.id.layout_date_to:
                CommonUtil.debug("Date_To");
                break;

            case R.id.text_check:
                CommonUtil.debug("Check");
                break;

            ////////////////////////////////////////////////
        }
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private void initList() {
        OrderShipListAdapter adapter = new OrderShipListAdapter(getContext(), mRequestManager);
        adapter.setOnOrderShipListener(() -> {
            if (true) {
                CommonUtil.debug("onEvent");
            }
        });
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.listContents.setAdapter(adapter);
        // Test
        List l = new ArrayList();
        l.add(new Object());
        l.add(new Object());
        adapter.setItems(l);
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

    private void initCalendar() {
        setPeriod(0);
        //
        mBinding.layoutCalendar.textDateFrom.setText("");
        mBinding.layoutCalendar.textDateTo.setText("");
    }

    private void setPeriod(int position) {
        switch (position) {
            case 0:
                mBinding.layoutCalendar.textWeek.setSelected(true); //
                mBinding.layoutCalendar.textMonth.setSelected(false);
                mBinding.layoutCalendar.textMonthThree.setSelected(false);
                mBinding.layoutCalendar.textYear.setSelected(false);
                break;

            case 1:
                mBinding.layoutCalendar.textWeek.setSelected(false);
                mBinding.layoutCalendar.textMonth.setSelected(true); //
                mBinding.layoutCalendar.textMonthThree.setSelected(false);
                mBinding.layoutCalendar.textYear.setSelected(false);
                break;

            case 2:
                mBinding.layoutCalendar.textWeek.setSelected(false);
                mBinding.layoutCalendar.textMonth.setSelected(false);
                mBinding.layoutCalendar.textMonthThree.setSelected(true); //
                mBinding.layoutCalendar.textYear.setSelected(false);
                break;

            case 3:
                mBinding.layoutCalendar.textWeek.setSelected(false);
                mBinding.layoutCalendar.textMonth.setSelected(false);
                mBinding.layoutCalendar.textMonthThree.setSelected(false);
                mBinding.layoutCalendar.textYear.setSelected(true); //
                break;
        }
    }

    ////////////////////////////////////////////////
}