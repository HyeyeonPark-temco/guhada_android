package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.databinding.ItemCoupondownloadBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class CouponDownloadListAdapter : RecyclerView.Adapter<CouponDownloadListAdapter.Holder>() {
    var mList = mutableListOf<Coupon>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_coupondownload, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemCoupondownloadBinding) : BaseViewHolder<ItemCoupondownloadBinding>(binding.root) {
        fun bind(item: Coupon) {
            mBinding.coupon = item
            mBinding.executePendingBindings()
        }
    }
}