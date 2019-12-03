package io.temco.guhada.view.fragment.mypage

import android.view.View
import androidx.core.widget.NestedScrollView
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

    override fun getBaseTag(): String = MyPageCouponFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_coupon_enabled

    override fun init() {
        if (::mViewModel.isInitialized) {
            mBinding.swipeRefreshLayout.setOnRefreshListener(this)
            setScrollView()

            // TODO 쿠폰 등록
            mBinding.buttonMypagecouponAdd.setOnClickListener { ToastUtil.showMessage("유효하지 않은 쿠폰입니다.") }

            mBinding.recyclerviewMypagecouponEnabledlist.adapter = MyPageCouponAdapter().apply {
                this.mViewModel = this@MyPageCouponFragment.mViewModel
                this.mIsAvailable = this@MyPageCouponFragment.mIsAvailable
            }
            mBinding.viewModel = mViewModel
            mBinding.isAvailable = mIsAvailable
            mViewModel.getCoupons(mIsAvailable)
            mBinding.executePendingBindings()
        }
    }

    private fun setScrollView() {
        mBinding.scrollviewMypagecoupon.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (v != null && scrollY == (v.getChildAt(0)?.measuredHeight!! - v.measuredHeight)) {
                if (mIsAvailable) {
                    if (mViewModel.enabledCouponResponse.value?.empty == false)
                        mViewModel.getCoupons(mIsAvailable)
                } else {
                    if (mViewModel.disabledCouponResponse.value?.empty == false)
                        mViewModel.getCoupons(mIsAvailable)
                }
            }
        }
    }

    fun setCoupons(list: MutableList<Coupon>) {
        if (mBinding.recyclerviewMypagecouponEnabledlist.adapter == null) mBinding.recyclerviewMypagecouponEnabledlist.adapter = MyPageCouponAdapter()
        (mBinding.recyclerviewMypagecouponEnabledlist.adapter as MyPageCouponAdapter).setItems(list)
    }

    fun addCoupons(list: MutableList<Coupon>) {
        (mBinding.recyclerviewMypagecouponEnabledlist.adapter as MyPageCouponAdapter).addItems(list)
    }

    fun checkEmptyVisible() {
        val isEmpty =
                if (mIsAvailable) mViewModel.enabledCouponResponse.value?.totalElements ?: 0 > 0
                else mViewModel.disabledCouponResponse.value?.totalElements ?: 0 > 0

        if (isEmpty) {
            mBinding.imageviewMypagecouponEnabledlistEmpty.visibility = View.GONE
            mBinding.textviewMypagecouponEnabledlistEmpty.visibility = View.GONE
        } else {
            mBinding.imageviewMypagecouponEnabledlistEmpty.visibility = View.VISIBLE
            mBinding.textviewMypagecouponEnabledlistEmpty.visibility = View.VISIBLE
        }
    }

    fun checkMoreVisible() {
        val isLast =
                if (mIsAvailable) mViewModel.enabledCouponResponse.value?.last == true
                else mViewModel.disabledCouponResponse.value?.last == true

        if (isLast) {
            mBinding.viewMypagecouponMore.visibility = View.GONE
            mBinding.textviewMypagecouponMore.visibility = View.GONE
            mBinding.imageviewMypagecouponMore.visibility = View.GONE
        } else {
            mBinding.viewMypagecouponMore.visibility = View.VISIBLE
            mBinding.textviewMypagecouponMore.visibility = View.VISIBLE
            mBinding.imageviewMypagecouponMore.visibility = View.VISIBLE
        }
    }

    override fun onRefresh() {
        mViewModel.page = 1
        mViewModel.getCoupons(mIsAvailable)
        mBinding.swipeRefreshLayout.isRefreshing = false
    }
}