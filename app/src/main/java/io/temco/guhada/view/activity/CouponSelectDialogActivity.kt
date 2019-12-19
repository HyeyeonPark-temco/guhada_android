package io.temco.guhada.view.activity

import android.app.Activity
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.payment.CalculatePaymentInfo
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ActivityCouponselectdialogBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.coupon.CouponSellerAdapter

/**
 * 주문결제-쿠폰 선택 팝업
 * @author Hyeyeon Park
 * @since 2019.09.13
 */
class CouponSelectDialogActivity : BindActivity<ActivityCouponselectdialogBinding>() {
    private lateinit var mViewModel: CouponSelectDialogViewModel

    override fun getBaseTag(): String = CouponSelectDialogActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_couponselectdialog
    override fun getViewType(): Type.View = Type.View.COUPON_SELECT

    override fun init() {
        initViewModel()
        mBinding.imagebuttonCouponselectClose.setOnClickListener { finish() }
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()

    }

    private fun initViewModel() {
        mViewModel = CouponSelectDialogViewModel().apply {
            this.mCalculatePaymentInfo.observe(this@CouponSelectDialogActivity, Observer {
                for (item in it.discountInfoResponseList)
                    if (item.discountType == CalculatePaymentInfo.DiscountInfoResponse.DiscountType.COUPON_DISCOUNT.type) {
                        intent.putExtra("discountPrice", item.discountPrice)
                        intent.putExtra("couponCount", mViewModel.mSelectedCouponMap.keys.size)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                        break
                    }
            })
        }
        intent.getSerializableExtra("couponInfo").let {
            if (it != null) {
                mViewModel.mCouponInfo = it as CouponInfo
                mBinding.recyclerviewCouponselectList.adapter = CouponSellerAdapter().apply {
                    this.mViewModel = this@CouponSelectDialogActivity.mViewModel.apply {
                        this.mTotalDiscountPrice = ObservableInt(it.totalCouponDiscountPrice)
                        this.mTotalProductPrice = ObservableInt(it.totalProductPrice)
                    }
                    this.mCouponBenefitSellerResponseList = it.benefitSellerResponseList
                }
            }
        }
    }
}