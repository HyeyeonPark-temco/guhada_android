package io.temco.guhada.view.activity

import android.graphics.Typeface
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.viewmodel.ShippingTrackingViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.ShippingTrackingAdapter

/**
 * 배송 조회 Activity
 * @author Hyeyeon Park
 * @since 2019.09.27
 */
class ShippingTrackingActivity : BindActivity<io.temco.guhada.databinding.ActivityShippingtrackingBinding>() {

    enum class ShippingStatus(val label: String, val level: Int) {
        READY("배송준비중", 1),  // UI 표시 X
        COMPLETE_COLLECTION("집하완료", 2),
        DELIVERING("배송중", 3),
        ARRIVAL_POINT("지점도착", 4),
        START_DELIVERY("배송출발", 5),
        COMPLETE_DELIVERY("배송완료", 6)
    }

    private lateinit var mViewModel: ShippingTrackingViewModel

    override fun getBaseTag(): String = ShippingTrackingActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_shippingtracking
    override fun getViewType(): Type.View = Type.View.SHIPPING_TRACKING
    override fun init() {
        initViewModel()
        initHeader()
    }

    private fun initViewModel() {
        mViewModel = ShippingTrackingViewModel()
        mViewModel.mShippingTrackingInfo.observe(this, Observer {
            if (it.status.isNotEmpty()) {
                mBinding.recyclerviewShippingtracking.adapter = ShippingTrackingAdapter().apply { this.mList = it.trackingDetails }
                mBinding.item = it
                initStatus()
                mBinding.executePendingBindings()
            } else {
                ToastUtil.showMessage(BaseApplication.getInstance().getString(R.string.shippingtracking_message_invalid))
                finish()
            }
        })

        intent.getStringExtra("invoiceNo").let {
            if (it != null) mViewModel.mInvoiceNo = it
        }
        intent.getStringExtra("companyNo").let { if (it != null) mViewModel.mCompanyNo = it }
        mViewModel.getShippingTracking()
    }

    private fun initHeader() {
        mBinding.includeShippingtrackingHeader.title = getString(R.string.shippingtracking_title)
        mBinding.includeShippingtrackingHeader.setOnClickCloseButton { finish() }
    }

    private fun initStatus() {
        when (mViewModel.mShippingTrackingInfo.value?.level) {
            ShippingStatus.READY.level -> {
            }
            ShippingStatus.COMPLETE_COLLECTION.level -> setCompleteCollectionActive()
            ShippingStatus.DELIVERING.level -> setDeliveringActive()
            ShippingStatus.ARRIVAL_POINT.level -> setArrivalPointActive()
            ShippingStatus.START_DELIVERY.level -> setStartDeliveringActive()
            ShippingStatus.COMPLETE_DELIVERY.level -> setCompleteDeliveringActive()
        }
    }

    private fun setCompleteCollectionActive() {
        mBinding.imageviewDeliverytrackingStatus1.setImageResource(R.drawable.delivery_icon_01_active)
        mBinding.imageviewDeliverytrackingStatusdot1.setImageResource(R.drawable.oval_all_purple)
        mBinding.textviewDeliverytrackingStatus1.setTextColor(resources.getColor(R.color.common_blue_purple))
        mBinding.textviewDeliverytrackingStatus1.setTypeface(mBinding.textviewDeliverytrackingStatus1.typeface, Typeface.BOLD)
    }

    private fun setDeliveringActive() {
        mBinding.imageviewDeliverytrackingStatus1.setImageResource(R.drawable.delivery_icon_01_active)
        mBinding.imageviewDeliverytrackingStatusdot1.setImageResource(R.drawable.circleline_all_purple)
        mBinding.textviewDeliverytrackingStatus1.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus2.setImageResource(R.drawable.delivery_icon_02_active)
        mBinding.imageviewDeliverytrackingStatusdot2.setImageResource(R.drawable.oval_all_purple)
        mBinding.viewDeliverytrackingStatusline1.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline2.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus2.setTextColor(resources.getColor(R.color.common_blue_purple))
        mBinding.textviewDeliverytrackingStatus2.setTypeface(mBinding.textviewDeliverytrackingStatus1.typeface, Typeface.BOLD)
    }

