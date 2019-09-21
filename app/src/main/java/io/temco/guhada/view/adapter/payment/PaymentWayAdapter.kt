package io.temco.guhada.view.adapter.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.order.PaymentMethod
import io.temco.guhada.data.viewmodel.payment.PaymentViewModel
import io.temco.guhada.databinding.ItemPaymentPaymentwayBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 주문결제-결제 수단 list adapter
 * @author Hyeyeon Park
 */
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

    fun getPaymentWay() : String{
        for(i in 0 until list.size){
            if(mViewModel.paymentWays[i])
                return list[i].methodCode
        }
        return list[0].methodCode // default: Card
    }

    inner class Holder(val binding: ItemPaymentPaymentwayBinding) : BaseViewHolder<ItemPaymentPaymentwayBinding>(binding.root) {
        fun bind(paymentMethod: PaymentMethod) {
            if (::mViewModel.isInitialized) {
                binding.radiobuttonPaymentway.tag = adapterPosition.toString()
                binding.paymentMethod = paymentMethod
                binding.viewModel = mViewModel
                binding.radiobuttonPaymentway.tag = adapterPosition
                binding.radiobuttonPaymentway.isChecked = mViewModel.paymentWays[adapterPosition]
                binding.executePendingBindings()
            }
        }
    }

}