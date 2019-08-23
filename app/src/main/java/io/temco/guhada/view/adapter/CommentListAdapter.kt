package io.temco.guhada.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.Comments
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.ItemCommentListBinding
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.view.holder.base.BaseViewHolder


class CommentListAdapter (private val model: ViewModel) : RecyclerView.Adapter<ListViewHolder<Comments>>() {
    var mList: MutableList<Comments> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        when (mList[position].id > 0){
            true -> return 1
            false -> return 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder<Comments> {
        val layoutInflater = LayoutInflater.from(parent.context)
        when(viewType){
            1 -> {
                var res = R.layout.item_comment_list
                val binding: ItemCommentListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                return CommentListViewHolder(binding.root, binding)
            }
            else -> {
                var res = R.layout.item_more_list
                val binding: ItemMoreListBinding = DataBindingUtil.inflate(layoutInflater, res, parent, false)
                return MoreListViewHolder(binding.root, binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder<Comments>, position: Int) {
        if(CustomLog.flag)CustomLog.L("onBindViewHolder position " + position,mList[position])
        holder.bind(model, position, mList[position])
    }


    fun setItems(list: MutableList<Comments>) {
        this.mList = list
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size


}


open abstract class ListViewHolder<T>(containerView: View, binding: ViewDataBinding) : BaseViewHolder<ViewDataBinding>(containerView){
    abstract fun bind(viewModel : ViewModel, position : Int, data : T)
}

/**
 * 댓글 리스트
 */
class CommentListViewHolder(containerView: View, val binding: ItemCommentListBinding) : ListViewHolder<Comments>(containerView, binding) {
    override fun bind(model: ViewModel, position: Int, data: Comments) {
        try{
            if(CustomLog.flag) CustomLog.L("CommentListViewHolder", "position",position)
            if(CustomLog.flag) CustomLog.L("CommentListViewHolder", "contents",data.contents)
            if(CustomLog.flag) CustomLog.L("CommentListViewHolder", data.createUserInfo.nickname)
            binding.userProfile = data.createUserInfo.profileImageUrl?:""
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        binding.contents = data.contents
        binding.createTime = DateUtil.getDateDiff(data.currentTimestamp,data.createdTimestamp)
        binding.userName = data.createUserInfo.nickname
        binding.isReply = data.originCommentId != null
        binding.isLikeComment = false
        binding.likeCount = 0
        binding.isContent = !"".equals(data.contents)
        if(data.commentImageList.isNullOrEmpty()){
            binding.isImage = false
        }else{
            binding.isImage = true
            binding.image = data.commentImageList[0].url
        }
        binding.viewModel = model as CommunityDetailViewModel
        binding.setClickReplyListener{
            model.onClickReply(data)
        }
        binding.executePendingBindings()
    }
}


class MoreListViewHolder(containerView: View, val binding: ItemMoreListBinding) : ListViewHolder<Comments>(containerView,binding){
    override fun bind(model : ViewModel, position : Int, data: Comments){
        binding.linearlayoutMoreView.setOnClickListener {
            (model as CommunityDetailViewModel).getCommentList()
        }
    }
}
