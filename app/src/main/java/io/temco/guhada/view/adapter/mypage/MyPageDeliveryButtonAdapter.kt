package io.temco.guhada.view.adapter.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.databinding.LayoutDeliveryButtonBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class MyPageDeliveryButtonAdapter : RecyclerView.Adapter<MyPageDeliveryButtonAdapter.Holder>(){
    var list : MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.layout_delivery_button, parent, false))

    override fun getItemCount(): Int= list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

    }

    inner class Holder(binding: LayoutDeliveryButtonBinding) : BaseViewHolder<LayoutDeliveryButtonBinding>(binding.root) {
        fun bind(text: String) {

        }
    }
}