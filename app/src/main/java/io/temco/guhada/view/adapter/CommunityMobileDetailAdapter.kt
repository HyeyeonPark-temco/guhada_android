package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.community.CommunityMobileDetail
import io.temco.guhada.data.model.community.CommunityMobileDetailImage
import io.temco.guhada.data.model.community.CommunityMobileDetailText
import io.temco.guhada.data.model.community.CommunityMobileDetailType
import io.temco.guhada.databinding.ItemCommunitydetailImageBinding
import io.temco.guhada.databinding.ItemCommunitydetailTextBinding
import io.temco.guhada.view.holder.base.BaseViewHolder


class CommunityMobileDetailAdapter : RecyclerView.Adapter<ViewHolder<CommunityMobileDetail>>() {
    var mList: MutableList<CommunityMobileDetail> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        when (mList[position].type == CommunityMobileDetailType.IMAGE){
            true -> return 1
            false -> return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<CommunityMobileDetail> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType){
            1 -> {
                var res = R.layout.item_communitydetail_image
                val binding: ItemCommunitydetailImageBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                return CommunityImageViewHolder(binding.root, binding)
            }
            else -> {
                var res = R.layout.item_communitydetail_text
                val binding: ItemCommunitydetailTextBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                return CommunityTextViewHolder(binding.root, binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<CommunityMobileDetail>, position: Int) {
        holder.bind(position, mList[position])
    }


    fun setItems(list: MutableList<CommunityMobileDetail>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size


}


open abstract class ViewHolder<T>(containerView: View, binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>(containerView){
    abstract fun bind(position : Int, data : T)
}


class CommunityImageViewHolder(containerView: View, binding: ItemCommunitydetailImageBinding) : ViewHolder<CommunityMobileDetail>(containerView, binding) {
    override fun bind(position: Int, data: CommunityMobileDetail) {

        (binding as ItemCommunitydetailImageBinding).imagePath = (data as CommunityMobileDetailImage).image.url
    }
}


class CommunityTextViewHolder(containerView: View, binding: ItemCommunitydetailTextBinding) : ViewHolder<CommunityMobileDetail>(containerView,binding){
    override fun bind(position : Int, data: CommunityMobileDetail){
        (binding as ItemCommunitydetailTextBinding).text = (data as CommunityMobileDetailText).text
    }
}
