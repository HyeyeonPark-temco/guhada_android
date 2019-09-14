package io.temco.guhada.view.activity

import androidx.lifecycle.Observer
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ActivityCouponselectdialogBinding
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.adapter.coupon.CouponDealAdapter
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
        mViewModel = CouponSelectDialogViewModel()
        mViewModel.mOrder.observe(this@CouponSelectDialogActivity, Observer { order ->
            mViewModel.mCouponWalletList = order.availableCouponWalletResponses
            for (couponWallet in order.availableCouponWalletResponses) {
                for (coupon in couponWallet.couponWalletResponseList) {
                    if (mViewModel.mCouponWalletMap[coupon.sellerName] == null)
                        mViewModel.mCouponWalletMap[coupon.sellerName ?: ""] = mutableListOf()

                    mViewModel.mCouponWalletMap[coupon.sellerName]?.add(couponWallet)
                    break
                }
            }

            mBinding.recyclerviewCouponselectList.adapter = CouponSellerAdapter().apply {
                this.mViewModel = this@CouponSelectDialogActivity.mViewModel
                this.mCouponWalletMap = mViewModel.mCouponWalletMap
            }

            mBinding.totalProductPrice = order.totalProdPrice
        })

        intent.getSerializableExtra("productList").let {
            if (it != null) mViewModel.mProductList = (it as ArrayList<BaseProduct>).toMutableList()
        }

        intent.getIntArrayExtra("cartIdList").let {
            if (it != null) {
                mViewModel.mCartIdList = it
                mViewModel.getOrderForm()
            }
        }
    }
}