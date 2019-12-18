package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.coupon.CouponInfo
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectSellerBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 쿠폰선택-셀러 list adapter
 * @author Hyeyeon Park
 * @since 2019.09.14
 */
class CouponSellerAdapter : RecyclerView.Adapter<CouponSellerAdapter.Holder>() {
    lateinit var mViewModel: CouponSelectDialogViewModel
    var mCouponBenefitSellerResponseList = mutableListOf<CouponInfo.BenefitSellerResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_seller, parent, false))

    override fun getItemCount(): Int = mCouponBenefitSellerResponseList.size// mCouponWalletMap.keys.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mCouponBenefitSellerResponseList[position])
    }

    inner class Holder(binding: ItemCouponselectSellerBinding) : BaseViewHolder<ItemCouponselectSellerBinding>(binding.root) {
        fun bind(benefitSellerResponse: CouponInfo.BenefitSellerResponse) {
            mBinding.sellerName = benefitSellerResponse.sellerName
            mBinding.recyclerviewCouponselectDeal.adapter = CouponDealAdapter().apply {
                this.mViewModel = this@CouponSellerAdapter.mViewModel
                this.mCouponBenefitOrderProductList = benefitSellerResponse.benefitOrderProductResponseList

            }
        }

    }
}