package io.temco.guhada.view.fragment.mypage

import io.temco.guhada.R
import io.temco.guhada.view.fragment.base.BaseFragment

class MyPageEnabledCouponFragment : BaseFragment<io.temco.guhada.databinding.FragmentCouponEnabledBinding>() {
    override fun getBaseTag(): String = MyPageEnabledCouponFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_coupon_enabled

    override fun init() {

    }
}