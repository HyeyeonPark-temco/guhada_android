package io.temco.guhada.view.activity

import io.temco.guhada.R
import io.temco.guhada.common.Type
import io.temco.guhada.data.viewmodel.CartViewModel
import io.temco.guhada.view.activity.base.BindActivity

class CartActivity : BindActivity<io.temco.guhada.databinding.ActivityCartBinding>() {
    private lateinit var mViewModel: CartViewModel

    override fun getBaseTag(): String = CartActivity::class.java.simpleName
    override fun getLayoutId(): Int = R.layout.activity_cart
    override fun getViewType(): Type.View = Type.View.CART
    override fun init() {
        mBinding.includeCartHeader.setOnClickBackButton { finish() }
        mBinding.includeCartHeader.title = resources.getString(R.string.cart_title)

        mViewModel = CartViewModel()
        mBinding.viewModel = mViewModel
        mBinding.executePendingBindings()
    }
}