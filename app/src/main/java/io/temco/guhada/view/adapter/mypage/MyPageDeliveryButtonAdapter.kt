package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.DeliveryButton
import io.temco.guhada.databinding.ItemDeliveryButtonBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 마이페이지 주문배송, 취소교환반품 리스트 아이템 버튼 adapter
 * @author Hyeyeon Park
 */
class MyPageDeliveryButtonAdapter : RecyclerView.Adapter<MyPageDeliveryButtonAdapter.Holder>() {
    var list: MutableList<DeliveryButton> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_delivery_button, parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    inner class Holder(binding: ItemDeliveryButtonBinding) : BaseViewHolder<ItemDeliveryButtonBinding>(binding.root) {
        fun bind(item: DeliveryButton) {
            setSpacing()
            mBinding.text = item.text
            mBinding.onClickListener = item.task
            mBinding.executePendingBindings()
        }

        private fun setSpacing() {
            val layoutParams = (binding.framelayoutDeliveryButton.layoutParams as ViewGroup.MarginLayoutParams)
            layoutParams.bottomMargin = 40
            if (adapterPosition % 2 == 0) layoutParams.rightMargin = 20
            else layoutParams.leftMargin = 20
            binding.framelayoutDeliveryButton.layoutParams = layoutParams
        }
    }
}