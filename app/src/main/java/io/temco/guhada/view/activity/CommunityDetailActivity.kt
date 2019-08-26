package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.Observer
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Preferences
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.community.detail.CommentListFragment
import io.temco.guhada.view.fragment.community.detail.CommunityDetailContentsFragment

class CommunityDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityCommunitydetailBinding>(), View.OnClickListener {
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel : CommunityDetailViewModel
    private lateinit var mDetailFragment : CommunityDetailContentsFragment
    private lateinit var mCommentFragment : CommentListFragment
    private lateinit var mLoadingIndicatorUtil : LoadingIndicatorUtil
    private lateinit var mHandler: Handler
    // -----------------------------

    ////////////////////////////////////////////////
    // OVERRIDE
    ////////////////////////////////////////////////

    override fun getBaseTag(): String = CommunityDetailActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_communitydetail
    override fun getViewType(): Type.View = Type.View.REVIEW_WRITE


    override fun init() {
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "init ---------------------")
        mViewModel = CommunityDetailViewModel(this)
        mBinding.viewModel = mViewModel
        mBinding.linearlayoutCommunitydetailCommentwrite.viewModel = mViewModel

        mBinding.clickListener = this
        mRequestManager = Glide.with(this)
        mHandler = Handler(this.mainLooper)
        initIntent()
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mLoadingIndicatorUtil.show()

        mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mViewModel.commentRegText.set(s.toString())
                checkRegBtn()
            }
        })

        setDetailView()

        setOnClick()
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.button_communitydetail_back-> finish()
            R.id.imagebutton_headerproductdetail_search-> CommonUtil.startSearchWordActivity(this,"",true)
            R.id.imagebutton_headerproductdetail_bag-> CommonUtil.startCartActivity(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                Flag.RequestCode.IMAGE_GALLERY -> {
                    var fileNm = data!!.extras.getString("file_name")
                    if(!TextUtils.isEmpty(fileNm))  mViewModel.commentRegImage.set(fileNm)
                    checkRegBtn()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(CommonUtil.checkToken()){
            mViewModel.userLoginCheck.set(true)
            Preferences.getToken().let { token ->
                if (token?.accessToken != null) {
                    mViewModel.userId = JWT(token?.accessToken!!).getClaim("userId").asLong() ?: -1L
                    if(mViewModel.commentAdapter!=null)mViewModel.commentAdapter?.notifyDataSetChanged()
                }
            }
        }else{
            mViewModel.userId = -1L
            mViewModel.userLoginCheck.set(false)
            if(mViewModel.commentAdapter!=null)mViewModel.commentAdapter?.notifyDataSetChanged()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            if (::mLoadingIndicatorUtil.isInitialized) {
                mLoadingIndicatorUtil.dismiss()
            }
        }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
    }

    ////////////////////////////////////////////////
    // PUBLIC
    ////////////////////////////////////////////////

    fun getLoading() : LoadingIndicatorUtil = mLoadingIndicatorUtil
    fun getHandler() : Handler = mHandler

    ////////////////////////////////////////////////
    // PRIVATE
    ////////////////////////////////////////////////

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
            if(it.use && !it.delete){
                mViewModel.getCommentList()
                mBinding.layoutAppbar.setExpanded(true,true)
            }else{
                if(it.delete){
                    CustomMessageDialog(message = "삭제된 글입니다.",
                            cancelButtonVisible = true,
                            confirmTask = {
                                finish()
                            }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }
        })
        mViewModel.commentList.observe(this, Observer {
            mHandler.postDelayed({
                initDetail()
                initComment()
            },100)
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

    private fun checkRegBtn(){
        if(!TextUtils.isEmpty(mViewModel.commentRegText.get()) || !TextUtils.isEmpty(mViewModel.commentRegImage.get())){
            mViewModel.commentBtnVisible.set(true)
        }else{
            mViewModel.commentBtnVisible.set(false)
        }
    }


    private fun setOnClick(){
        // 댓글 이미지 찾기
        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentImageListener {
            CommonUtil.startImageGallery(this@CommunityDetailActivity)
        }

        // 댓글 이미지 삭제
        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentImageDelListener {
            mViewModel.commentRegImage.set("")
            checkRegBtn()
        }

        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentReplyClearListener {
            mViewModel.postCommentDataClear()
            mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.text = Editable.Factory.getInstance().newEditable("")
        }


        // 댓글 등록 하기
        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentRegListener {
            if(CommonUtil.checkToken()){
                if(!TextUtils.isEmpty(mViewModel.commentRegText.get()) || !TextUtils.isEmpty(mViewModel.commentRegImage.get())){
                    mLoadingIndicatorUtil?.show()
                    mHandler.postDelayed({
                        CommonUtil.hideKeyboard(this@CommunityDetailActivity, mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail)
                    },200)
                    mViewModel.postCommentData(object : OnCallBackListener {
                        override fun callBackListener(resultFlag: Boolean, value: Any) {
                            mLoadingIndicatorUtil?.dismiss()
                            mViewModel.postCommentDataClear()
                            mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.text = Editable.Factory.getInstance().newEditable("")
                            for (v in mViewModel.commentAdapter?.mList!!){
                                if(CustomLog.flag)CustomLog.L("linearlayoutCommunitydetailCommentwrite",v.toString())
                            }
                        }
                    })
                }else{
                    CustomMessageDialog(message = "댓글의 내용을 입력하거나 이미지를 선택해주세요.",
                            cancelButtonVisible = false,
                            confirmTask = {
                            }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }else{
                CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                        cancelButtonVisible = true,
                        confirmTask = {
                            CommonUtil.moveLoginPage(this@CommunityDetailActivity)
                        }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
            }
        }
    }

    ////////////////////////////////////////////////





}