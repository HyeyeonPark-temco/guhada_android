package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.server.BenefitServer
import io.temco.guhada.databinding.ItemCouponBinding
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.holder.base.BaseViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 마이페이지 - 쿠폰 리스트 Adapter
 * @author Hyeyeon Park
 * @since 2019.08.08
 */
class MyPageCouponAdapter : RecyclerView.Adapter<MyPageCouponAdapter.Holder>() {
    var list: MutableList<Coupon> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_coupon, parent, false))
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<Coupon>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun addItems(list: MutableList<Coupon>) {
        val prevSize = this.list.size
        this.list.addAll(list)
        notifyItemRangeInserted(prevSize, list.size - 1)
    }

    inner class Holder(binding: ItemCouponBinding) : BaseViewHolder<ItemCouponBinding>(binding.root) {
        fun bind(item: Coupon) {
            mBinding.coupon = item
            mBinding.imagebuttonCouponDelete.setOnClickListener {
                CustomMessageDialog(mBinding.root.context.resources.getString(R.string.mypagecoupon_message_delete), true) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Preferences.getToken().let {
                            if (it.accessToken != null) {
                                val model = deleteCouponAsync("Bearer ${it.accessToken}", item.couponNumber).await()
                                if (model.resultCode == ResultCode.SUCCESS.flag) {
                                    this@MyPageCouponAdapter.list.removeAt(adapterPosition)
                                    notifyItemRemoved(adapterPosition)
                                }
                            }
                        }
                    }
                }.show(manager = (mBinding.root.context as AppCompatActivity).supportFragmentManager, tag = "MyPageCouponAdapter")
            }
            mBinding.executePendingBindings()
        }

        private suspend fun deleteCouponAsync(accessToken: String, couponNumber: String) = BenefitServer.deleteCouponAsync(accessToken, couponNumber).await()

    }
}