package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.databinding.ItemDeliveryOrderBinding
import io.temco.guhada.databinding.ItemPaymentresultOrderBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class PaymentResultOrderAdapter : RecyclerView.Adapter<PaymentResultOrderAdapter.Holder>() {
    private var list: MutableList<PurchaseOrder> = ArrayList()
    var mIsDetail = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layout = if (mIsDetail) R.layout.item_delivery_order else R.layout.item_paymentresult_order
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layout, parent, false)
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

    inner class Holder(var mBinding: ViewDataBinding) : RecyclerView.ViewHolder(mBinding.root) {
        fun bind(item: PurchaseOrder) {
            if (mIsDetail) setBinding(mBinding as ItemDeliveryOrderBinding, item)
            else setBinding(mBinding as ItemPaymentresultOrderBinding, item)
        }

        private fun getAttribute(item: PurchaseOrder): String {
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
            return attributeText
        }

        private fun setBinding(binding: ViewDataBinding, item: PurchaseOrder) {
            if (binding is ItemDeliveryOrderBinding) {
                binding.viewPaymentresultLine.visibility =
                        if (adapterPosition == this@PaymentResultOrderAdapter.list.size - 1) View.INVISIBLE
                        else View.VISIBLE

                binding.attributeText = "${getAttribute(item)}${item.quantity}개"
                binding.purchaseOrder = item
            } else if (binding is ItemPaymentresultOrderBinding) {
                binding.viewPaymentresultLine.visibility =
                        if (adapterPosition == this@PaymentResultOrderAdapter.list.size - 1) View.INVISIBLE
                        else View.VISIBLE
                binding.attributeText = "${getAttribute(item)}${item.quantity}개"
                binding.purchaseOrder = item
            }

            mBinding.executePendingBindings()
        }
    }

}