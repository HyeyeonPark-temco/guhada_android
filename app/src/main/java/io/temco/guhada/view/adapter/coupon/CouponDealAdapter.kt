package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.AvailableCouponWallet
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.order.OrderItemResponse
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectCouponBinding
import io.temco.guhada.databinding.ItemCouponselectDealBinding
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_deal, parent, false))

    override fun getItemCount(): Int = mCouponWalletList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val couponWallet = mCouponWalletList[position]
        for (product in mOrderItemList) {
            if (product.dealId == couponWallet.dealId) {
                holder.bind(product = product, couponWallet = couponWallet)
                break
            }
        }
    }

    inner class Holder(binding: ItemCouponselectDealBinding) : BaseViewHolder<ItemCouponselectDealBinding>(binding.root) {
        fun bind(product: BaseProduct, couponWallet: AvailableCouponWallet) {
            mBinding.recyclerviewCouponselectCoupon.adapter = CouponWalletAdapter().apply {
                this.mViewModel = this@CouponDealAdapter.mViewModel
                this.mList = couponWallet.couponWalletResponseList
            }

            mBinding.product = product
            mBinding.executePendingBindings()
        }
    }
}