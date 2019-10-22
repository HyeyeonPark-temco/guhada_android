package io.temco.guhada.view.custom.layout.mypage

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.Deal
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.mypage.MyPageBookMarkViewModel
import io.temco.guhada.databinding.CustomlayoutMypageBookmarkBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 찜한상품 화면
 *
 */
class MyPageBookMarkLayout constructor(
        context: Context,
        disposable : CompositeDisposable,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageBookmarkBinding, MyPageBookMarkViewModel>(context, disposable, attrs, defStyleAttr) , SwipeRefreshLayout.OnRefreshListener{

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_bookmark
    override fun init() {
        mViewModel = MyPageBookMarkViewModel(context, disposable)
        mBinding.viewModel = mViewModel

        mBinding.recyclerviewMypagebookmarkList.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)
        mBinding.recyclerviewMypagebookmarkList.setHasFixedSize(true)

        (mBinding.recyclerviewMypagebookmarkList.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerviewMypagebookmarkList.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true
        (mBinding.recyclerviewMypagebookmarkList.layoutManager as WrapGridLayoutManager).setSpanSizeLookup(
                object : GridLayoutManager.SpanSizeLookup(){
                    override fun getSpanSize(position: Int): Int {
                        return if(mViewModel.listData.value!![position].dealId > 0) 1 else 2
                    }
                }
        )
        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<Deal>> {
                    if(it.isNullOrEmpty()){
                        mViewModel.emptyViewVisible.set(true)
                    }else{
                        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "observe it ", it.size)
                        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "observe items ", mViewModel.getListAdapter().items.size)
                        mViewModel.emptyViewVisible.set(false)
                    }
                }
        )
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "onRefresh ", "init -----")
        mViewModel.reloadRecyclerView(object : OnSwipeRefreshResultListener {
            override fun onResultCallback() {
                if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "onResultCallback ", "init -----")
                this@MyPageBookMarkLayout.handler.postDelayed({
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
    override fun onDestroy() {
        if (CustomLog.flag) CustomLog.L("MyPageBookMarkLayout", "onDestroy ", "init -----")
        mBinding.viewModel?.destroyModel()

    }

}