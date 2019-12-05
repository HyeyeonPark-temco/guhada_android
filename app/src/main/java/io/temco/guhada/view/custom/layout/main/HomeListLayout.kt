package io.temco.guhada.view.custom.layout.main

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.EventBusData
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.listener.OnMainCustomLayoutListener
import io.temco.guhada.common.listener.OnMainListListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CommonUtilKotlin
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.model.main.HomeDeal
import io.temco.guhada.data.model.main.MainBanner
import io.temco.guhada.data.viewmodel.main.HomeListViewModel
import io.temco.guhada.databinding.CustomlayoutMainHomelistBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.MainActivity
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.main.HomeFragment
import io.temco.guhada.view.fragment.mypage.MyPageTabType


class HomeListLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMainHomelistBinding, HomeListViewModel>(context, attrs, defStyleAttr), OnMainCustomLayoutListener {
    var mHomeFragment: HomeFragment? = null

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation
    private var recentViewCount = -1

    lateinit var mainListListener: OnMainListListener

    override fun getBaseTag() = HomeListLayout::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_main_homelist
    override fun init() {
        mViewModel = HomeListViewModel(context)
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

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {}
        })

        mBinding.buttonFloatingItem.layoutFloatingButtonBadge.setOnClickListener { view ->
            (context as MainActivity).mBinding.layoutContents.layoutPager.currentItem = 4
            (context as MainActivity).selectTab(4, false, true)
            EventBusHelper.sendEvent(EventBusData(Flag.RequestCode.MYPAGE_MOVE, MyPageTabType.LAST_VIEW.ordinal))
        }

        mBinding.buttonFloatingKakaochat.buttonFloatingKakao.setOnClickListener {
            CommonUtilKotlin.openKakaoChat(context as Activity)
        }
        getRecentProductCount()
    }


    private fun scrollToTop(isSmooth: Boolean) {
        if (isSmooth) {
            mBinding.recyclerView.smoothScrollToPosition(0)
        } else {
            mBinding.recyclerView.scrollToPosition(0)
        }
        mainListListener.requestDataList(0, "scrollToTop")
    }

    // Floating Button
    private fun changeFloatingButtonLayout(isShow: Boolean) {
        changeTopFloatingButton(isShow)
        changeItemFloatingButton(isShow)
        changeKakaoChatButton(isShow)
    }

    private fun changeItemFloatingButton(isShow: Boolean) {
        changeItemFloatingButton(isShow, false)
    }

    private fun changeTopFloatingButton(isShow: Boolean) {
        changeTopFloatingButton(isShow, false)
    }

    private fun changeKakaoChatButton(isShow: Boolean) {
        changeKakaoChatButton(isShow, false)
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

    private fun changeKakaoChatButton(isShow: Boolean, animate: Boolean) {
        val view = mBinding.buttonFloatingKakaochat.root
        if (isShow) {
            if (animate) showScaleAnimation(view)
            else view.visibility = View.VISIBLE
        } else {
            if (animate) hideScaleAnimation(view)
            else view.visibility = View.GONE
        }
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
                    listScrollTop()
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

    fun setData(premiumData: HomeDeal, bestData: HomeDeal, mainBanner: ArrayList<MainBanner>) {
        mViewModel.premiumData = premiumData
        mViewModel.bestData = bestData
        mViewModel.mainBanner = mainBanner
        mViewModel.setListData()
        if (CustomLog.flag) CustomLog.L("HomeListLayout", "mainBanner", mViewModel.mainBanner)
    }

    fun listScrollTop() {
        try {
            mHomeFragment?.getmBinding()?.layoutAppbar?.setExpanded(true)
        } catch (e: Exception) {
            if (CustomLog.flag) CustomLog.E(e)
        }
        scrollToTop(false)
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
                        recentViewCount = count.toInt()
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

    override fun updateDataList(tabIndex: Int, type: String) {
        if (CustomLog.flag) CustomLog.L("HomeListLayout", "updateDataList", "tabIndex", "type", type)

    }

    override fun loadNewInDataList(tabIndex: Int, value: HomeDeal) {
        if (CustomLog.flag) CustomLog.L("HomeListLayout", "loadNewInDataList", "tabIndex")
        mViewModel.newInData = value
        mViewModel.setNewInData()
        if (CustomLog.flag) CustomLog.L("HomeFragment", "newInData kidsList size", mViewModel.newInData?.kidsList!!.size)
        if (CustomLog.flag) CustomLog.L("HomeFragment", "newInData menList size", mViewModel.newInData?.menList!!.size)
        if (CustomLog.flag) CustomLog.L("HomeFragment", "newInData womenList size", mViewModel.newInData?.womenList!!.size)
        if (CustomLog.flag) CustomLog.L("HomeFragment", "newInData allList size", mViewModel.newInData?.allList!!.size)
    }

    override fun onFocusView() {
        mViewModel.getListAdapter().notifyDataSetChanged()
    }

    override fun onReleaseView() {
        mViewModel.getListAdapter().clearRunnable()
    }

    override fun onStart() {}
    override fun onResume() {
        mViewModel.getListAdapter().notifyDataSetChanged()
        setRecentProductCount()
    }

    override fun onPause() {
        mViewModel.getListAdapter().clearRunnable()
    }

    override fun onStop() {}
    override fun onDestroy() {}

    fun getViewModel() = mViewModel

}