package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.model.point.PointHistory
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
 *
 */
class MyPagePointLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypagePointBinding, MyPagePointViewModel>(context, attrs, defStyleAttr), CustomCalendarFilter.CustomCalendarListener, SwipeRefreshLayout.OnRefreshListener {
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_point
    override fun init() {
        // mBinding.lifecycleOwner = this
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPagePointViewModel(context)
        mBinding.recyclerviewMypagepoint.adapter = MyPagePointAdapter(mViewModel)
        mBinding.viewModel = mViewModel
        mViewModel.getPointSummary()

        initCalendar()
        onClickCheck()
        mBinding.executePendingBindings()
    }

    private fun initCalendar() {
        mBinding.calendarfilterMypagepoint.mListener = this
        mBinding.calendarfilterMypagepoint.setPeriod(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.pos)
        mBinding.calendarfilterMypagepoint.setDate(CustomCalendarFilter.CalendarPeriod.THREE_MONTH.date)
    }

    override fun onRefresh() {
        mViewModel.page = 1
        onClickCheck()
        mViewModel.getPointSummary()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    // CustomCalendarListener
    override fun onClickWeek() = onClickCheck()

    override fun onClickMonth() = onClickCheck()
    override fun onClickThreeMonth() = onClickCheck()
    override fun onClickYear() = onClickCheck()
    override fun onChangeDate(startDate: String, fromDate: String) {
        mViewModel.page = 1
    }

    // startDate, endDate timeStamp로 변경 후 사용 예정 (2019.08.06)
    override fun onClickCheck(startDate: String, endDate: String) {

    }

    private fun onClickCheck() {
        val startDate = mBinding.calendarfilterMypagepoint.startDate.split(".")
        val endDate = mBinding.calendarfilterMypagepoint.endDate.split(".")
        mViewModel.fromDate = "${startDate[0]}-${startDate[1]}-${startDate[2]}"
        mViewModel.toDate = "${endDate[0]}-${endDate[1]}-${endDate[2]}"
        mViewModel.getPointHistories()
    }

    override fun onFocusView() { }
    override fun onReleaseView() { }
    override fun onStart() { }
    override fun onResume() { }
    override fun onPause() { }
    override fun onStop() { }
    override fun onDestroy() { }

    companion object {
        @JvmStatic
        @BindingAdapter("pointHistories")
        fun RecyclerView.bindPointHistories(list: MutableList<PointHistory.PointHistoryContent>?) {
            if (list != null && this.adapter != null)
                (this.adapter as MyPagePointAdapter).setItems(list)
        }
    }
}