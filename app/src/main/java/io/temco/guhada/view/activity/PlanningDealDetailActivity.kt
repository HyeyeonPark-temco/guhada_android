package io.temco.guhada.view.activity

import android.annotation.SuppressLint
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
import io.temco.guhada.common.Type
import io.temco.guhada.common.listener.OnCallBackListener
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.common.util.CustomLog
import io.temco.guhada.data.viewmodel.PlanningDealDetailViewModel
import io.temco.guhada.databinding.ActivityPlanningdealdetailBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.base.BindActivity
import io.temco.guhada.view.fragment.mypage.MyPageTabType

/**
 * 기획전 상세 화면
 *
 * @author park jungho
 */
class PlanningDealDetailActivity : BindActivity<ActivityPlanningdealdetailBinding>(), View.OnClickListener  {

    private lateinit var mViewModel : PlanningDealDetailViewModel
    private lateinit var recyclerLayoutManager : WrapGridLayoutManager

    private val INTERPOLATOR = FastOutSlowInInterpolator() // Button Animation

    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = this::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_planningdealdetail
    override fun getViewType(): Type.View = Type.View.PLANNING_DEAL

    override fun init() {

        mViewModel = PlanningDealDetailViewModel(this)
        mViewModel.planningDealDetailId = intent?.extras?.getInt("planningDealDetailId") ?: -1
        if(mViewModel.planningDealDetailId < 0) finish()
        else{
            mViewModel.imageBannerUrl = intent?.extras?.getString("url", "") ?: ""

            mViewModel.recyclerView = mBinding.recyclerView
            mBinding.recyclerView.setHasFixedSize(true)
            recyclerLayoutManager = WrapGridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false).apply {
                orientation = RecyclerView.VERTICAL
                recycleChildrenOnDetach = true
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return mViewModel.adapter.items[position].gridSpanCount
                    }
                }
            }

            mBinding.floating.bringToFront()

            mBinding.recyclerView.layoutManager  = recyclerLayoutManager
            mBinding.recyclerView.adapter = mViewModel.adapter
            mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
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
                    super.onScrolled(recyclerView, dx, dy)
                    synchronized(this){
                        if (mViewModel.adapter.itemCount - (recyclerLayoutManager.findLastVisibleItemPosition()+10) <= 0 && !mViewModel.isLoading && mViewModel.totalPage > mViewModel.currentPage) {
                            if(CustomLog.flag)CustomLog.L("PlanningDealDetailActivity","itemCount",mViewModel.adapter.itemCount,"findLastVisibleItemPosition",recyclerLayoutManager.findLastVisibleItemPosition())
                            if(CustomLog.flag)CustomLog.L("PlanningDealDetailActivity","mViewModel.totalPage",mViewModel.totalPage,"mViewModel.totalPage",mViewModel.currentPage)
                            mViewModel.isLoading = true
                            mViewModel.getPlanningDetail(false, null)
                        }
                    }
                }
            })


            mViewModel.getPlanningDetail(true, object : OnCallBackListener{
                override fun callBackListener(resultFlag: Boolean, value: Any) {
                    mBinding.textviewTitle.text = value.toString()
                }
            })
            mBinding.clickListener = this
            mBinding.setOnClickBackButton { finish() }

            setEvenBus()
        }
    }


    ////////////////////////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {
        when (v?.id) {
            // Top Menu
            R.id.image_side_menu -> CommonUtil.startMenuActivity(this@PlanningDealDetailActivity, Flag.RequestCode.SIDE_MENU)
            R.id.image_search -> CommonUtil.startSearchWordActivity(this@PlanningDealDetailActivity, null, true)
            R.id.image_shop_cart -> CommonUtil.startCartActivity(this@PlanningDealDetailActivity)
        }
    }


    override fun onResume() {
        super.onResume()
        /**
         * 상단 툴바 장바구니 뱃지
         * @author Hyeyeon Park
         * @since 2019.11.05
         */
        CommonUtil.getCartItemCount()
    }

    ////////////////////////////////////////////////////////////////////////////////

    @SuppressLint("CheckResult")
    private fun setEvenBus() {
        EventBusHelper.mSubject.subscribe { (requestCode, data) ->
            if (requestCode == Flag.RequestCode.CART_BADGE) {
                val count = Integer.parseInt(data!!.toString())
                if (count > 0) {
                    mBinding.textviewBadge.setVisibility(View.VISIBLE)
                    mBinding.textviewBadge.setText(count.toString() + "")
                } else {
                    mBinding.textviewBadge.setVisibility(View.GONE)
                }
            }
        }
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
    }

    private fun changeTopFloatingButton(isShow: Boolean) {
        changeTopFloatingButton(isShow, false)
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


    ////////////////////////////////////////////////////////////////////////////////
}

