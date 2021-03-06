package io.temco.guhada.view.activity

import android.app.Activity
import androidx.databinding.ObservableInt
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.order.RequestOrder
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
                val jsonArray = JsonArray()
                for (cartId in mViewModel.mCartIdList) {
                    val couponNumber = mViewModel.mSelectedCouponMap[cartId]?.couponNumber ?: ""

                    RequestOrder.CartItemPayment().apply {
                        this.cartItemId = cartId
                        this.couponNumber = couponNumber
                    }.let {
                        val element = JsonParser().parse(Gson().toJson(it))
                        jsonArray.add(element)
                    }
                }

                var discountPrice = 0
                for (item in it.discountInfoResponseList) {
                    if (item.discountType == CalculatePaymentInfo.DiscountInfoResponse.DiscountType.COUPON_DISCOUNT.type) {
                        discountPrice = item.discountPrice
                        break
                    }
                }

                intent.putExtra("discountPrice", discountPrice)
                intent.putExtra("couponCount", mViewModel.mSelectedCouponMap.keys.size)
                intent.putExtra("selectedCouponArray", jsonArray.toString())
                intent.putExtra("calculatePaymentInfo", it)
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }

        mViewModel.mPointConsumption = intent.getIntExtra("consumptionPoint", 0)
        mViewModel.mCouponInfo.observe(this@CouponSelectDialogActivity, Observer {
            mViewModel.mTotalDiscountPrice.set(it.totalCouponDiscountPrice)
            mViewModel.mTotalProductPrice.set(it.totalProductPrice)

            mBinding.recyclerviewCouponselectList.adapter = CouponSellerAdapter().apply {
                this.mViewModel = this@CouponSelectDialogActivity.mViewModel
                this.mCouponBenefitSellerResponseList = it.benefitSellerResponseList
            }
        })

        intent.getIntArrayExtra("cartIdList").let {
            if (it != null)
                mViewModel.getCouponInfo(it)
        }
    }
}