package io.temco.guhada.view.fragment.mypage

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import io.temco.guhada.R
import io.temco.guhada.data.model.coupon.Coupon
import io.temco.guhada.data.viewmodel.mypage.MyPageCouponViewModel
import io.temco.guhada.view.adapter.mypage.MyPageCouponAdapter
import io.temco.guhada.view.fragment.base.BaseFragment

class MyPageEnabledCouponFragment : BaseFragment<io.temco.guhada.databinding.FragmentCouponEnabledBinding>() {
    lateinit var mViewModel: MyPageCouponViewModel

    override fun getBaseTag(): String = MyPageEnabledCouponFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_coupon_enabled

    override fun init() {
        if (::mViewModel.isInitialized) {
            mBinding.recyclerviewMypagecouponEnabledlist.adapter = MyPageCouponAdapter()
            mBinding.viewModel = mViewModel
            mBinding.executePendingBindings()
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("coupons")
        fun RecyclerView.bindCoupons(list: MutableList<Coupon>?) {
            if (list != null)
                (this.adapter as MyPageCouponAdapter).setItems(list)
        }
    }
}