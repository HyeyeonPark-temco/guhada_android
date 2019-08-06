package io.temco.guhada.view.adapter.mypage

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.data.model.order.PurchaseOrder
import io.temco.guhada.databinding.ItemDeliveryBinding
import io.temco.guhada.view.activity.DeliveryDetailActivity
import io.temco.guhada.view.holder.base.BaseViewHolder

class MyPageDeliveryCerAdapter : RecyclerView.Adapter<MyPageDeliveryCerAdapter.Holder>() {
    var list: MutableList<PurchaseOrder> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_delivery, parent, false))
    override fun getItemCount(): Int = list.size
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(list[position])

    fun setItems(list: MutableList<PurchaseOrder>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemDeliveryBinding) : BaseViewHolder<ItemDeliveryBinding>(binding.root) {
        fun bind(item: PurchaseOrder) {
            mBinding.item = item

            // set buttons
            val buttons = getButtons(item)
            mBinding.recyclerviewDeliveryButton.adapter = MyPageDeliveryButtonAdapter().apply { this.list = buttons }
            (mBinding.recyclerviewDeliveryButton.layoutManager as GridLayoutManager).spanCount = if (buttons.size > 1) 2 else 1

            // image click listener
            mBinding.imageviewDeliveryProfile.setOnClickListener {
                val data = EventBusData(requestCode = RequestCode.PRODUCT_DETAIL.flag, data = item.dealId)
                EventBusHelper.sendEvent(data)
            }

            mBinding.imageviewDeliveryOrdernumber.setOnClickListener { redirectDeliveryDetailActivity(item.purchaseId) }
            mBinding.textviewDeliveryOrdernumber.setOnClickListener { redirectDeliveryDetailActivity(item.purchaseId) }

            mBinding.executePendingBindings()
        }

        private fun getButtons(item: PurchaseOrder): MutableList<MyPageDeliveryAdapter.DeliveryButton> {
            val buttons = mutableListOf<MyPageDeliveryAdapter.DeliveryButton>()
            when (item.purchaseStatus) {

            }
            return buttons
        }

        private fun redirectDeliveryDetailActivity(purchaseId: Long) {
            val intent = Intent(binding.root.context, DeliveryDetailActivity::class.java)
            intent.putExtra("purchaseId", purchaseId)
            binding.root.context.startActivity(intent)
        }
    }
}