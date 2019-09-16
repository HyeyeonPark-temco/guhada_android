package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnBaseDialogListener
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.UserShipping
import io.temco.guhada.data.viewmodel.shippingaddress.ShippingAddressViewModel
import io.temco.guhada.databinding.ItemShippingaddressListBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

class ShippingAddressListAdapter(val mViewModel: ShippingAddressViewModel) : RecyclerView.Adapter<ShippingAddressListAdapter.Holder>() {
    var radioButtonVisible = true
    var prevPos = -1
    var currentPos = -1
    var deletePos = -1
    var list: MutableList<UserShipping> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemShippingaddressListBinding>(LayoutInflater.from(parent.context), R.layout.item_shippingaddress_list, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }

    fun setItems(list: MutableList<UserShipping>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun deleteItem() {
        if (deletePos > -1) {
            list.removeAt(deletePos)
            if (list.size == 1) currentPos = 0 // 배송지 1개 남았을 경우
            this@ShippingAddressListAdapter.notifyDataSetChanged()
            ToastUtil.showMessage("선택하신 배송지가 삭제되었습니다.")
        }
    }

    fun initPoses() {
        currentPos = -1
        prevPos = -1
        deletePos = -1
    }

    inner class Holder(val binding: ItemShippingaddressListBinding) : BaseViewHolder<ItemShippingaddressListBinding>(binding.root) {
        fun bind(shipping: UserShipping) {
            if (mViewModel.selectedItem.id == shipping.id) currentPos = adapterPosition
            binding.radioButtonVisible = radioButtonVisible
            binding.radiobuttonShippingaddresslist.setOnCheckedChangeListener(null)
            binding.radiobuttonShippingaddresslist.isChecked = currentPos == adapterPosition || mViewModel.selectedItem.id == shipping.id
            binding.viewModel = mViewModel
            binding.shipping = shipping
            binding.radiobuttonShippingaddresslist.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    mViewModel.selectedItem = list[adapterPosition]
                }
                prevPos = currentPos
                currentPos = adapterPosition
                this@ShippingAddressListAdapter.notifyItemChanged(prevPos)
                this@ShippingAddressListAdapter.notifyItemChanged(currentPos)
            }
            binding.textviewShippingaddressDelete.setOnClickListener {
                val id = list[adapterPosition].id
                if (mViewModel.prevSelectedItem.id == id) {
                    ToastUtil.showMessage("현재 선택된 배송지입니다.")
                } else {
                    CommonViewUtil.showDialog((binding.root.context as AppCompatActivity),"해당 배송지를 삭제하시겠습니까?",true, object : OnBaseDialogListener{
                        override fun onClickOk() {
                            deletePos = adapterPosition
                            mViewModel.deleteShippingAddress(id)
                        }
                    })
                }
            }
            binding.textviewShippingaddressEdit.setOnClickListener {
                mViewModel.editShippingAddress(adapterPosition)
            }

            if (adapterPosition == itemCount - 1) binding.viewShippingaddresslistLine.visibility = View.GONE
            else binding.viewShippingaddresslistLine.visibility = View.VISIBLE

            binding.executePendingBindings()
        }
    }
}
