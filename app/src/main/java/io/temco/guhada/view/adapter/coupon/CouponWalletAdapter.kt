package io.temco.guhada.view.adapter.coupon

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.BR
import io.temco.guhada.R
import io.temco.guhada.common.BaseApplication
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.model.coupon.CouponInfo
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
    var mList = mutableListOf<CouponInfo.BenefitOrderProductCouponResponse>()
    var mCartId = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_coupon, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    /*
    *  - 쿠폰 선택: mSelectedCouponInfo[couponNumber, cartId]
    *  - 적용 안함 선택: mSelectedCouponInfo[cartId, "NOT_SELECT"]
    */
    inner class Holder(binding: ItemCouponselectCouponBinding) : BaseViewHolder<ItemCouponselectCouponBinding>(binding.root) {
        fun bind(item: CouponInfo.BenefitOrderProductCouponResponse) {
            if (item.selected) {
                mViewModel.mSelectedCouponMap[mCartId] = item
                mViewModel.mSelectedCouponInfo.get()?.set(item.couponNumber, mCartId)
                mViewModel.notifyPropertyChanged(BR.mSelectedCouponInfo)
            }

            mBinding.imageviewCouponselectCoupon.setOnClickListener {
                for (couponNumber in mViewModel.mSelectedCouponInfo.get()?.keys ?: mutableSetOf()) {
                    if (mViewModel.mSelectedCouponInfo.get()?.get(couponNumber) == mCartId) {

                        if (mViewModel.mSelectedCouponMap[mCartId] != null) {
                            mViewModel.mTotalDiscountPrice = ObservableInt(mViewModel.mTotalDiscountPrice.get() - mViewModel.mSelectedCouponMap[mCartId]!!.couponDiscountPrice)
                            mViewModel.mSelectedCouponMap.remove(mCartId)
                        }
                        mViewModel.mSelectedCouponInfo.get()?.remove(mCartId.toString())
                        mViewModel.mSelectedCouponInfo.get()?.remove(couponNumber ?: "")
                        break
                    }
                }

                mViewModel.mSelectedCartId = mCartId
                mViewModel.mSelectedCoupon = ObservableField(item)

                if (item.couponNumber == CouponSelectDialogViewModel.CouponFlag().NOT_SELECT_COUPON_NUMBER) {
                    mViewModel.mSelectedCouponInfo.get()?.set(mCartId.toString(), CouponSelectDialogViewModel.CouponFlag().NOT_SELECT_COUPON_NUMBER)
                } else {
                    mViewModel.mSelectedCouponInfo.get()?.set(item.couponNumber, mCartId)
                    mViewModel.mSelectedCouponInfo.get()?.remove(mCartId.toString())
                    mViewModel.mSelectedCouponMap[mCartId] = item
                    mViewModel.mTotalDiscountPrice = ObservableInt(mViewModel.mTotalDiscountPrice.get() + mViewModel.mSelectedCouponMap[mCartId]?.couponDiscountPrice!!)
                }
                mViewModel.notifyPropertyChanged(BR.mTotalDiscountPrice)
                mViewModel.notifyPropertyChanged(BR.mSelectedCouponInfo)

                mBinding.viewModel = mViewModel
                mBinding.executePendingBindings()

//                Log.e("선택 쿠폰", mViewModel.mSelectedCouponMap.toString())
            }
            mBinding.textviewCouponselectCouponname.text =
                    if (item.maximumDiscountPrice == null || item.maximumDiscountPrice == 0) {
                        // maximumDiscountPrice 없는 경우, 정률 쿠폰 할인 금액 미표시
                        when {
                            item.discountType == Coupon.DiscountType.RATE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titlerate_format_withoutprice), "${(item.discountRate).toInt()}%", item.couponTitle)
                            item.discountType == Coupon.DiscountType.PRICE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titleprice_format), item.couponDiscountPrice, item.couponTitle)
                            else -> item.couponTitle ?: ""
                        }
                    } else {
                        when {
                            item.discountType == Coupon.DiscountType.RATE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titlerate_format), "${(item.discountRate).toInt()}%", item.couponTitle, item.couponDiscountPrice)
                            item.discountType == Coupon.DiscountType.PRICE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titleprice_format), item.couponDiscountPrice, item.couponTitle)
                            else -> item.couponTitle ?: ""
                        }
                    }

            mBinding.cartId = mCartId
            mBinding.couponWallet = item

            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["currentCartId", "currentCouponNumber", "selectedCouponInfo"])
        fun ImageView.bindSelected(currentCartId: Long, currentCouponNumber: String?, selectedCouponInfo: MutableMap<String, Any?>?) {

            fun setButtonInactive() {
                this.isClickable = false
                this.setImageResource(R.drawable.radio_inactive)
            }

            fun setButtonActive() {
                this.isClickable = true
                this.setImageResource(R.drawable.radio_select)
            }

            fun setButtonChecked() {
                this.isClickable = true
                this.setImageResource(R.drawable.radio_checked)
            }

            if (currentCouponNumber != null && selectedCouponInfo != null)
                when {
                    currentCouponNumber != "NOT_SELECT" -> when {
                        selectedCouponInfo[currentCouponNumber] == null -> setButtonActive()
                        selectedCouponInfo[currentCouponNumber] == currentCartId -> setButtonChecked()
                        selectedCouponInfo[currentCouponNumber] != currentCartId -> setButtonInactive()
                    }
                    selectedCouponInfo[currentCartId.toString()] == CouponSelectDialogViewModel.CouponFlag().NOT_SELECT_COUPON_NUMBER -> setButtonChecked()
                    else -> setButtonActive()
                }

//            Log.e("쿠폰", "currentCartId: $currentCartId      currentCouponNumber: $currentCouponNumber   selectedCouponMap:${selectedCouponInfo.toString()}")
        }

        @JvmStatic
        @BindingAdapter(value = ["currentCartId", "currentCouponNumber", "selectedCouponInfo"])
        fun TextView.bindInactivated(currentCartId: Long, currentCouponNumber: String?, selectedCouponInfo: MutableMap<String, Any?>?) {

            val inactiveTextColor = BaseApplication.getInstance().resources.getColor(R.color.pinkish_grey)
            val activeTextColor = BaseApplication.getInstance().resources.getColor(R.color.greyish_brown_two)
            fun setTextInactive() = this.setTextColor(inactiveTextColor)
            fun setTextActive() = this.setTextColor(activeTextColor)

            if (currentCouponNumber != null && selectedCouponInfo != null && currentCouponNumber != CouponSelectDialogViewModel.CouponFlag().NOT_SELECT_COUPON_NUMBER)
                if (selectedCouponInfo[currentCouponNumber] != null && selectedCouponInfo[currentCouponNumber] != currentCartId)
                    setTextInactive()
                else setTextActive()
            else
                setTextActive()

        }
    }
}
