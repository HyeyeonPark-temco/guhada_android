package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnOrderShipListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.common.util.TextUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.MyOrderItem
import io.temco.guhada.data.viewmodel.mypage.MyPageDeliveryViewModel
import io.temco.guhada.databinding.CustomlayoutMypageDeliveryBinding
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.mypage.OrderShipListAdapter
import io.temco.guhada.view.custom.CustomCalendarFilter
import io.temco.guhada.view.custom.dialog.MessageDialog
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import java.math.BigDecimal
import java.util.*

/**
 * created 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 주문배송 화면
 * @author Hyeyeon Park
 * @since 2019.07.26
 *
 */
class MyPageDeliveryLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageDeliveryBinding, MyPageDeliveryViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener, CustomCalendarFilter.CustomCalendarListener {
    // -------- LOCAL VALUE --------
    private var mRequestManager: RequestManager? = null

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_delivery
    override fun init() {
        mViewModel = MyPageDeliveryViewModel(context)
        mBinding.calendarfilterMypageDeliver.mListener = this
        mViewModel.orderHistoryList.observe(this, androidx.lifecycle.Observer {
            val list = it.orderItemList

        })

        mRequestManager = Glide.with(this)
        //
        initList()
        //    initCalendar()
        setLinkText()
    }

    // CustomCalendarListener
    override fun onClickWeek() {
        ToastUtil.showMessage("week")
    }

    override fun onClickMonth() {
        ToastUtil.showMessage("month")
    }

    override fun onClickThreeMonth() {
        ToastUtil.showMessage("3 month")
    }

    override fun onClickYear() {
        ToastUtil.showMessage("year")
    }

    override fun onClickCheck() {
        mViewModel.startDate = mBinding.calendarfilterMypageDeliver.startDate
        mViewModel.endDate = mBinding.calendarfilterMypageDeliver.endDate
        mViewModel.getOrders()
    }

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun initList() {
        val adapter = OrderShipListAdapter(context, mRequestManager)
        adapter.setOnOrderShipListener(object : OnOrderShipListener {
            override fun onEvent() {}

            override fun onReward(adapter: OrderShipListAdapter, position: Int, data: MyOrderItem, message: String) {
                //
                showRewardDialog(message)
                //
                data.checkReward = true
                adapter.changeItems(position, data)
            }
        })
        mBinding.listContents.setLayoutManager(LinearLayoutManager(context))
        mBinding.listContents.setAdapter(adapter)
        // Test
        val l = ArrayList<MyOrderItem>()
        l.add(getTestItem(
                278453,
                "2019.06.28",
                "St.Jogn",
                "19SS",
                "Beanpole Ladies / 19SS Beige single a-line long trench coat",
                "Blue",
                "S",
                "Qty  1",
                BigDecimal("568500"),
                "Appraisal completed"))
        l.add(getTestItem(
                342507,
                "2019.06.17",
                "Missoni",
                "19SS",
                "Maison Kitsune / 19SS Women knit fox head pullover sweatshirts",
                "Camel",
                "M",
                "Qty  1",
                BigDecimal("547100"),
                "Appraisal completed"))
        l.add(getTestItem(
                827692,
                "2019.05.03",
                "MaisonKitsune",
                "19SS",
                "Burberry 19SS / Burberry house checks leather ankle boots",
                "Black",
                "36",
                "Qty  1",
                BigDecimal("958400"),
                "Appraisal completed"))
        /*l.add(getTestItem(
                278453,
                "2019.06.28",
                "St.Jogn",
                "19SS",
                "여성 의류 원피스 & 드레스 니랭스 원피스/드레스 no.5676 상품 by.SP",
                "라임",
                "S",
                "1개",
                new BigDecimal("568500"),
                "제품 감정 완료"));
        l.add(getTestItem(
                342507,
                "2019.06.17",
                "Missoni",
                "19SS",
                "여성 의류 니트 & 스웻 셔츠 가디건 no.5462 상품 by.SP",
                "카멜",
                "FREE",
                "1개",
                new BigDecimal("547100"),
                "제품 감정 완료"));
        l.add(getTestItem(
                827692,
                "2019.05.03",
                "MaisonKitsune",
                "19SS",
                "여성 슈즈 펌프스 no.2964 상품 by.SP",
                "블랙",
                "US6",
                "1개",
                new BigDecimal("958400"),
                "제품 감정 완료"));*/
        adapter.setItems(l)

        mBinding.swipeRefreshLayout.setOnRefreshListener(this@MyPageDeliveryLayout)
    }

