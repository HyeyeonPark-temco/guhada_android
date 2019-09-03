package io.temco.guhada.view.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.DateUtil
import io.temco.guhada.data.model.Comments
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.ItemCommentListBinding
import io.temco.guhada.databinding.ItemMoreListBinding
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
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
        if(data.delete){
            binding.isDelete = true
            binding.isContent = true
            binding.isLikeComment = false
            binding.isImage = false
            binding.contents = data.contents
            binding.isReply = data.originCommentId != null
            binding.userGrade = ""
            binding.setClickReplyListener{null}
            binding.setClickClaimListener{null}
            binding.setClickDeleteListener{null}
            binding.setClickModifyListener{null}
        }else{
            binding.isDelete = false
            binding.userGrade = "99"
            binding.userProfile = data.createUserInfo.profileImageUrl
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
                if(CommonUtil.checkToken()){
                    data.isModify = false
                    model.onClickReplyAndModify(data,null)
                }else{
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.moveLoginPage(model.context as AppCompatActivity)
                            }).show(manager = (model.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }
            binding.setClickClaimListener{
                if(CommonUtil.checkToken()){
                    CommonUtil.startReportActivity((model.context as AppCompatActivity), 3, data, model.communityDetail.value)
                }else{
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.moveLoginPage(model.context as AppCompatActivity)
                            }).show(manager = (model.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }

            binding.setClickDeleteListener{
                CustomMessageDialog(message = "삭제하시겠습니까?",
                        cancelButtonVisible = true,
                        confirmTask = {
                            model.deleteComment(data.id, position, object : OnCallBackListener{
                                override fun callBackListener(resultFlag: Boolean, value: Any) {
                                    model.dismissLoadingDialog()
                                    if(resultFlag){
                                        model.commentAdapter?.mList?.get(position)!!.delete = true
                                        model.commentAdapter?.mList?.get(position)!!.contents = "삭제된 댓글입니다."
                                        model.commentAdapter?.notifyItemChanged(position)
                                    }
                                }
                            })
                        }).show(manager = (model.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
            }

            binding.setClickModifyListener{
                if(CommonUtil.checkToken()){
                    val stringProject = Gson().toJson(data, Comments::class.java)
                    var modi : Comments = Gson().fromJson<Comments>(stringProject, Comments::class.java)
                    modi.isModify = true
                    model.onClickReplyAndModify(modi, position)
                }else{
                    CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                CommonUtil.moveLoginPage(model.context as AppCompatActivity)
                            }).show(manager = (model.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }

            if(data.originCreaterUser != null){
                var modifyContents = "@"+data.originCreaterUser.nickname + " " + data.contents

                val ssb = SpannableStringBuilder(modifyContents)
                ssb.setSpan(StyleSpan(Typeface.BOLD), 0, data.originCreaterUser.nickname.length+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Style
                ssb.setSpan(ForegroundColorSpan(Color.parseColor("#111111")),
                        0, data.originCreaterUser.nickname.length+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) // Color
                binding.contents = ssb
            }else{
                binding.contents = data.contents
            }
            if(CommonUtil.checkToken()){
                binding.isUserOwnerCheck = userCheck(model.userId, data.createUserInfo.id)
            }else{
                binding.isUserOwnerCheck = false
            }
        }

        binding.executePendingBindings()
    }

    private fun userCheck(user : Long, cUser : Long) : Boolean{
        return user == cUser
    }

}


class MoreListViewHolder(containerView: View, val binding: ItemMoreListBinding) : ListViewHolder<Comments>(containerView,binding){
    override fun bind(model : ViewModel, position : Int, data: Comments){
        binding.linearlayoutMoreView.setOnClickListener {
            (model as CommunityDetailViewModel).getCommentList()
        }
    }
}
