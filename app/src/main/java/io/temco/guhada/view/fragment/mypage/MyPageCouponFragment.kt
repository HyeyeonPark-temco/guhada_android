package io.temco.guhada.view.fragment.mypage

import android.view.View
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
class MyPageCouponFragment : BaseFragment<io.temco.guhada.databinding.FragmentCouponEnabledBinding>() {
    lateinit var mViewModel: MyPageCouponViewModel
    var mIsAvailable = false

    override fun getBaseTag(): String = MyPageCouponFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_coupon_enabled

    override fun init() {
        if (::mViewModel.isInitialized) {
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
}