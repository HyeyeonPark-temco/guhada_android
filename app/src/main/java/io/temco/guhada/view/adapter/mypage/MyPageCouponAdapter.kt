package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.databinding.ItemCouponBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

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

    inner class Holder(binding: ItemCouponBinding) : BaseViewHolder<ItemCouponBinding>(binding.root) {
        fun bind(item: Coupon) {
            mBinding.coupon = item
            mBinding.executePendingBindings()
        }
    }
}