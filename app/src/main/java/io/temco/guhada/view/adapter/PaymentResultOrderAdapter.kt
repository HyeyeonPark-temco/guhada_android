package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.PurchaseOrder
import io.temco.guhada.databinding.ItemPaymentresultOrderBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class PaymentResultOrderAdapter : RecyclerView.Adapter<PaymentResultOrderAdapter.Holder>() {
    private var list: MutableList<PurchaseOrder> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemPaymentresultOrderBinding>(LayoutInflater.from(parent.context), R.layout.item_paymentresult_order, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<PurchaseOrder>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemPaymentresultOrderBinding) : BaseViewHolder<ItemPaymentresultOrderBinding>(binding.root) {
        fun bind(item: PurchaseOrder) {
            binding.purchaseOrder = item
            binding.executePendingBindings()
        }
    }
}