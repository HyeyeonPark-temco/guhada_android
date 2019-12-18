package io.temco.guhada.view.adapter.coupon

import android.util.Log
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
import io.temco.guhada.data.model.coupon.CouponWallet
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.viewmodel.CouponSelectDialogViewModel
import io.temco.guhada.databinding.ItemCouponselectCouponBinding
import io.temco.guhada.view.activity.CouponSelectDialogActivity
import io.temco.guhada.view.adapter.coupon.CouponWalletAdapter.Companion.bindSelected
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 쿠폰선택-쿠폰 list adapter
 * @author Hyeyeon Park
 * @since 2019.09.13
 */
class CouponWalletAdapter : RecyclerView.Adapter<CouponWalletAdapter.Holder>() {
    lateinit var mViewModel: CouponSelectDialogViewModel
    var mList = mutableListOf<CouponInfo.BenefitOrderProductCouponResponse>()// mutableListOf<CouponWallet>()
    //    var mProduct = BaseProduct()
    var mDealId = 0L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_couponselect_coupon, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemCouponselectCouponBinding) : BaseViewHolder<ItemCouponselectCouponBinding>(binding.root) {
        fun bind(item: CouponInfo.BenefitOrderProductCouponResponse) {
            if (item.selected)
                mViewModel.mSelectedCouponInfo[item.couponNumber] = mDealId

            mBinding.imageviewCouponselectCoupon.setOnClickListener {
                mViewModel.mSelectedDealId = mDealId
                mViewModel.mSelectedCoupon = ObservableField(item)
                mViewModel.mSelectedCouponMap[mDealId] = item

                mViewModel.mSelectedCouponInfo[item.couponNumber] = mDealId
                mViewModel.notifyPropertyChanged(BR.mSelectedCoupon)

                mViewModel.mTotalDiscountPrice = ObservableInt(getTotalDiscountPrice())
                mViewModel.notifyPropertyChanged(BR.mTotalDiscountPrice)

                mBinding.viewModel = mViewModel
                mBinding.executePendingBindings()
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

            mBinding.dealId = mDealId
            mBinding.couponWallet = item
            mBinding.viewModel = mViewModel

            mBinding.executePendingBindings()
        }

        fun bind1(item: CouponWallet) {
            mBinding.imageviewCouponselectCoupon.setOnClickListener {
                //                mViewModel.mSelectedProduct = mProduct
                mViewModel.mSelectedCoupon = ObservableField(item)
//                mViewModel.mSelectedCouponMap[mProduct.dealId] = item
                mViewModel.notifyPropertyChanged(BR.mSelectedCoupon)

                mViewModel.mTotalDiscountPrice = ObservableInt(getTotalDiscountPrice())
                mViewModel.notifyPropertyChanged(BR.mTotalDiscountPrice)

                mBinding.viewModel = mViewModel
                mBinding.executePendingBindings()
            }

            mBinding.textviewCouponselectCouponname.text =
                    if (item.maximumDiscountPrice == null || item.maximumDiscountPrice == 0) {
                        // maximumDiscountPrice 없는 경우, 정률 쿠폰 할인 금액 미표시
                        when {
                            item.discountType == Coupon.DiscountType.RATE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titlerate_format_withoutprice), "${(item.discountRate * 100).toInt()}%", item.couponTitle)
                            item.discountType == Coupon.DiscountType.PRICE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titleprice_format), item.discountPrice, item.couponTitle)
                            else -> item.couponTitle ?: ""
                        }
                    } else {
                        val discountPrice = 0
//                        = if (item.discountType == Coupon.DiscountType.RATE.type) {
//                            val price = (Math.round(mProduct.sellPrice * item.discountRate)).toInt()
//                            if (price > item.maximumDiscountPrice ?: 0) item.maximumDiscountPrice
//                            else price
//                        } else item.discountPrice

                        when {
                            item.discountType == Coupon.DiscountType.RATE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titlerate_format), "${(item.discountRate * 100).toInt()}%", item.couponTitle, discountPrice
                                    ?: 0)
                            item.discountType == Coupon.DiscountType.PRICE.type -> String.format(BaseApplication.getInstance().getString(R.string.couponselect_titleprice_format), item.discountPrice, item.couponTitle)
                            else -> item.couponTitle ?: ""
                        }
                    }

//            mBinding.dealId = mProduct.dealId
//            mBinding.couponWallet = item
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    private fun getTotalDiscountPrice(): Int {
        var discountPrice = 0
        for (dealId in mViewModel.mSelectedCouponMap.keys) {
            for (product in mViewModel.mProductList) {
                if (product.dealId == dealId) {
                    val couponWallet = mViewModel.mSelectedCouponMap[dealId]
                            ?: CouponWallet()
                    val productPrice = product.totalPrice

                    discountPrice += when {
                        couponWallet.discountType == Coupon.DiscountType.PRICE.type -> couponWallet.discountPrice
                        couponWallet.discountType == Coupon.DiscountType.RATE.type -> {
                            val price = Math.round(productPrice * couponWallet.discountRate).toInt()
                            if (price > couponWallet.maximumDiscountPrice ?: 0) {
                                //  ToastUtil.showMessage(String.format(BaseApplication.getInstance().getString(R.string.couponselect_overmaxdiscountprice), couponWallet.maximumDiscountPrice))
                                couponWallet.maximumDiscountPrice ?: 0
                            } else {
                                price
                            }
                        }
                        else -> 0
                    }
                }
            }
        }
        return discountPrice
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["selectedDealId", "selectedCouponNumber", "vmDealId", "vmCouponNumber", "selectedCouponMap"])
        fun ImageView.bindSelected(selectedDealId: Long, selectedCouponNumber: String?, vmDealId: Long, vmCouponNumber: String?, selectedCouponMap: MutableMap<String, Long>?) { // MutableMap<Long, CouponWallet>?

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


            /*
                - vmCouponNumber: selected coupon number
                - selectedCouponNumber: current coupon number
             */

            val currentDealId = selectedDealId
            val currentCouponNumber = selectedCouponNumber

            if (selectedCouponNumber != null && selectedCouponMap != null)
                if(selectedCouponMap[currentCouponNumber] == null) setButtonActive()
                else if(selectedCouponMap[currentCouponNumber] == currentDealId)  setButtonChecked()
                else  setButtonInactive()

            Log.e("쿠폰","currentDealId: $currentDealId   currentCouponNumber: $currentCouponNumber   selectedCouponMap: ${selectedCouponMap.toString()}")
        }

        @JvmStatic
        @BindingAdapter(value = ["selectedDealId", "selectedCouponNumber", "vmDealId", "vmCouponNumber", "selectedCouponMap"])
        fun TextView.bindInactivated(selectedDealId: Long, selectedCouponNumber: String?, vmDealId: Long, vmCouponNumber: String?, selectedCouponMap: MutableMap<String, Long>?) {

            val inactiveTextColor = BaseApplication.getInstance().resources.getColor(R.color.pinkish_grey)
            val activeTextColor = BaseApplication.getInstance().resources.getColor(R.color.greyish_brown_two)
            fun setTextInactive() = this.setTextColor(inactiveTextColor)
            fun setTextActive() = this.setTextColor(activeTextColor)

            if (selectedCouponNumber != null && selectedCouponMap != null && selectedCouponNumber != CouponSelectDialogActivity.CouponFlag().NOT_SELECT_COUPON_NUMBER)
                if (selectedCouponMap[selectedCouponNumber] != selectedDealId)
                    setTextInactive()
                else setTextActive()
            else
                setTextActive()

        }
    }
}