    private fun getTestItem(orderNo: Int,
                            date: String,
                            brand: String,
                            season: String,
                            productName: String,
                            option1: String,
                            option2: String,
                            option3: String,
                            price: BigDecimal,
                            status: String): MyOrderItem {
        val i = MyOrderItem()
        i.orderProdGroupId = orderNo
        i.orderDate = date
        i.brandName = brand
        i.season = season
        i.prodName = productName
        i.optionAttribute1 = option1
        i.optionAttribute2 = option2
        i.optionAttribute3 = option3
        i.discountPrice = price
        i.purchaseStatusText = status
        return i
    }

    private fun setLinkText() {
        // Sales Number
        mBinding.layoutInformation.textInformationSalesNumber.setMovementMethod(LinkMovementMethod.getInstance())
        mBinding.layoutInformation.textInformationSalesNumber.setText(TextUtil.createTextWithLink(context!!,
                R.string.information_company_sales_number, R.string.information_confirm_company,
                R.dimen.text_11, true
        ) {
            if (true) {
                CommonUtil.debug("Company Info!")
            }
        }, TextView.BufferType.SPANNABLE)
        // Description
        mBinding.layoutInformation.textInformationDescription.setMovementMethod(LinkMovementMethod.getInstance())
        mBinding.layoutInformation.textInformationDescription.setText(TextUtil.createTextWithLink(context!!,
                R.string.information_description, R.string.information_confirm_service,
                R.dimen.text_11, true
        ) {
            if (true) {
                CommonUtil.debug("Sevice Confirm!")
            }
        }, TextView.BufferType.SPANNABLE)
    }

    private fun initCalendar() {
        setPeriod(0)
        //
        val today = DateUtil.getTodayToString(Type.DateFormat.TYPE_1)
        mBinding.layoutCalendar.textDateFrom.setText(today)
        mBinding.layoutCalendar.textDateTo.setText(today)
    }

    private fun setPeriod(position: Int) {
        when (position) {
            0 -> {
                mBinding.layoutCalendar.textWeek.setSelected(true) //
                mBinding.layoutCalendar.textMonth.setSelected(false)
                mBinding.layoutCalendar.textMonthThree.setSelected(false)
                mBinding.layoutCalendar.textYear.setSelected(false)
            }

            1 -> {
                mBinding.layoutCalendar.textWeek.setSelected(false)
                mBinding.layoutCalendar.textMonth.setSelected(true) //
                mBinding.layoutCalendar.textMonthThree.setSelected(false)
                mBinding.layoutCalendar.textYear.setSelected(false)
            }

            2 -> {
                mBinding.layoutCalendar.textWeek.setSelected(false)
                mBinding.layoutCalendar.textMonth.setSelected(false)
                mBinding.layoutCalendar.textMonthThree.setSelected(true) //
                mBinding.layoutCalendar.textYear.setSelected(false)
            }

            3 -> {
                mBinding.layoutCalendar.textWeek.setSelected(false)
                mBinding.layoutCalendar.textMonth.setSelected(false)
                mBinding.layoutCalendar.textMonthThree.setSelected(false)
                mBinding.layoutCalendar.textYear.setSelected(true) //
            }
        }
    }

    private fun showRewardDialog(message: String) {
        val md = MessageDialog()
        md.setMessage(message)
        md.show((context as MainActivity).supportFragmentManager, "OrderShipFragment")
    }


    override fun onRefresh() {
        this@MyPageDeliveryLayout.handler.postDelayed({
            mBinding.swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    ////////////////////////////////////////////////


    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }
}