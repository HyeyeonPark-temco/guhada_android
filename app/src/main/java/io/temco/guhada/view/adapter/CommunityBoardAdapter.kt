package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonViewUtil
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.databinding.ItemCommunityPhotoBinding
import io.temco.guhada.databinding.ItemCommunityTextBinding
import io.temco.guhada.view.fragment.community.CommunitySubListFragment
import io.temco.guhada.view.holder.base.BaseViewHolder

class CommunityBoardAdapter(val type: String) : RecyclerView.Adapter<CommunityBoardAdapter.Holder>() {
    var mList = mutableListOf<CommunityBoard>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
                if (type == CommunitySubListFragment.CommunityListType.IMAGE.type)
                    DataBindingUtil.inflate<ItemCommunityPhotoBinding>(LayoutInflater.from(parent.context), R.layout.item_community_photo, parent, false)
                else
                    DataBindingUtil.inflate<ItemCommunityTextBinding>(LayoutInflater.from(parent.context), R.layout.item_community_text, parent, false)
        return Holder(binding, type)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    class Holder(binding: ViewDataBinding, val type: String) : BaseViewHolder<ViewDataBinding>(binding.root) {
        fun bind(item: CommunityBoard) {
            if (type == CommunitySubListFragment.CommunityListType.IMAGE.type)
                (mBinding as ItemCommunityPhotoBinding).item = item
            else
                (mBinding as ItemCommunityTextBinding).item = item

            setSpacing()
            binding.executePendingBindings()
        }

        private fun setSpacing() {
            if (type == CommunitySubListFragment.CommunityListType.IMAGE.type) {
                ((mBinding as ItemCommunityPhotoBinding).constraintlayoutCommunityphotoContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    if (adapterPosition % 2 == 0) {
                        leftMargin = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
                        rightMargin = CommonViewUtil.convertDpToPixel(dp = 5, context = mBinding.root.context)
                    } else {
                        leftMargin = CommonViewUtil.convertDpToPixel(dp = 5, context = mBinding.root.context)
                        rightMargin = CommonViewUtil.convertDpToPixel(dp = 10, context = mBinding.root.context)
                    }
                }.let {
                    (mBinding as ItemCommunityPhotoBinding).constraintlayoutCommunityphotoContainer.layoutParams = it
                }
            }
        }
    }
}