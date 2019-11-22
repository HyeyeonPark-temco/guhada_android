package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.core.widget.NestedScrollView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnMainListListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.main.TimeDealListViewModel
import io.temco.guhada.databinding.CustomlayoutMainTimelistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.main.TimeDealListAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.main.HomeFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType

/**
 * @author park jungho
 * @since 2019.10.21
 *
 * @author Hyeyeon Park
 * @since 2019.10.23
 *
 * 메인화면 타임딜
 *
 *
 * - 추가 작업 할 내용 : SwipeRefreshLayout 추가, 타임딜 이미지 bg 리스트 스크롤시 안보이게
 */
class TimeDealListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainTimelistBinding, TimeDealListViewModel>(context, attrs, defStyleAttr) {
    var mHomeFragment: HomeFragment? = null

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation
    private var recentViewCount = -1

    lateinit var mainListListener : OnMainListListener

    override fun getBaseTag() = KidsListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_timelist
    override fun init() {
        mViewModel = TimeDealListViewModel(context)
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
            loadTimeDealData()
        }
        mBinding.viewModel = mViewModel

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = WrapGridLayoutManager(context as Activity, 2, LinearLayoutManager.VERTICAL, false)

        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).orientation = RecyclerView.VERTICAL
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).recycleChildrenOnDetach = true
        (mBinding.recyclerView.layoutManager as WrapGridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return mViewModel.listData[position].gridSpanCount
            }
        }

        mBinding.floating.bringToFront()
        /*mBinding.scrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            changeFloatingButtonLayout(scrollY > oldScrollY)
        }*/

        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                // super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(-1)) {
                    // Top
                    changeFloatingButtonLayout(false)
                } else if (!recyclerView.canScrollVertically(1)) {
                    // Bottom
                } else {
                    // Idle
                    changeFloatingButtonLayout(true)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) { }
        })

        mViewModel.adapter = TimeDealListAdapter(mViewModel, mViewModel.listData)
        mBinding.recyclerView.adapter = mViewModel.adapter

        mBinding.buttonFloatingItem.layoutFloatingButtonBadge.setOnClickListener { view ->
            (context as MainActivity).mBinding.layoutContents.layoutPager.currentItem = 4
            (context as MainActivity).selectTab(4, false,true)
            EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.MYPAGE_MOVE, MyPageTabType.LAST_VIEW.ordinal))
        }

        getRecentProductCount()
    }

    private fun scrollToTop(isSmooth: Boolean) {
        if (isSmooth) {
            mBinding.scrollView.smoothScrollTo(0,0)
        } else {
            mBinding.scrollView.scrollTo(0,0)
        }
    }

    // Floating Button
    private fun changeFloatingButtonLayout(isShow: Boolean) {
        changeTopFloatingButton(isShow)
        changeItemFloatingButton(isShow)
    }

    private fun changeItemFloatingButton(isShow: Boolean) {
        changeItemFloatingButton(isShow, false)
    }

    private fun changeTopFloatingButton(isShow: Boolean) {
        changeTopFloatingButton(isShow, false)
    }

    private fun changeItemFloatingButton(isShow: Boolean, animate: Boolean) {
        if (CommonUtil.checkToken()) {
            if (recentViewCount > 0) {
                changeLastView(mBinding.buttonFloatingItem.root, isShow, animate)
            }
        } else {
            changeLastView(mBinding.buttonFloatingItem.root, false, false)
        }
    }

    private fun changeTopFloatingButton(isShow: Boolean, animate: Boolean) {
        changeScaleView(mBinding.buttonFloatingTop.root, isShow, animate)
    }

    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     */
    private fun changeScaleView(v: View, isShow: Boolean, animate: Boolean) {
        if (isShow) {
            if (v.visibility != View.VISIBLE) {
                v.setOnClickListener {
                    try {
                        mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true)
                    } catch (e: Exception) {
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                    scrollToTop(false)
                }
                v.visibility = View.VISIBLE
                if (animate) {
                    showScaleAnimation(v)
                }
            }
        } else {
            if (v.visibility == View.VISIBLE) {
                v.setOnClickListener(null)
                if (animate) {
                    hideScaleAnimation(v)
                } else {
                    v.visibility = View.GONE
                }
            }
        }
    }


    /**
     * @editor park jungho
     * 19.08.01
     * scrollToTop Value -> false 로 변경
     */
    private fun changeLastView(v: View, isShow: Boolean, animate: Boolean) {
        if (isShow) {
            if (v.visibility != View.VISIBLE) {
                v.visibility = View.VISIBLE
                if (animate) {
                    showScaleAnimation(v)
                }
            }
        } else {
            if (v.visibility == View.VISIBLE) {
                if (animate) {
                    hideScaleAnimation(v)
                } else {
                    v.visibility = View.GONE
                }
            }
        }
    }

    private fun setRecentProductCount() {
        try {
            if (!CommonUtil.checkToken()) return
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        val count = value.toString()
                        mBinding.buttonFloatingItem.textviewFloatingCount.text = count
                        if (mBinding.buttonFloatingItem.textviewFloatingCount.text.toString().toInt() == 0) {
                            changeLastView(mBinding.buttonFloatingItem.root, false, false)
                        } else {
                            changeLastView(mBinding.buttonFloatingItem.root, true, false)
                        }
                    } catch (e: Exception) {
                        changeLastView(mBinding.buttonFloatingItem.root, false, false)
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                }
            })
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    private fun getRecentProductCount() {
        try {
            if (!CommonUtil.checkToken()) return
            CommonUtilKotlin.recentProductCount((context as MainActivity).getmDisposable(), (context as MainActivity).getmDb(), object : OnCallBackListener {
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    try {
                        recentViewCount = value.toString().toInt()
                        mBinding.buttonFloatingItem.textviewFloatingCount.text = value.toString()
                    } catch (e: Exception) {
                        if (CustomLog.flag) CustomLog.E(e)
                    }
                }
            })
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
    }

    private fun showScaleAnimation(v: View) {
        ViewCompat.animate(v)
                .scaleX(1.0f).scaleY(1.0f).alpha(1.0f)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(null)
                .start()
    }

    private fun hideScaleAnimation(v: View) {
        ViewCompat.animate(v)
                .scaleX(0.0f).scaleY(0.0f).alpha(0.0f)
                .setInterpolator(INTERPOLATOR)
                .withLayer()
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationStart(view: View) {}
                    override fun onAnimationCancel(view: View) {}
                    override fun onAnimationEnd(view: View) {
                        view.visibility = View.GONE
                    }
                })
                .start()
    }

    private fun loadTimeDealData() {
        if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "loadTimeDealData onResume")
        mViewModel.adapter.items.clear()
        mViewModel.adapter.notifyDataSetChanged()
        mViewModel.getTimeDealItem(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "onResume callBackListener")
                (mBinding.recyclerView.adapter as TimeDealListAdapter).notifyDataSetChanged()
            }
        })
    }

    // viewpager의 화면 진입시
    override fun onFocusView() {
        if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "onFocusView")
        loadTimeDealData()
    }

    // viewpager의 화면 해재시
    override fun onReleaseView() {
        if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "onReleaseView")
        mViewModel.adapter.clearRunnable()
    }

    override fun onStart() {}
    override fun onResume() {
        setRecentProductCount()
        if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "onResume")
        mViewModel.adapter.notifyDataSetChanged()
        //loadTimeDealData()
    }

    override fun onPause() {
        if (CustomLog.flag) CustomLog.L("TimeDealListLayout", "onPause")
        mViewModel.adapter.clearRunnable()
    }


    fun listScrollTop() {
        try{  mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true) }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        scrollToTop(false)
    }

    override fun onStop() {}
    override fun onDestroy() {}
}