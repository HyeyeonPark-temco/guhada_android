package io.temco.guhada.view.activity

import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.fragment.comment.CommentListFragment
import io.temco.guhada.view.fragment.community.detail.CommunityDetailContentsFragment
import java.lang.Exception

class CommunityDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityCommunitydetailBinding>(), View.OnClickListener {
    private lateinit var mRequestManager: RequestManager
    private lateinit var mLoadingIndicatorUtil : LoadingIndicatorUtil
    private lateinit var mViewModel : CommunityDetailViewModel
    private lateinit var mDetailFragment : CommunityDetailContentsFragment
    private lateinit var mCommentFragment : CommentListFragment

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_communitydetail
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE


    override fun init() {
        mViewModel = CommunityDetailViewModel(this)
        mBinding.viewModel = mViewModel
        mRequestManager = Glide.with(this)

        initIntent()
        setDetailView()
    }


    private fun initIntent(){
        if(intent?.extras?.containsKey("bbsId")!!){
            mViewModel.bbsId = intent.extras.getLong("bbsId")
            mViewModel.info = intent.getSerializableExtra("info") as CommunityInfo
            if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "mViewModel.info ",mViewModel.info.toString())
        }
    }

    private fun setDetailView(){
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.show()
        mViewModel.communityDetail.observe(this, Observer {
            mLoadingIndicatorUtil.hide()
            if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "setDetailView ",it.toString())
            initDetail()
            initComment()
        })
        mViewModel.getDetaileData()
        mBinding.headerTitle = mViewModel.info.communityCategoryName
    }

    private fun initDetail() {
        mDetailFragment = CommunityDetailContentsFragment(mViewModel)
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutCommunitydetailDetail.id, mDetailFragment)
            it.commitAllowingStateLoss()
        }
    }


    private fun initComment() {
        mCommentFragment = CommentListFragment(mViewModel)
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutCommunitydetailComment.id, mCommentFragment)
            it.commitAllowingStateLoss()
        }
    }



    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_communitydetail_back-> finish()
            R.id.imagebutton_headerproductdetail_search-> CommonUtil.startSearchWordActivity(this,"",true)
            R.id.imagebutton_headerproductdetail_bag-> CommonUtil.startCartActivity(this)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try{
            if (::mLoadingIndicatorUtil.isInitialized && mLoadingIndicatorUtil.isShowing) mLoadingIndicatorUtil.dismiss()
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }

}