package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectCouponBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class CouponWalletAdapter : RecyclerView.Adapter<CouponWalletAdapter.Holder>() {
    lateinit var mViewModel : CouponSelectDialogViewModel
    var mList = mutableListOf<CouponWallet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_coupon, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemCouponselectCouponBinding) : BaseViewHolder<ItemCouponselectCouponBinding>(binding.root) {
        fun bind(item: CouponWallet) {
            mBinding.couponWallet = item
        }
    }
}