package io.temco.guhada.view.activity

import android.os.Handler
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
import io.temco.guhada.view.fragment.community.detail.CommentListFragment
import io.temco.guhada.view.fragment.community.detail.CommunityDetailContentsFragment
import java.lang.Exception

class CommunityDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityCommunitydetailBinding>(), View.OnClickListener {
    private lateinit var mRequestManager: RequestManager
    private var mLoadingIndicatorUtil : LoadingIndicatorUtil? = null
    private lateinit var mViewModel : CommunityDetailViewModel
    private lateinit var mDetailFragment : CommunityDetailContentsFragment
    private lateinit var mCommentFragment : CommentListFragment
    private lateinit var mHandler: Handler

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_communitydetail
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE

    override fun init() {
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "init ---------------------")
        mViewModel = CommunityDetailViewModel(this)
        mBinding.viewModel = mViewModel
        mBinding.clickListener = this
        mRequestManager = Glide.with(this)
        mHandler = Handler(this.mainLooper)
        initIntent()
        if(mLoadingIndicatorUtil == null){
            mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
            mLoadingIndicatorUtil?.show()
        }
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
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "setDetailView ---------------------")
        mViewModel.communityDetail.observe(this, Observer {
            mLoadingIndicatorUtil?.dismiss()
            mViewModel.getCommentList()
            initDetail()
        })
        mViewModel.commentList.observe(this, Observer {
            mHandler.postDelayed({ initComment() },500)
        })
        mViewModel.getDetailData()
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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            if (mLoadingIndicatorUtil != null) {
                mLoadingIndicatorUtil?.dismiss()
                mLoadingIndicatorUtil = null
            }
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }

}