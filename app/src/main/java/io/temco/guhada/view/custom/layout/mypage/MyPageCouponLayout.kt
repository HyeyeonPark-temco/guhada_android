package io.temco.guhada.view.custom.layout.mypage

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.tabs.TabLayout
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.mypage.MyPageCouponViewModel
import io.temco.guhada.databinding.CustomlayoutMypageCouponBinding
import io.temco.guhada.view.adapter.base.BaseFragmentPagerAdapter
import io.temco.guhada.view.custom.layout.common.BaseListLayout
import io.temco.guhada.view.fragment.mypage.MyPageCouponFragment

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
) : BaseListLayout<CustomlayoutMypageCouponBinding, MyPageCouponViewModel>(context, attrs, defStyleAttr) {
    private lateinit var mFragmentAdapter: BaseFragmentPagerAdapter
    private lateinit var mCouponFragment: MyPageCouponFragment
    private lateinit var mDisabledCouponFragment: MyPageCouponFragment

    override fun getBaseTag() = this::class.simpleName.toString()
    override fun getLayoutId() = R.layout.customlayout_mypage_coupon
    override fun init() {
        initViewModel()
        initTabs()
        initViewPager()


        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }

    private fun initViewModel() {
        mViewModel = MyPageCouponViewModel(context)
        mViewModel.enabledCouponResponse.observe(this, Observer {
            setEnabledCouponTabText(totalElements = mViewModel.enabledCouponResponse.value?.totalElements
                    ?: 0)
            updateCouponFragment(isAvailable = true, list = it.content)
        })
        mViewModel.disabledCouponResponse.observe(this, Observer { updateCouponFragment(isAvailable = false, list = it.content) })
    }

    private fun setEnabledCouponTabText(totalElements: Int) {
        val text = resources.getString(R.string.mypagecoupon_tab_enabled, totalElements)
        if (mBinding.tablayoutMypagecoupon.getTabAt(0)?.text != text) mBinding.tablayoutMypagecoupon.getTabAt(0)?.text = text
    }

    private fun updateCouponFragment(isAvailable: Boolean, list: MutableList<Coupon>) {
        val fragment = if (isAvailable) mCouponFragment else mDisabledCouponFragment
        if (mViewModel.page > 1) fragment.addCoupons(list)
        else fragment.setCoupons(list)

        fragment.checkEmptyVisible()
        fragment.checkMoreVisible()
    }

    private fun initViewPager() {
        mFragmentAdapter = BaseFragmentPagerAdapter((context as FragmentActivity).supportFragmentManager).apply {
            this.mTabTitles = mutableListOf(resources.getString(R.string.mypagecoupon_tab_enabled_temp), resources.getString(R.string.mypagecoupon_tab_disabled))
        }
        mCouponFragment = MyPageCouponFragment().apply {
            mIsAvailable = true
            mViewModel = this@MyPageCouponLayout.mViewModel
        }
        mDisabledCouponFragment = MyPageCouponFragment().apply {
            mIsAvailable = false
            mViewModel = this@MyPageCouponLayout.mViewModel
        }
        mFragmentAdapter.addFragment(mCouponFragment)
        mFragmentAdapter.addFragment(mDisabledCouponFragment)
        mBinding.viewpagerMypagecoupon.adapter = mFragmentAdapter
    }

    private fun initTabs() {
        mBinding.tablayoutMypagecoupon.setupWithViewPager(mBinding.viewpagerMypagecoupon)
    }

    override fun onFocusView() {}

    override fun onReleaseView() {}
    override fun onStart() {}
    override fun onResume() {}
    override fun onPause() {}
    override fun onStop() {}
    override fun onDestroy() {}
}