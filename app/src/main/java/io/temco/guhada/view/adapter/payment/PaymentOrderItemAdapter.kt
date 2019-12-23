package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.order.OrderItemResponse
import io.temco.guhada.data.model.product.BaseProduct
import io.temco.guhada.data.viewmodel.payment.PaymentViewModel
import io.temco.guhada.databinding.ItemPaymentOrderitemBinding
import io.temco.guhada.databinding.ItemPaymentProductBinding

class PaymentOrderItemAdapter : RecyclerView.Adapter<PaymentOrderItemAdapter.Holder>() {
    private var originItems = mutableListOf<OrderItemResponse>()
    private var items = mutableListOf<OrderItemResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemPaymentOrderitemBinding>(LayoutInflater.from(parent.context), R.layout.item_payment_orderitem, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(items: MutableList<OrderItemResponse>) {
        this.items = items
        this.originItems = items
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemPaymentOrderitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(orderItem: OrderItemResponse) {
            binding.viewPaymentproductDivider.visibility = if (adapterPosition == itemCount - 1) View.GONE else View.VISIBLE
            (binding.linearlayoutPaymentproductContainer.layoutParams as ViewGroup.MarginLayoutParams).topMargin = if (adapterPosition == 0) 0 else CommonViewUtil.convertDpToPixel(20, binding.root.context)
            binding.orderItem = orderItem
            binding.executePendingBindings()
        }
    }

    companion object {

        /**
         * 주문결제-주문 상품 list collapsed
         * @author Hyeyeon Park
         */
        @JvmStatic
        @BindingAdapter("paymentOrderItemCollapsed")
        fun RecyclerView.bindOrderItemCollapsed(visible: Boolean?) {
            if (visible != null && this.adapter != null) {
                val adapter = (this.adapter as PaymentOrderItemAdapter)
                adapter.items = if (visible) adapter.originItems else mutableListOf(adapter.originItems[0])
                adapter.notifyDataSetChanged()
            }
        }
    }
}