    private fun setArrivalPointActive() {
        mBinding.imageviewDeliverytrackingStatus1.setImageResource(R.drawable.delivery_icon_01_active)
        mBinding.imageviewDeliverytrackingStatusdot1.setImageResource(R.drawable.circleline_all_purple)
        mBinding.textviewDeliverytrackingStatus1.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus2.setImageResource(R.drawable.delivery_icon_02_active)
        mBinding.imageviewDeliverytrackingStatusdot2.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline1.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline2.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus2.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus3.setImageResource(R.drawable.delivery_icon_03_active)
        mBinding.imageviewDeliverytrackingStatusdot3.setImageResource(R.drawable.oval_all_purple)
        mBinding.viewDeliverytrackingStatusline3.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline4.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus3.setTextColor(resources.getColor(R.color.common_blue_purple))
        mBinding.textviewDeliverytrackingStatus3.setTypeface(mBinding.textviewDeliverytrackingStatus1.typeface, Typeface.BOLD)
    }

    private fun setStartDeliveringActive() {
        mBinding.imageviewDeliverytrackingStatus1.setImageResource(R.drawable.delivery_icon_01_active)
        mBinding.imageviewDeliverytrackingStatusdot1.setImageResource(R.drawable.circleline_all_purple)
        mBinding.textviewDeliverytrackingStatus1.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus2.setImageResource(R.drawable.delivery_icon_02_active)
        mBinding.imageviewDeliverytrackingStatusdot2.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline1.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline2.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus2.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus3.setImageResource(R.drawable.delivery_icon_03_active)
        mBinding.imageviewDeliverytrackingStatusdot3.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline3.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline4.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus3.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus4.setImageResource(R.drawable.delivery_icon_04_active)
        mBinding.imageviewDeliverytrackingStatusdot4.setImageResource(R.drawable.oval_all_purple)
        mBinding.viewDeliverytrackingStatusline4.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline5.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus4.setTextColor(resources.getColor(R.color.common_blue_purple))
        mBinding.textviewDeliverytrackingStatus4.setTypeface(mBinding.textviewDeliverytrackingStatus1.typeface, Typeface.BOLD)
    }

    private fun setCompleteDeliveringActive() {
        mBinding.imageviewDeliverytrackingStatus1.setImageResource(R.drawable.delivery_icon_01_active)
        mBinding.imageviewDeliverytrackingStatusdot1.setImageResource(R.drawable.circleline_all_purple)
        mBinding.textviewDeliverytrackingStatus1.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus2.setImageResource(R.drawable.delivery_icon_02_active)
        mBinding.imageviewDeliverytrackingStatusdot2.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline1.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline2.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus2.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus3.setImageResource(R.drawable.delivery_icon_03_active)
        mBinding.imageviewDeliverytrackingStatusdot3.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline3.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline4.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus3.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus4.setImageResource(R.drawable.delivery_icon_04_active)
        mBinding.imageviewDeliverytrackingStatusdot4.setImageResource(R.drawable.circleline_all_purple)
        mBinding.viewDeliverytrackingStatusline4.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline5.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus4.setTextColor(resources.getColor(R.color.common_blue_purple))

        mBinding.imageviewDeliverytrackingStatus5.setImageResource(R.drawable.delivery_icon_05_active)
        mBinding.imageviewDeliverytrackingStatusdot5.setImageResource(R.drawable.oval_all_purple)
        mBinding.viewDeliverytrackingStatusline6.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline7.setBackgroundResource(R.color.common_blue_purple)
        mBinding.viewDeliverytrackingStatusline8.setBackgroundResource(R.color.common_blue_purple)
        mBinding.textviewDeliverytrackingStatus5.setTextColor(resources.getColor(R.color.common_blue_purple))
        mBinding.textviewDeliverytrackingStatus5.setTypeface(mBinding.textviewDeliverytrackingStatus1.typeface, Typeface.BOLD)
    }

}