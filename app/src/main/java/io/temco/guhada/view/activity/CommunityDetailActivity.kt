package io.temco.guhada.view.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.auth0.android.jwt.JWT
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.*
import io.temco.guhada.common.enum.CommunityOrderType
import io.temco.guhada.common.enum.ResultCode
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.community.CommunityInfo
import io.temco.guhada.data.model.main.EventData
import io.temco.guhada.data.viewmodel.community.CommunityDetailViewModel
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.custom.dialog.CustomMessageDialog
import io.temco.guhada.view.fragment.community.detail.CommentListFragment
import io.temco.guhada.view.fragment.community.detail.CommunityDetailContentsFragment
import io.temco.guhada.view.fragment.community.detail.CommunityPostListFragment
import io.temco.guhada.view.viewpager.InfiniteGeneralFixedPagerAdapter

/**
 * @author park jungho
 *
 * 게시글 상세
 *
 */
class CommunityDetailActivity : BindActivity<io.temco.guhada.databinding.ActivityCommunitydetailBinding>(), View.OnClickListener {
    private lateinit var mRequestManager: RequestManager
    private lateinit var mViewModel : CommunityDetailViewModel
    private lateinit var mDetailFragment : CommunityDetailContentsFragment
    private lateinit var mPostListFragment : CommunityPostListFragment
    private lateinit var mCommentFragment : CommentListFragment
    private lateinit var mLoadingIndicatorUtil : LoadingIndicatorUtil

    private var detailReload = false
    private var isLoadData = false
    private var isLoginUser = false

    private var infiniteAdapter: InfiniteGeneralFixedPagerAdapter<EventData>? = null
    private var currentAdIndex : Int = -1
    private var eventListSize = 0
    private var isViewPagerIdle = false
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

        detailReload = false
        isLoginUser = false

        initIntent()
        mLoadingIndicatorUtil = LoadingIndicatorUtil(this)
        mLoadingIndicatorUtil.show()

        mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.addTextChangedListener(object  : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!mViewModel.userLoginCheck.get()){
                    showLoginDialog()
                }else{
                    mViewModel.commentRegText.set(s.toString())
                    checkRegBtn()
                }
            }
        })
        mBinding.linearlayoutCommunitydetailCommentwrite.edittextCommentDetail.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus && !mViewModel.userLoginCheck.get() && isLoadData){
                showLoginDialog()
            }
        }
        mViewModel.mRedirectDetailTask = {
            if(CustomLog.flag)CustomLog.L("mRedirectDetailTask","bbsId",it.bbsId)
            mViewModel.bbsId = it.bbsId
            detailReload = true
            mViewModel.getDetailData()
        }

        if(CommonUtil.checkToken()) isLoginUser = true
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
                    if(!fileNm.substring(0,4).equals("http",true)){
                        if(!TextUtils.isEmpty(fileNm))  mViewModel.commentRegImage.set(fileNm)
                        checkRegBtn()
                    }else{
                        showDialog("외부 이미지 링크가 아닌 파일만 등록 가능합니다.",false)
                    }
                }
                Flag.RequestCode.COMMUNITY_DETAIL_WRT_MOD -> {
                    detailReload = true
                    mViewModel.getDetailData()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(!isLoginUser && CommonUtil.checkToken()){
            isLoginUser = true
            detailReload = true
            mViewModel.getDetailData()
        }
        if(infiniteAdapter != null){
            isViewPagerIdle = true
            homeRolling()
        }
    }

    override fun onPause() {
        super.onPause()
        if(infiniteAdapter != null){
            isViewPagerIdle = false
            mHandler.removeCallbacks(homeAdRolling)
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
            mViewModel.bbsId = intent?.extras?.getLong("bbsId") ?: 0L
            mViewModel.info = intent?.extras?.getSerializable("info") as CommunityInfo
            mViewModel.mFilterId = intent?.extras?.getInt("filterId") ?: 0
            mViewModel.mOrder = intent?.extras?.getString("order") ?: CommunityOrderType.DATE_DESC.type
            mViewModel.mCategoryId = intent?.extras?.getLong("categoryId") ?: -1L
        }
    }

    private fun setDetailView(){
        if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "setDetailView ---------------------")
        mViewModel.communityDetail.observe(this, Observer {
            if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "setDetailView observe ---------------------detailReload",detailReload)
            mLoadingIndicatorUtil?.dismiss()
            if(it.use && !it.delete){
                if(it.createUserInfo == null){
                    CustomMessageDialog(message = "탈퇴한 유저의 글입니다.",
                            cancelButtonVisible = false,
                            confirmTask = {
                                if(!detailReload) finish()
                                detailReload = false
                            }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
                }else{
                    if(detailReload){
                        mDetailFragment.setDetailView()
                        mViewModel.totalPageCount = -1
                        mHandler.postDelayed({
                            mBinding.scrollviewCommunitydetail.smoothScrollTo(0,0)
                            mViewModel.getCommentInitList(null)
                        },100)
                    }else{
                        mViewModel.getCommentList()
                        mViewModel.repository.getCommunityList()
                        mBinding.layoutAppbar.setExpanded(true,true)
                    }
                    isLoadData = true

                    for(info in mViewModel.communityInfoList){
                        if(info.communityCategoryId == mViewModel.communityDetail.value!!.categoryId){
                            mViewModel.info = info
                            mBinding.headerTitle = info.communityCategoryName
                        }
                    }
                }
            }else{
                if(it.delete){
                    CustomMessageDialog(message = "삭제된 글입니다.",
                            cancelButtonVisible = false,
                            confirmTask = {
                                finish()
                            }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
                }
            }
        })
        mViewModel.commentList.observe(this, Observer {
            mHandler.postDelayed({
                if (CustomLog.flag) CustomLog.L("CommunityDetailActivity", "mViewModel.commentList.observe ---------------------detailReload",detailReload)
                if(!detailReload){
                    initDetail()
                    initComment()
                    setViewPager()
                }
                detailReload = false
            },100)
        })
        mViewModel.mCommunityResponse.observe(this, Observer {
            if (it.bbs.isNotEmpty()) {
                if(CustomLog.flag)CustomLog.L("mCommunityResponse","initPostList",it.bbs)
                mHandler.postDelayed({
                    if(!::mPostListFragment.isInitialized) initPostList()
                    else mPostListFragment.mRedirectPostListObserverTask(it)
                },100)
            }
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


    private fun initPostList() {
        mPostListFragment = CommunityPostListFragment(mViewModel)
        supportFragmentManager.beginTransaction().let {
            it.add(mBinding.framelayoutCommunitydetailPostlist.id, mPostListFragment)
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

    private fun showDialog(msg: String, isFinish: Boolean) {
        CustomMessageDialog(message = msg, cancelButtonVisible = false,
                confirmTask = {
                    if (isFinish) finish()
                }).show(manager = this.supportFragmentManager, tag = "ReportActivity")
    }

    private fun showLoginDialog(){
        CustomMessageDialog(message = "로그인 후 이용이 가능합니다.",
                cancelButtonVisible = true,
                confirmTask = {
                    CommonUtil.startLoginPage(this@CommunityDetailActivity)
                }).show(manager = this.supportFragmentManager, tag = "CommunityDetailActivity")
    }

    private fun setOnClick(){
        // 댓글 이미지 찾기
        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentListener {
            if(!mViewModel.userLoginCheck.get()){
                showLoginDialog()
            }
        }
        // 댓글 이미지 찾기
        mBinding.linearlayoutCommunitydetailCommentwrite.setClickCommentImageListener {
            if(!mViewModel.userLoginCheck.get()){
                showLoginDialog()
            }else{
                CommonUtil.startImageGallery(this@CommunityDetailActivity)
            }
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
                            mViewModel.communityDetailCommentTotalElements.set(mViewModel.communityDetailCommentTotalElements.get() + 1)
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
                showLoginDialog()
            }
        }
    }


    private fun setViewPager(){
        if(infiniteAdapter == null){
            val tmpList = java.util.ArrayList<EventData>()
            // 이벤트 더미 데이터 --------------------------------
            tmpList.add(EventData(0, "#d6b5ad", R.drawable.community_banner_mobile, "main_banner_mobile", "", "", 0, ""))
            tmpList.add(EventData(1, "", R.drawable.timedeal_com_m_360, "main_banner_mobile", "", "", 1, ""))

            var metrics = DisplayMetrics()
            isViewPagerIdle = true
            windowManager.defaultDisplay.getMetrics(metrics)
            infiniteAdapter = object : InfiniteGeneralFixedPagerAdapter<EventData>(tmpList, true, true){
                override fun getPageView(paramViewGroup: ViewGroup, paramInt: Int, item: EventData): View {
                    val imageView = ImageView(paramViewGroup.context)
                    if(item.imgPath.isNullOrEmpty()){
                        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        imageView.setBackgroundResource(tmpList[paramInt].imgRes)
                        imageView.setOnClickListener {
                            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, 4,true,true)
                            setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                            onBackPressed()
                        }
                    }else{
                        imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        imageView.setBackgroundColor(Color.parseColor(item.imgPath))
                        imageView.setImageResource(tmpList[paramInt].imgRes)
                        imageView.setOnClickListener {
                            BaseApplication.getInstance().moveToMain = ActivityMoveToMain(ResultCode.GO_TO_MAIN_HOME.flag, true,true)
                            setResult(Flag.ResultCode.GO_TO_MAIN_HOME)
                            onBackPressed()
                        }
                    }
                    //ImageUtil.loadImage(Glide.with(containerView.context as Activity),imageView, data.eventList[paramInt].imgPath)
                    return imageView
                }
                override fun getPageTitle(position: Int): CharSequence? = ""
                override fun getPagerIcon(position: Int): Int = 0
                override fun getPagerIconBackground(position: Int): Int = 0
            }
            mBinding.viewPager.adapter = infiniteAdapter

            if(currentAdIndex == -1){
                eventListSize = mBinding.viewPager.offsetAmount
                currentAdIndex = mBinding.viewPager.currentItem
            }
            mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                override fun onPageScrollStateChanged(state: Int) {
                    isViewPagerIdle = state == ViewPager.SCROLL_STATE_IDLE
                }
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {  }
                override fun onPageSelected(position: Int) {
                    currentAdIndex = position
                    mViewModel.communityEventViewIndex.set(mBinding.viewPager.realCurrentItem)
                }
            })
            homeRolling()
        }
    }

    private var homeAdRolling =  Runnable {
        try{
            if(infiniteAdapter != null && isViewPagerIdle){
                if(currentAdIndex > (eventListSize * 1000) -100) currentAdIndex = (eventListSize*1000) / 2
                mBinding.viewPager.setCurrentItemSmooth(currentAdIndex+1)
            }
        }catch (e:Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        homeRolling()
    }


    private fun homeRolling(){
        try{
            mHandler.removeCallbacks(homeAdRolling)
            mHandler.postDelayed(homeAdRolling,5000)
        }catch (e:Exception){
            mHandler.removeCallbacks(homeAdRolling)
            if(CustomLog.flag)CustomLog.E(e)
        }
    }


    ////////////////////////////////////////////////





}