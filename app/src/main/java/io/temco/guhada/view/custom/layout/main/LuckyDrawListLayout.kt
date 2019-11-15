package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import io.temco.guhada.common.Type
import io.temco.guhada.common.enum.TrackingEvent
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.common.util.TrackingUtil
import io.temco.guhada.data.viewmodel.main.LuckyDrawViewModel
import io.temco.guhada.databinding.CustomlayoutMainLuckydrawBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.adapter.main.LuckyDrawAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.main.HomeFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType

/**
 * @author park jungho
 * @since 2019.11.12
 *
 * 럭키 드로우
 *      타이머는 luckyDrawList의 첫번째 item의
 *      remainedTimeForStart 가 0보다 작고
 *      remainedTimeForEnd 가 0보다 클때 remainedTimeForEnd 의 남은시간(초)를 타이머 한다
 *
 *
 * 메인화면 타임딜
 *
 *
 * - 추가 작업 할 내용 : SwipeRefreshLayout 추가, 타임딜 이미지 bg 리스트 스크롤시 안보이게
 */
class LuckyDrawListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainLuckydrawBinding, LuckyDrawViewModel>(context, attrs, defStyleAttr) {
    var mHomeFragment: HomeFragment? = null

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation
    private var recentViewCount = -1

    override fun getBaseTag() = KidsListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_luckydraw
    override fun init() {
        mViewModel = LuckyDrawViewModel(context)
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
            loadLuckyDrawsData()
        }
        mBinding.viewModel = mViewModel

        TrackingUtil.sendKochavaEvent(TrackingEvent.MainEvent.View_Lucky_Event_Product.eventName)

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

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(CustomLog.flag)CustomLog.L("LuckyDrawListLayout","onScrolled dy",dy)
                if((dy > 0 && mBinding.layoutBottom.visibility == View.GONE) ||  (dy < 0 && mBinding.layoutBottom.visibility == View.VISIBLE)){
                    if(CustomLog.flag)CustomLog.L("LuckyDrawListLayout","onScrolled changeLastView",dy)
                    changeLastView(mBinding.layoutBottom, dy > 0, true)
                }
            }
        })


        mViewModel.adapter = LuckyDrawAdapter(mViewModel, mViewModel.listData)
        mBinding.recyclerView.adapter = mViewModel.adapter

        mBinding.buttonFloatingItem.layoutFloatingButtonBadge.setOnClickListener { view ->
            (context as MainActivity).mBinding.layoutContents.layoutPager.currentItem = 4
            (context as MainActivity).selectTab(4, false,true)
            EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.MYPAGE_MOVE, MyPageTabType.LAST_VIEW.ordinal))
        }

        mBinding.setClickLoginListener { CommonUtil.startLoginPage(this.context as Activity) }
        mBinding.setClickShareListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, Type.getWebUrl()+"event/luckydraw")
                type = "text/plain"
            }
            (context as Activity).startActivity(Intent.createChooser(sendIntent, "공유"))
        }
        //getRecentProductCount()
    }

    private fun scrollToTop(isSmooth: Boolean) {
        if (isSmooth) {
            mBinding.recyclerView.smoothScrollToPosition(0)
        } else {
            mBinding.recyclerView.scrollToPosition(0)
        }
    }

    // Floating Button
    private fun changeFloatingButtonLayout(isShow: Boolean) {
        changeTopFloatingButton(isShow)
        //changeItemFloatingButton(isShow)
    }

    private fun changeItemFloatingButton(isShow: Boolean) {
        changeItemFloatingButton(isShow, false)
    }

    private fun changeTopFloatingButton(isShow: Boolean) {
        changeTopFloatingButton(isShow, false)
    }

    private fun changeItemFloatingButton(isShow: Boolean, animate: Boolean) {
        /*if (CommonUtil.checkToken()) {
            if (recentViewCount > 0) {
                changeLastView(mBinding.buttonFloatingItem.root, isShow, animate)
            }
        } else {
            changeLastView(mBinding.buttonFloatingItem.root, false, false)
        }*/
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

    private fun loadLuckyDrawsData() {
        if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "loadTimeDealData onResume")
        mViewModel.adapter.clearRunnable()
        mViewModel.adapter.items.clear()
        mViewModel.adapter.notifyDataSetChanged()
        mViewModel.getLuckyDraws(object : OnCallBackListener {
            override fun callBackListener(resultFlag: Boolean, value: Any) {
                if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "onResume callBackListener")
                (mBinding.recyclerView.adapter as LuckyDrawAdapter).notifyDataSetChanged()
            }
        })
    }

    // viewpager의 화면 진입시
    override fun onFocusView() {
        if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "onFocusView")
        loadLuckyDrawsData()
    }

    // viewpager의 화면 해재시
    override fun onReleaseView() {
        if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "onReleaseView")
        mViewModel.adapter.clearRunnable()
    }

    override fun onStart() {}
    override fun onResume() {
        //setRecentProductCount()
        if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "onResume----")
        mViewModel.adapter.notifyDataSetChanged()
        /*if (CommonUtil.checkToken()) {
            mBinding.textviewBottomLogin.visibility = View.VISIBLE
        }else{
            mBinding.textviewBottomLogin.visibility = View.GONE
        }*/
        //loadTimeDealData()
    }

    override fun onPause() {
        if (CustomLog.flag) CustomLog.L("LuckyDrawListLayout", "onPause")
        mViewModel.adapter.clearRunnable()
    }



    // 외부 스키마 등으로 맨 위로 이동될때 사용함
    fun listScrollTop() {
        try{  mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true) }catch (e : Exception){
            if(CustomLog.flag)CustomLog.E(e)
        }
        scrollToTop(false)
    }

    override fun onStop() {}
    override fun onDestroy() {}
}