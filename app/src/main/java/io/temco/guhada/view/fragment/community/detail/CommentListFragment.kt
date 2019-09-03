package io.temco.guhada.view.fragment.community.detail

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.databinding.FragmentCommentBinding
import io.temco.guhada.view.activity.CreateBbsActivity
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
                        R.id.community_detail_owner01 -> {
                            var userId = CommonUtil.checkUserId()
                            if(CustomLog.flag)CustomLog.L("setOnMenuItemClickListener","userId",userId)
                            if(userId > 0 && userId == viewModel.communityDetail.value!!.createUserInfo.id) {
                                if("MOBILE".equals(viewModel.communityDetail.value!!.guhadaClientPlatform, true)){
                                    var intent = Intent((context as AppCompatActivity), CreateBbsActivity::class.java)
                                    intent.putExtra("currentIndex",0)
                                    intent.putExtra("modifyData",viewModel.communityDetail.value!!.setBbsResponse())
                                    intent.putExtra("bbsId",viewModel.bbsId)
                                    (context as AppCompatActivity).startActivityForResult(intent, Flag.RequestCode.COMMUNITY_DETAIL_WRT_MOD)
                                }else{
                                    CustomMessageDialog(message = "모바일에서 등록된 글만 수정이 가능합니다.",
                                            cancelButtonVisible = false,
                                            confirmTask = {
                                            }).show(manager = (viewModel.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                                }
                            }else{
                                CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                                        cancelButtonVisible = true,
                                        confirmTask = {
                                            CommonUtil.moveLoginPage(viewModel.context as AppCompatActivity)
                                        }).show(manager = (viewModel.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                            }
                        }
                        R.id.community_detail_owner02 -> {
                            CustomMessageDialog(message = "글을 삭제 하시겠습니까?",
                                    cancelButtonVisible = true,
                                    confirmTask = {
                                        viewModel.deleteDetail(object : OnCallBackListener{
                                            override fun callBackListener(resultFlag: Boolean, value: Any) {
                                                CustomMessageDialog(message = "삭제되었습니다.",
                                                        cancelButtonVisible = false,
                                                        confirmTask = {
                                                            (context as Activity).setResult(Activity.RESULT_OK)
                                                            (context as Activity).finish()
                                                        }).show(manager = (viewModel.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                                            }
                                        })
                                    }).show(manager = (viewModel.context as AppCompatActivity).supportFragmentManager, tag = "CommunityDetailActivity")
                        }
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