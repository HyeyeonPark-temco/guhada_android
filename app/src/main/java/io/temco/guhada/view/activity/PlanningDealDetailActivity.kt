package io.temco.guhada.view.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.common.EventBusHelper
import io.temco.guhada.common.Flag
import io.temco.guhada.common.Type
import io.temco.guhada.common.util.CommonUtil
import io.temco.guhada.data.viewmodel.PlanningDealDetailViewModel
import io.temco.guhada.databinding.ActivityPlanningdealdetailBinding
import io.temco.guhada.view.WrapGridLayoutManager
import io.temco.guhada.view.activity.base.BindActivity

/**
 * 기획전 상세 화면
 *
 * @author park jungho
 */
class PlanningDealDetailActivity : BindActivity<ActivityPlanningdealdetailBinding>(), View.OnClickListener  {

    private lateinit var mViewModel : PlanningDealDetailViewModel
    private lateinit var recyclerLayoutManager : WrapGridLayoutManager
    private var planningDealDetailId = 0


    ////////////////////////////////////////////////////////////////////////////////
    override fun getBaseTag(): String = this::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_planningdealdetail
    override fun getViewType(): Type.View = Type.View.PLANNING_DEAL

    override fun init() {
        planningDealDetailId = intent?.extras?.getInt("planningDealDetailId") ?: -1
        if(planningDealDetailId < 0) finish()

        mViewModel = PlanningDealDetailViewModel(this)
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
        mBinding.recyclerView.layoutManager  = recyclerLayoutManager
        mBinding.recyclerView.adapter = mViewModel.adapter
        mBinding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*synchronized(this){
                    if (mViewModel.adapter.itemCount - recyclerLayoutManager.findLastVisibleItemPosition() == 0 && !mViewModel.isLoading) {
                        if(CustomLog.flag)CustomLog.L("PlanningDealDetailActivity","itemCount",mViewModel.adapter.itemCount,"findLastVisibleItemPosition",recyclerLayoutManager.findLastVisibleItemPosition())
                        mViewModel.isLoading = true
                        mViewModel.getDealListData(false)
                    }
                }*/
            }
        })

        mViewModel.getDealListData(true, false)
        mBinding.clickListener = this
        mBinding.setOnClickBackButton { finish() }

        setEvenBus()
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

    ////////////////////////////////////////////////////////////////////////////////
}

