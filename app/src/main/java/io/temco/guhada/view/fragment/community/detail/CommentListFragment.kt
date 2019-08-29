package io.temco.guhada.view.fragment.community.detail

import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import io.temco.guhada.R
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommentBinding
import io.temco.guhada.view.adapter.CommentListAdapter
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.base.BaseFragment

class CommentListFragment(val viewModel : CommunityDetailViewModel) : BaseFragment<FragmentCommentBinding>() {
    // -------- LOCAL VALUE --------
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommentListFragment::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.fragment_comment
    override fun init() {
        mBinding.viewModel = viewModel

        if (mBinding.recyclerviewCommentList.adapter == null) {
            viewModel.commentAdapter = CommentListAdapter(viewModel).apply { mList = viewModel.commentList.value!! }
            mBinding.recyclerviewCommentList.adapter = viewModel.commentAdapter
            mBinding.executePendingBindings()
        }
        mBinding.setOnClickCommentMore {
            val popup = PopupMenu(this@CommentListFragment.context, it)
            if(CommonUtil.checkToken() && userCheck(viewModel.userId, viewModel.communityDetail.value?.createUserInfo!!.id)){
                popup.menuInflater.inflate(R.menu.menu_community_detail_owner, popup.menu)
            }else{
                popup.menuInflater.inflate(R.menu.menu_community_detail_other, popup.menu)
            }
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    when(item?.itemId){
                        R.id.community_detail_owner01 -> {}
                        R.id.community_detail_owner02 -> {}
                        R.id.community_detail_other01 -> {
                            if(CommonUtil.checkToken()){
                                CommonUtil.startReportActivity((context as AppCompatActivity), 2, viewModel.communityDetail.value, null)
                            }else{
                                CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                                        cancelButtonVisible = true,
                                        confirmTask = {
                                            CommonUtil.moveLoginPage(viewModel.context as AppCompatActivity)
                                        }).show(manager = (viewModel.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                            }
                        }
                    }
                    return true
                }
            })
            popup.show()
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////



    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

    private fun userCheck(user : Long, cUser : Long) : Boolean{
        return user == cUser
    }

    ////////////////////////////////////////////////


}