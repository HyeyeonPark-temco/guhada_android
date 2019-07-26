package io.temco.guhada.view.custom.layout.mypage

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import io.temco.guhada.R
import io.temco.guhada.common.listener.OnSwipeRefreshResultListener
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.product.Product
import io.temco.guhada.data.viewmodel.mypage.MyPageRecentViewModel
import io.temco.guhada.databinding.CustomlayoutMypageRecentBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import kotlinx.android.synthetic.main.customlayout_mypage_recent.view.*

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 최근본상품 화면
 *
 */
class MyPageRecentLayout constructor(
        context: Context,
        disposable : CompositeDisposable,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageRecentBinding, MyPageRecentViewModel>(context, disposable, attrs, defStyleAttr) , SwipeRefreshLayout.OnRefreshListener{

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_recent
    override fun init() {
        mViewModel = MyPageRecentViewModel(context, disposable)
        mBinding.viewModel = mViewModel

        recyclerview_mypagerecentlayout_list.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)
        recyclerview_mypagerecentlayout_list.setHasFixedSize(true)

        (recyclerview_mypagerecentlayout_list.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (recyclerview_mypagerecentlayout_list.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true

        mViewModel.listData.observe(this,
                androidx.lifecycle.Observer<ArrayList<Product>> {
                    mViewModel.getListAdapter().notifyDataSetChanged()
                }
        )
        mViewModel.totalItemSize.observe(this,androidx.lifecycle.Observer<Int> {
            var sizeTxt = " " + it.toString()
            mBinding.textMypagerecentTotal.setText(sizeTxt)
            if(it > 0){
                mBinding.recyclerviewMypagerecentlayoutList.visibility = View.VISIBLE
                mBinding.linearlayoutMypagerecentlayoutNoitem.visibility = View.GONE
            }else{
                mBinding.recyclerviewMypagerecentlayoutList.visibility = View.GONE
                mBinding.linearlayoutMypagerecentlayoutNoitem.visibility = View.VISIBLE
            }
        })
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onRefresh ", "init -----")
        mViewModel.reloadRecyclerView(object : OnSwipeRefreshResultListener{
            override fun onResultCallback() {
                if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onResultCallback ", "init -----")
                this@MyPageRecentLayout.handler.postDelayed({
                    mBinding.swipeRefreshLayout.isRefreshing = false
                },200)
            }
        })
    }

    override fun onFocusView() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onFocusView ", "init -----")

    }

    override fun onStart() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onStart ", "init -----")

    }

    override fun onResume() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onResume ", "init -----")

    }

    override fun onPause() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onPause ", "init -----")

    }

    override fun onStop() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onStop ", "init -----")

    }

    override fun onDestroy() {
        if (CustomLog.flag) CustomLog.L("MyPageRecentLayout", "onDestroy ", "init -----")
        mBinding.viewModel?.destroyModel()

    }

}