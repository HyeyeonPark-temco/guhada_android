package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectCouponBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 쿠폰선택-쿠폰 list adapter
 * @author Hyeyeon Park
 * @since 2019.09.13
 */
class CouponWalletAdapter : RecyclerView.Adapter<CouponWalletAdapter.Holder>() {
    lateinit var mViewModel: CouponSelectDialogViewModel
    var mList = mutableListOf<CouponWallet>()
    var prevPos = -1
    var selectedPos = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_coupon, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemCouponselectCouponBinding) : BaseViewHolder<ItemCouponselectCouponBinding>(binding.root) {
        fun bind(item: CouponWallet) {
            mBinding.imageviewCouponselectCoupon.setOnClickListener {
                prevPos = selectedPos
                selectedPos = adapterPosition
                (it as ImageView).setImageResource(R.drawable.radio_checked)
                if (prevPos > -1) notifyItemChanged(prevPos)
            }

            mBinding.imageviewCouponselectCoupon.setImageResource(R.drawable.radio_select)
            mBinding.couponWallet = item
            mBinding.executePendingBindings()
        }
    }
}