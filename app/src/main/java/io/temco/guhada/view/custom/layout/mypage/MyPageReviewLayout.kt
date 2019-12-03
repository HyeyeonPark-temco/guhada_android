package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.enum.RequestCode
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.model.review.MyPageReviewBase
import io.temco.guhada.data.viewmodel.mypage.MyPageReviewViewModel
import io.temco.guhada.databinding.CustomlayoutMypageReviewBinding
import io.temco.guhada.view.WrapContentLinearLayoutManager
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import java.util.ArrayList

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 상품리뷰 화면
 *
 */
class MyPageReviewLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageReviewBinding, MyPageReviewViewModel>(context, attrs, defStyleAttr) , SwipeRefreshLayout.OnRefreshListener{

    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil
    private lateinit var recyclerLayoutManager1 : WrapContentLinearLayoutManager
    private lateinit var recyclerLayoutManager2 : WrapContentLinearLayoutManager

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_review
    override fun init() {
        mLoadingIndicatorUtil = LoadingIndicatorUtil(context)
        mViewModel = MyPageReviewViewModel(context, mLoadingIndicatorUtil!!)
        mBinding.viewModel = mViewModel

        mBinding.recyclerViewReviewTab1.setHasFixedSize(true)
        recyclerLayoutManager1 = WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
            orientation = RecyclerView.VERTICAL
            recycleChildrenOnDetach = true
        }
        mBinding.recyclerViewReviewTab1.layoutManager = recyclerLayoutManager1
        mBinding.recyclerViewReviewTab1.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(CustomLog.flag)CustomLog.L("MyPageBookMarkLayout","itemCount",mViewModel.getAvailableAdapter().itemCount,"findLastVisibleItemPosition",recyclerLayoutManager1.findLastVisibleItemPosition())
                if (mViewModel.getAvailableAdapter().itemCount - recyclerLayoutManager1.findLastVisibleItemPosition() <= 2 && !mViewModel.isLoading1) {
                    mViewModel.isLoading1 = true
                    mViewModel.getMoreTab1List()
                }
            }
        })

        mViewModel.listAvailableReviewOrder.observe(this,
                androidx.lifecycle.Observer<ArrayList<MyPageReviewBase>> {
                    if(CustomLog.flag)CustomLog.L("MyPageReviewLayout","mViewModel.getAvailableAdapter().items.size" + mViewModel.getAvailableAdapter().items.size)
                    if(mViewModel.getAvailableAdapter().items.size > 0) {
                        mViewModel.tab1EmptyViewVisible.set(false)
                    }else{
                        mViewModel.tab1EmptyViewVisible.set(true)
                    }
                }
        )

        mBinding.recyclerViewReviewTab2.setHasFixedSize(true)
        recyclerLayoutManager2 = WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false).apply {
            orientation = RecyclerView.VERTICAL
            recycleChildrenOnDetach = true
        }
        mBinding.recyclerViewReviewTab2.layoutManager = recyclerLayoutManager2
        mBinding.recyclerViewReviewTab2.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(CustomLog.flag)CustomLog.L("MyPageBookMarkLayout","itemCount",mViewModel.getReviewAdapter().itemCount,"findLastVisibleItemPosition",recyclerLayoutManager2.findLastVisibleItemPosition())
                if (mViewModel.getReviewAdapter().itemCount - recyclerLayoutManager2.findLastVisibleItemPosition() <= 2 && !mViewModel.isLoading2) {
                    mViewModel.isLoading2 = true
                    mViewModel.getMoreTab2List()
                }
            }
        })

        mViewModel.listUserMyPageReview.observe(this,
                androidx.lifecycle.Observer<ArrayList<MyPageReviewBase>> {
                    if(mViewModel.getReviewAdapter().items.size > 0) {
                        mViewModel.tab2EmptyViewVisible.set(false)
                    }else{
                        mViewModel.tab2EmptyViewVisible.set(true)
                    }
                }
        )

        EventBusHelper.mSubject.subscribe { data ->
            when (data.requestCode) {
                RequestCode.REVIEW_WRITE.flag -> {
                    mViewModel.reloadRecyclerViewAll()
                }
                RequestCode.REVIEW_MODIFY.flag -> {
                    mViewModel.reloadRecyclerMyReviewList()
                }
            }
        }

        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        mViewModel.reloadRecyclerView(object : OnSwipeRefreshResultListener{
            override fun onResultCallback() {
                this@MyPageReviewLayout.handler.postDelayed({
                    mBinding.swipeRefreshLayout.isRefreshing = false
                },200)
            }
        })
    }

    override fun onFocusView() { }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }
}