package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.order.Order
import io.temco.guhada.data.model.order.PaymentMethod
import io.temco.guhada.data.viewmodel.PaymentViewModel
import io.temco.guhada.databinding.ItemPaymentPaymentwayBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class PaymentWayAdapter : RecyclerView.Adapter<PaymentWayAdapter.Holder>() {
    lateinit var mViewModel: PaymentViewModel
    private var list: MutableList<PaymentMethod> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ItemPaymentPaymentwayBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_payment_paymentway, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<PaymentMethod>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemPaymentPaymentwayBinding) : BaseViewHolder<ItemPaymentPaymentwayBinding>(binding.root) {
        fun bind(paymentMethod: PaymentMethod) {
            if (::mViewModel.isInitialized) {
                binding.paymentMethod = paymentMethod
                binding.viewModel = mViewModel
                binding.radiobuttonPaymentway.tag = adapterPosition
                binding.radiobuttonPaymentway.isChecked = mViewModel.paymentWays[adapterPosition]
                binding.executePendingBindings()
            }
        }
    }
}