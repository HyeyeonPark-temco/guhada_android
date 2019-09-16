package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
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
    var mDealId = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_coupon, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemCouponselectCouponBinding) : BaseViewHolder<ItemCouponselectCouponBinding>(binding.root) {
        fun bind(item: CouponWallet) {
            mBinding.imageviewCouponselectCoupon.setOnClickListener {
                mViewModel.mDealId = mDealId
                mViewModel.mSelectedCoupon = ObservableField(item)
                mViewModel.notifyPropertyChanged(BR.mSelectedCoupon)
                mViewModel.mSelectedCouponMap[mDealId] = item
            }

            mBinding.dealId = mDealId
            mBinding.couponWallet = item
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["selectedDealId", "selectedCouponId", "vmDealId", "vmCouponId", "selectedCouponMap"])
        fun ImageView.bindSelected(selectedDealId: Long, selectedCouponId: Long, vmDealId: Long, vmCouponId: Long, selectedCouponMap: MutableMap<Long, CouponWallet>) {
            val NOT_SELECT_COUPON_ID: Long = -1
            if (selectedDealId != vmDealId) {
                if (selectedCouponId == vmCouponId && selectedCouponId > NOT_SELECT_COUPON_ID) {
                    this.setImageResource(R.drawable.radio_inactive)
                } else {
                    // 다른 쿠폰
                    val prevSelectedCoupon = selectedCouponMap[selectedDealId]
                    if (prevSelectedCoupon == null) {
                        this.setImageResource(R.drawable.radio_select)
                    } else {
                        if (selectedCouponId == prevSelectedCoupon.couponId) {
                            this.setImageResource(R.drawable.radio_checked)
                        }
                    }
                }
            } else {
                if (selectedCouponId == vmCouponId) {
                    this.setImageResource(R.drawable.radio_checked)
                } else {
                    this.setImageResource(R.drawable.radio_select)
                }
            }

        }
    }
}