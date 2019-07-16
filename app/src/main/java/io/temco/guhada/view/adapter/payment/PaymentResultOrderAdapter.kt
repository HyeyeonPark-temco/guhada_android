package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.View
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

            var attributeText = ""
            if (!item.optionAttribute1.isNullOrEmpty()) {
                attributeText = "$attributeText${item.optionAttribute1}, "
            }

            if (!item.optionAttribute2.isNullOrEmpty()) {
                attributeText = "$attributeText${item.optionAttribute2}, "
            }

            if (!item.optionAttribute3.isNullOrEmpty()) {
                attributeText = "$attributeText${item.optionAttribute3}, "
            }

            mBinding.viewPaymentresultLine.visibility = if (adapterPosition == this@PaymentResultOrderAdapter.list.size - 1) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            binding.attributeText = "$attributeText${item.quantity}개"
            binding.purchaseOrder = item
            binding.executePendingBindings()
        }
    }
}