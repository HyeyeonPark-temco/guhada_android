package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnClickSelectItemListener
import io.temco.guhada.data.model.SellerInquireOrder
import io.temco.guhada.databinding.ItemUserclaimsellerSpinnerBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 *
 */
class UserClaimSellerProductAdapter: RecyclerView.Adapter<UserClaimSellerProductAdapter.Holder>() {
    var mList: MutableList<SellerInquireOrder> = arrayListOf()

    var mClickSelectItemListener : OnClickSelectItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_userclaimseller_spinner, parent, false))

    override fun getItemCount(): Int = mList.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position],position)
    }

    fun setItems(list: MutableList<SellerInquireOrder>) {
        this.mList = list
        notifyDataSetChanged()
    }

    inner class Holder(binding: ItemUserclaimsellerSpinnerBinding) : BaseViewHolder<ItemUserclaimsellerSpinnerBinding>(binding.root) {
        fun bind(item: SellerInquireOrder, position: Int) {
            mBinding.brand = item.brandName
            mBinding.season = item.season
            mBinding.title = item.productName
            mBinding.image = item.imageUrl
            mBinding.option = ""
            mBinding.deliveryComplete = item.statusText
            if(position != mList.size-1) mBinding.viewUserclaimsellerBar.visibility = View.VISIBLE
            else mBinding.viewUserclaimsellerBar.visibility = View.GONE
            itemView.setOnClickListener { mClickSelectItemListener?.clickSelectItemListener(0,position,item) }
            mBinding.executePendingBindings()
        }
    }


}
