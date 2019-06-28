package io.temco.guhada.view.fragment.mypage;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.temco.guhada.R;
import io.temco.guhada.common.Type;
import io.temco.guhada.common.listener.OnOrderShipListener;
import io.temco.guhada.common.util.CommonUtil;
import io.temco.guhada.common.util.DateUtil;
import io.temco.guhada.common.util.TextUtil;
import io.temco.guhada.data.model.MyOrderItem;
import io.temco.guhada.databinding.FragmentMypageOrderShipBinding;
import io.temco.guhada.view.adapter.mypage.OrderShipListAdapter;
import io.temco.guhada.view.custom.dialog.MessageDialog;
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
        adapter.setOnOrderShipListener(new OnOrderShipListener() {
            @Override
            public void onEvent() {
            }

            @Override
            public void onReward(OrderShipListAdapter adapter, int position, MyOrderItem data) {
                //
                showRewardDialog();
                //
                data.checkReward = true;
                adapter.changeItems(position, data);
            }
        });
        mBinding.listContents.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.listContents.setAdapter(adapter);
        // Test
        List<MyOrderItem> l = new ArrayList<>();
        l.add(getTestItem(
                123456,
                "2019.06.28",
                "ACNE",
                "19FW",
                "슬림핏 하프넥 플라워 패턴 슬림핏 하프넥 플라워 패턴",
                "체리",
                "85",
                "1개",
                new BigDecimal("215000"),
                "제품 감정 완료"));
        l.add(getTestItem(
                123456,
                "2019.06.28",
                "ACNE",
                "19FW",
                "슬림핏 하프넥 플라워 패턴 슬림핏 하프넥 플라워 패턴",
                "체리",
                "85",
                "1개",
                new BigDecimal("215000"),
                "제품 감정 완료"));
        l.add(getTestItem(
                123456,
                "2019.06.28",
                "ACNE",
                "19FW",
                "슬림핏 하프넥 플라워 패턴 슬림핏 하프넥 플라워 패턴",
                "체리",
                "85",
                "1개",
                new BigDecimal("215000"),
                "제품 감정 완료"));
        adapter.setItems(l);
    }

    private MyOrderItem getTestItem(int orderNo,
                                    String date,
                                    String brand,
                                    String season,
                                    String productName,
                                    String option1,
                                    String option2,
                                    String option3,
                                    BigDecimal price,
                                    String status) {
        MyOrderItem i = new MyOrderItem();
        i.orderProdGroupId = orderNo;
        i.orderDate = date;
        i.brandName = brand;
        i.season = season;
        i.prodName = productName;
        i.optionAttribute1 = option1;
        i.optionAttribute2 = option2;
        i.optionAttribute3 = option3;
        i.discountPrice = price;
        i.purchaseStatusText = status;
        return i;
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
        String today = DateUtil.getTodayToString(Type.DateFormat.TYPE_1);
        mBinding.layoutCalendar.textDateFrom.setText(today);
        mBinding.layoutCalendar.textDateTo.setText(today);
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

    private void showRewardDialog() {
        MessageDialog md = new MessageDialog();
        md.setMessage(getString(R.string.mypage_order_reward_message));
        md.show(getFragmentManager(), getBaseTag());
    }

    ////////////////////////////////////////////////
}