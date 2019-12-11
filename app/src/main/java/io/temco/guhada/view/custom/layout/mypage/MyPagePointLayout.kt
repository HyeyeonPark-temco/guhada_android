package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.util.LoadingIndicatorUtil
import io.temco.guhada.data.viewmodel.mypage.MyPagePointViewModel
import io.temco.guhada.databinding.CustomlayoutMypagePointBinding
import io.temco.guhada.view.adapter.mypage.MyPagePointAdapter
import io.temco.guhada.view.custom.CustomCalendarFilter
import io.temco.guhada.view.custom.layout.common.BaseListLayout

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 포인트 화면
 * @author Hyeyeon Park
 */
class MyPagePointLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypagePointBinding, MyPagePointViewModel>(context, attrs, defStyleAttr), CustomCalendarFilter.CustomCalendarListener, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mLoadingIndicatorUtil: LoadingIndicatorUtil

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_point
    override fun init() {
        // mBinding.lifecycleOwner = this
        mLoadingIndicatorUtil = LoadingIndicatorUtil(context)
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPagePointViewModel(context).apply {
            this.mPointHistory.observe(this@MyPagePointLayout, Observer {
                if (it.totalElements > 0) {
                    if (mBinding.recyclerviewMypagepoint.adapter == null)
                        mBinding.recyclerviewMypagepoint.adapter = MyPagePointAdapter(mViewModel)

                    (mBinding.recyclerviewMypagepoint.adapter as MyPagePointAdapter).addAllItems(it.content)
                    mViewModel.mEmptyVisible.set(false)
                } else {
                    mViewModel.mEmptyVisible.set(true)
                }

                hideIndicator()
            })
        }
        mBinding.recyclerviewMypagepoint.adapter = MyPagePointAdapter(mViewModel)
        mBinding.viewModel = mViewModel
        mViewModel.getPointSummary()

        initCalendar()
        setScrollView()
        onClickCheck()
        mBinding.executePendingBindings()
    }

    private fun setScrollView() {
        mBinding.scrollviewMypagepoint.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v != null && scrollY == (v.getChildAt(0)?.measuredHeight!! - v.measuredHeight)) {
                if (mBinding.recyclerviewMypagepoint.adapter?.itemCount ?: 0 > 0 && mViewModel.mPointHistory.value?.totalElements ?: 0 > 0 &&  mViewModel.mPointHistory.value?.last == false) {
                    showIndicator()
                    mViewModel.getPointHistories()
                }
            }
        }
    }

    private fun initCalendar() {
        mBinding.calendarfilterMypagepoint.mListener = this
        mBinding.calendarfilterMypagepoint.setPeriod(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.pos)
        mBinding.calendarfilterMypagepoint.setDate(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.date)
    }

    override fun onRefresh() {
        mViewModel.page = 1
        (mBinding.recyclerviewMypagepoint.adapter as MyPagePointAdapter).clearList()
        onClickCheck()
        mViewModel.getPointSummary()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    // CustomCalendarListener
    override fun onClickWeek() = onClickCheck()
    override fun onClickMonth() = onClickCheck()
    override fun onClickThreeMonth() = onClickCheck()
    override fun onClickYear() = onClickCheck()
    override fun onChangeDate(startDate: String, endDate: String) {
        mViewModel.page = 1
    }

    // startDate, endDate timeStamp로 변경 후 사용 예정 (2019.08.06)
    override fun onClickCheck(startDate: String, endDate: String) {
        onClickCheck()
    }

    private fun onClickCheck() {
        (mBinding.recyclerviewMypagepoint.adapter as MyPagePointAdapter).clearList()

        val startDate = mBinding.calendarfilterMypagepoint.startDate.split(".")
        val endDate = mBinding.calendarfilterMypagepoint.endDate.split(".")
        mViewModel.fromDate = "${startDate[0]}-${startDate[1]}-${startDate[2]}"
        mViewModel.toDate = "${endDate[0]}-${endDate[1]}-${endDate[2]}"
        mViewModel.getPointHistories()
    }

    private fun showIndicator() {
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.show()
    }

    private fun dismissIndicator() {
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.dismiss()
    }

    private fun hideIndicator() {
        if (::mLoadingIndicatorUtil.isInitialized) mLoadingIndicatorUtil.hide()
    }

    override fun onFocusView() {}
    override fun onReleaseView() {}
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() = dismissIndicator()
    override fun onStop() = dismissIndicator()
    override fun onDestroy() = dismissIndicator()
}