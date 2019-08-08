package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.mypage.MyPageCouponViewModel
import io.temco.guhada.databinding.CustomlayoutMypageCouponBinding
import io.temco.guhada.view.adapter.base.BaseFragmentPagerAdapter
import io.temco.guhada.view.adapter.mypage.MyPageCouponAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.mypage.MyPageEnabledCouponFragment

/**
 * 19.07.22
 * @author park jungho
 *
 * 마이페이지 - 쿠폰 화면
 * @author  Hyeyeon Park
 * @since   2019.08..08
 *
 */
class MyPageCouponLayout constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseListLayout<CustomlayoutMypageCouponBinding, MyPageCouponViewModel>(context, attrs, defStyleAttr), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var mFragmentAdapter: BaseFragmentPagerAdapter
    private lateinit var mEnabledCouponFragment: MyPageEnabledCouponFragment
    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_coupon
    override fun init() {
        mBinding.swipeRefreshLayout.setOnRefreshListener(this)
        mViewModel = MyPageCouponViewModel(context)

        mFragmentAdapter = BaseFragmentPagerAdapter((context as FragmentActivity).supportFragmentManager)
        mEnabledCouponFragment = MyPageEnabledCouponFragment().apply { mViewModel = this@MyPageCouponLayout.mViewModel }
        mFragmentAdapter.addFragment(mEnabledCouponFragment)
        mBinding.viewpagerMypagecoupon.adapter = mFragmentAdapter

        mViewModel.getCoupons()
        mBinding.executePendingBindings()
    }

    override fun onRefresh() {
        mViewModel.getCoupons()
        mBinding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onFocusView() {

    }

    override fun onStart() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onStop() {

    }

    override fun onDestroy() {

    }
}