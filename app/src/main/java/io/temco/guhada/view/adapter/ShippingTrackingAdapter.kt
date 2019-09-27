package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.shipping.ShippingTrackingDetail
import io.temco.guhada.databinding.ItemShippingtrackingBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 배송 조회 상태 list adapter
 * @author Hyeyeon Park
 * @since 2019.09.27
 */
class ShippingTrackingAdapter : RecyclerView.Adapter<ShippingTrackingAdapter.Holder>() {
    var mList = mutableListOf<ShippingTrackingDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingTrackingAdapter.Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_shippingtracking, parent, false))

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ShippingTrackingAdapter.Holder, position: Int) {
        holder.bind(mList[position])
    }

    inner class Holder(binding: ItemShippingtrackingBinding) : BaseViewHolder<ItemShippingtrackingBinding>(binding.root) {
        fun bind(item: ShippingTrackingDetail) {
            mBinding.item = item
            mBinding.executePendingBindings()
        }
    }
}