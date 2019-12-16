package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.community.CommunityBoard
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.ItemCommunityPostListBinding
import io.temco.guhada.view.holder.base.BaseViewHolder

/**
 * 커뮤니티 게시판 어댑터
 * @author park jungho
 */
class CommunityPostListAdapter(val type: String) : RecyclerView.Adapter<CommunityPostListAdapter.Holder>() {
    var mList = mutableListOf<CommunityBoard>()
    lateinit var mViewModel: CommunityDetailViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = DataBindingUtil.inflate<ItemCommunityPostListBinding>(LayoutInflater.from(parent.context), R.layout.item_community_post_list, parent, false)
        return Holder(binding, mViewModel, type)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mList[position])
    }

    class Holder(binding: ViewDataBinding, val mViewModel: CommunityDetailViewModel, val type: String) : BaseViewHolder<ViewDataBinding>(binding.root) {
        fun bind(item: CommunityBoard) {
            (mBinding as ItemCommunityPostListBinding).item = item
            (mBinding as ItemCommunityPostListBinding).viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }
}