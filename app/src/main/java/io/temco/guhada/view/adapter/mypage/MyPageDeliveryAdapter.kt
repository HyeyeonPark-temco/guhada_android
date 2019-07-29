package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.databinding.ItemDeliveryBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class MyPageDeliveryAdapter : RecyclerView.Adapter<MyPageDeliveryAdapter.Holder>() {
    var list: MutableList<PurchaseOrder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_delivery, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(binding: ItemDeliveryBinding) : BaseViewHolder<ItemDeliveryBinding>(binding.root) {
        fun bind(item: PurchaseOrder) {
            mBinding.item = item

            val temp = mutableListOf<String>()
            for (i in 0 until adapterPosition)
                temp.add("TEXT$adapterPosition")

            mBinding.recyclerviewDeliveryButton.adapter = MyPageDeliveryButtonAdapter().apply { this.list = temp }
            (mBinding.recyclerviewDeliveryButton.layoutManager as GridLayoutManager).spanCount = if (temp.size > 1) 2 else 1
            mBinding.executePendingBindings()
        }
    }
}