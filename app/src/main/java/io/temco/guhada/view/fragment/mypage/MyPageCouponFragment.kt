package io.temco.guhada.view.fragment.mypage

import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.common.util.ToastUtil
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.mypage.MyPageCouponViewModel
import io.temco.guhada.view.adapter.mypage.MyPageCouponAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

/**
 * 마이페이지 - 쿠폰 리스트
 * @author Hyeyeon Park
 * @since 2019.08.08
 */
class MyPageCouponFragment : BaseFragment<io.temco.guhada.databinding.FragmentCouponEnabledBinding>(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var mViewModel: MyPageCouponViewModel
    var mIsAvailable = false
    var mSetTabTitleTask: (text: String) -> Unit = {}

    override fun getBaseTag(): String = MyPageCouponFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_coupon_enabled

    override fun init() {
        if (::mViewModel.isInitialized) {
            mBinding.recyclerviewMypagecouponEnabledlist.adapter = MyPageCouponAdapter().apply {
                this.mViewModel = this@MyPageCouponFragment.mViewModel
                this.mIsAvailable = this@MyPageCouponFragment.mIsAvailable
            }
            mViewModel.enabledCouponResponse.observe(this, Observer {
                updateCouponList(it.content)
                mSetTabTitleTask(resources.getString(R.string.mypagecoupon_tab_enabled, it.totalElements))
            })
            mViewModel.disabledCouponResponse.observe(this, Observer { updateCouponList(it.content) })
            mViewModel.getCoupons(mIsAvailable)
            mBinding.viewModel = mViewModel
            setScrollView()
        }

        // TODO 쿠폰 등록
        mBinding.buttonMypagecouponAdd.setOnClickListener { ToastUtil.showMessage("유효하지 않은 쿠폰입니다.") }
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mBinding.isAvailable = mIsAvailable

        mBinding.executePendingBindings()
    }

    private fun setScrollView() {
        mBinding.scrollviewMypagecoupon.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v != null && scrollY == (v.getChildAt(0)?.measuredHeight!! - v.measuredHeight)) {
                val isLast = if (mIsAvailable) mViewModel.enabledCouponResponse.value?.last else mViewModel.disabledCouponResponse.value?.last
                if (isLast == false) mViewModel.getCoupons(mIsAvailable)
            }
        }
    }

    private fun setCoupons(list: MutableList<Coupon>) {
        if (mBinding.recyclerviewMypagecouponEnabledlist.adapter == null) mBinding.recyclerviewMypagecouponEnabledlist.adapter = MyPageCouponAdapter()
        (mBinding.recyclerviewMypagecouponEnabledlist.adapter as MyPageCouponAdapter).setItems(list)
    }

    private fun addCoupons(list: MutableList<Coupon>) = (mBinding.recyclerviewMypagecouponEnabledlist.adapter as MyPageCouponAdapter).addItems(list)

    private fun checkEmptyVisible() {
        val isNotEmpty =
                if (mIsAvailable) mViewModel.enabledCouponResponse.value?.totalElements ?: 0 > 0
                else mViewModel.disabledCouponResponse.value?.totalElements ?: 0 > 0

        mBinding.imageviewMypagecouponEnabledlistEmpty.visibility = if (isNotEmpty) View.GONE else View.VISIBLE
        mBinding.textviewMypagecouponEnabledlistEmpty.visibility = if (isNotEmpty) View.GONE else View.VISIBLE
    }

    private fun updateCouponList(list: MutableList<Coupon>) {
        if (::mViewModel.isInitialized && mViewModel.page > 1) addCoupons(list)
        else setCoupons(list)
        checkEmptyVisible()
    }

    override fun onRefresh() {
        if(::mViewModel.isInitialized){
            mViewModel.page = 0
            mViewModel.getCoupons(mIsAvailable)
        }
        mBinding.swipeRefreshLayout.isRefreshing = false
    }
}