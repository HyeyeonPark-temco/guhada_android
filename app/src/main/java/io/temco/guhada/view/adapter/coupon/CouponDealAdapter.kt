package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectDealBinding
import io.temco.guhada.view.activity.CouponSelectDialogActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 쿠폰선택-상품 list adapter
 * @author Hyeyeon Park
 * @since 2019.09.13
 */
class CouponDealAdapter : RecyclerView.Adapter<CouponDealAdapter.Holder>() {
    lateinit var mViewModel: CouponSelectDialogViewModel
    var mOrderItemList = mutableListOf<BaseProduct>()
    var mCouponWalletList = mutableListOf<AvailableCouponWallet>()

    //
    var mCouponBenefitOrderProductList = mutableListOf<CouponInfo.BenefitOrderProductResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_deal, parent, false))

    override fun getItemCount(): Int = mCouponBenefitOrderProductList.size// mCouponWalletList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
//        val couponWallet = mCouponWalletList[position]
//        for (product in mOrderItemList) {
//            if (product.dealId == couponWallet.dealId) {
//                holder.bind1(product = product, couponWallet = couponWallet)
//                break
//            }
//        }

        holder.bind(mCouponBenefitOrderProductList[position])
    }

    inner class Holder(binding: ItemCouponselectDealBinding) : BaseViewHolder<ItemCouponselectDealBinding>(binding.root) {
        fun bind(benefitOrderProductResponse: CouponInfo.BenefitOrderProductResponse) {
            mBinding.recyclerviewCouponselectCoupon.adapter = CouponWalletAdapter().apply {
                this.mViewModel = this@CouponDealAdapter.mViewModel

                CouponInfo.BenefitOrderProductCouponResponse().apply {
                    this.couponId = CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_ID
                    this.couponNumber = CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_NUMBER
                    this.couponTitle = "적용 안함"
                    this.discountType = Coupon.DiscountType.NONE.type
                }.let { benefitOrderProductResponse.benefitProductCouponResponseList.add(it) }

                this.mList = benefitOrderProductResponse.benefitProductCouponResponseList
                this.mDealId = benefitOrderProductResponse.dealId

            }

            benefitOrderProductResponse.optionStr =
                    if (benefitOrderProductResponse.option.isNullOrEmpty()) "${benefitOrderProductResponse.currentQuantity}개"
                    else "${benefitOrderProductResponse.option}, ${benefitOrderProductResponse.currentQuantity}개"

            mBinding.product = benefitOrderProductResponse
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }

        fun bind1(product: BaseProduct, couponWallet: AvailableCouponWallet) {
            mBinding.recyclerviewCouponselectCoupon.adapter = CouponWalletAdapter().apply {
                this.mViewModel = this@CouponDealAdapter.mViewModel
                CouponWallet().apply {
                    this.couponId = CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_ID
                    this.couponNumber = CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_NUMBER
                    this.couponTitle = "적용 안함"
                    this.discountType = Coupon.DiscountType.NONE.type
                }.let { couponWallet.couponWalletResponseList.add(it) }
//                this.mList = couponWallet.couponWalletResponseList
//                this.mProduct = product
            }

//            mBinding.product = product
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

}